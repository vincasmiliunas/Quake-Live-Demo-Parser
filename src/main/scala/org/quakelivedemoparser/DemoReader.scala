package org.quakelivedemoparser

import java.nio.{ByteBuffer, ByteOrder}
import java.nio.channels.ReadableByteChannel

class DemoReader(channel: ReadableByteChannel) extends DemoStream {
  val logger = org.slf4j.LoggerFactory.getLogger(classOf[DemoReader].getSimpleName)
  var prev: Option[DemoEvent] = None
  var bit_reader: BitReader = null
  var state_reader: StateReader = null

  def read_block(length: Int): Option[ByteBuffer] = {
    val buffer = ByteBuffer.allocateDirect(length)
    buffer.order(ByteOrder.LITTLE_ENDIAN)
    while (buffer.remaining > 0) {
      val ret = channel.read(buffer)
      if (ret == -1 && buffer.position == 0) {
        return None
      } else if (ret == -1) {
        throw new IllegalStateException("Failed to read block content")
      }
    }
    buffer.flip
    Some(buffer)
  }

  def read_int: Option[Int] = {
    val ret = read_block(BitBuffer.BYTES_IN_INT)
    if (ret.isDefined) {
      Some(ret.get.getInt)
    } else {
      None
    }
  }

  def read_block: Option[ByteBuffer] = {
    val ret = read_int.get
    if (ret == -1) {
      None
    } else {
      Some(read_block(ret).get)
    }
  }

  def start_stream: Option[DemoEvent] = {
    Some(new StartDemoEvent)
  }

  def stop_stream(input: EndDemoEvent): Option[DemoEvent] = {
    None
  }

  def block_loop(input: StartDemoEvent): Option[DemoEvent] = {
    val id = read_int
    if (id.isEmpty) {
      return Some(new EndDemoEvent)
    }
    val block = read_block
    if (block.isEmpty) {
      return Some(new EndDemoEvent)
    }
    val feed = new BitBufferFeed(block.get)
    bit_reader = new BitReader(feed)
    state_reader = new StateReader(bit_reader)
    val code = bit_reader.read_int
    Some(new StartMessageEvent(input, id.get, code))
  }

  def message_loop(input: StartMessageEvent): Option[DemoEvent] = {
    val ret = bit_reader.read_byte
    ret match {
      case 1 => message_loop(input)
      case 2 =>
        val id = bit_reader.read_int
        return Some(new StartGamestateEvent(input, id))
      case 5 =>
        val id = bit_reader.read_int
        val str = bit_reader.read_string
        return Some(new MessageCommandEvent(input, id, str))
      case 7 =>
        val time = bit_reader.read_int
        val delta = bit_reader.read_byte
        val flags = bit_reader.read_byte
        val blob_len = bit_reader.read_byte
        val blob = bit_reader.read_blob(blob_len)
        val player = state_reader.read_player
        return Some(new StartSnapshotEvent(input, time, delta, flags, blob, player))
      case 8 =>
        return Some(new EndMessageEvent(input.parent, input.id))
      case _ =>
        throw new IllegalStateException("Invalid operation code %d".format(ret));
    }
  }

  def end_message(input: EndMessageEvent): Option[DemoEvent] = {
    block_loop(input.parent)
  }

  def gamestate_loop(input: StartGamestateEvent): Option[DemoEvent] = {
    val ret = bit_reader.read_byte
    ret match {
      case 3 =>
        val id = bit_reader.read_short
        val str = bit_reader.read_string
        return Some(new BaselineConfigEvent(input, id, str))
      case 4 =>
        val id = bit_reader.read_bits(10)
        val ret = state_reader.read_entity
        return Some(new BaselineEntityEvent(input, id, ret.get))
      case 8 =>
        val client = bit_reader.read_int
        val checksum = bit_reader.read_int
        return Some(new EndGamestateEvent(input.parent, input.id, client, checksum))
      case _ =>
        throw new IllegalStateException("Invalid operation code %d".format(ret));
    }
  }

  def end_gamestate(input: EndGamestateEvent): Option[DemoEvent] = {
    message_loop(input.parent)
  }

  def snapshot_loop(input: StartSnapshotEvent): Option[DemoEvent] = {
    val id = bit_reader.read_bits(10)
    if (id == 1023) {
      return Some(new EndSnapshotEvent(input.parent, input.time))
    }
    val ret = state_reader.read_entity
    if (ret.isDefined) {
      Some(new SnapshotEntityEvent(input, id, ret.get))
    } else {
      Some(new SnapshotEntityRemovedEvent(input, id))
    }
  }

  def end_snapshot(input: EndSnapshotEvent): Option[DemoEvent] = {
    message_loop(input.parent)
  }

  def next: Option[DemoEvent] = {
    prev = prev match {
      case None => start_stream
      case Some(input: StartDemoEvent) => block_loop(input)
      case Some(input: EndDemoEvent) => stop_stream(input)
      case Some(input: StartMessageEvent) => message_loop(input)
      case Some(input: MessageCommandEvent) => message_loop(input.parent)
      case Some(input: EndMessageEvent) => end_message(input)
      case Some(input: StartGamestateEvent) => gamestate_loop(input)
      case Some(input: BaselineConfigEvent) => gamestate_loop(input.parent)
      case Some(input: BaselineEntityEvent) => gamestate_loop(input.parent)
      case Some(input: EndGamestateEvent) => end_gamestate(input)
      case Some(input: StartSnapshotEvent) => snapshot_loop(input)
      case Some(input: SnapshotEntityEvent) => snapshot_loop(input.parent)
      case Some(input: SnapshotEntityRemovedEvent) => snapshot_loop(input.parent)
      case Some(input: EndSnapshotEvent) => end_snapshot(input)
    }
    prev
  }
}
