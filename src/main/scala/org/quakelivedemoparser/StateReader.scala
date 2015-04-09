package org.quakelivedemoparser

class StateReader(bit_reader: BitReader) {
  val logger = org.slf4j.LoggerFactory.getLogger(classOf[StateReader].getSimpleName)

  def read_main[T](input: () => T): T = {
    if (bit_reader.read_bit == 0) 0.asInstanceOf[T] else input()
  }

  def read_bits(count: Int): Int = read_main(() => { bit_reader.read_bits(count) })

  def read_byte: Int = read_main(bit_reader.read_byte _)

  def read_short: Int = read_main(bit_reader.read_short _)

  def read_int: Int = read_main(bit_reader.read_int _)
  
  def read_float: Float = {
    if (bit_reader.read_bit == 0) 0 else bit_reader.read_float
  }

 def read_values(result: Array[Option[Int]], read_value: () => Int): Unit = {
    if (bit_reader.read_bit == 0) {
      return
    }
    val bits = bit_reader.read_short
    for (i <- 0 until 16) {
      val tmp = bits & (1 << i)
      if (tmp != 0) {
        result(i) = Some(read_value())
      }
    }
  }

  def read_player: Player = {
    val result = new Player

    val readers: Array[() => Unit] = Array(
      () => { result.time = Some(bit_reader.read_int) },
      () => { result.origin(0) = Some(bit_reader.read_float) },
      () => { result.origin(1) = Some(bit_reader.read_float) },
      () => { result.misc(0) = Some(bit_reader.read_byte) },
      () => { result.velocity(0) = Some(bit_reader.read_float) },
      () => { result.velocity(1) = Some(bit_reader.read_float) },
      () => { result.view(1) = Some(bit_reader.read_float) },
      () => { result.view(0) = Some(bit_reader.read_float) },
      () => { result.weapon(2) = Some(bit_reader.read_signed_short) },
      () => { result.origin(2) = Some(bit_reader.read_float) },
      () => { result.velocity(2) = Some(bit_reader.read_float) },
      () => { result.animations(1)(1) = Some(bit_reader.read_byte) },
      () => { result.movement(2) = Some(bit_reader.read_signed_short) },
      () => { result.event(0) = Some(bit_reader.read_short) },
      () => { result.animations(0)(0) = Some(bit_reader.read_byte) },
      () => { result.movement(0) = Some(bit_reader.read_bits(4)) },
      () => { result.events(0) = Some(bit_reader.read_byte) },
      () => { result.animations(1)(0) = Some(bit_reader.read_byte) },
      () => { result.events(1) = Some(bit_reader.read_byte) },
      () => { result.movement(1) = Some(bit_reader.read_short) },
      () => { result.entities(0) = Some(bit_reader.read_bits(10)) },
      () => { result.weapon(1) = Some(bit_reader.read_bits(4)) },
      () => { result.entity(0) = Some(bit_reader.read_short) },
      () => { result.external(0) = Some(bit_reader.read_bits(10)) },
      () => { result.misc(2) = Some(bit_reader.read_short) },
      () => { result.misc(4) = Some(bit_reader.read_short) },
      () => { result.delta(1) = Some(bit_reader.read_short) },
      () => { result.external(1) = Some(bit_reader.read_byte) },
      () => { result.misc(5) = Some(bit_reader.read_signed_byte) },
      () => { result.damage(1) = Some(bit_reader.read_byte) },
      () => { result.damage(3) = Some(bit_reader.read_byte) },
      () => { result.damage(2) = Some(bit_reader.read_byte) },
      () => { result.damage(0) = Some(bit_reader.read_byte) },
      () => { result.misc(2) = Some(bit_reader.read_byte) },
      () => { result.movement(3) = Some(bit_reader.read_byte) },
      () => { result.delta(0) = Some(bit_reader.read_short) },
      () => { result.delta(2) = Some(bit_reader.read_short) },
      () => { result.animations(0)(1) = Some(bit_reader.read_bits(12)) },
      () => { result.event(1) = Some(bit_reader.read_byte) },
      () => { result.event(2) = Some(bit_reader.read_byte) },
      () => { result.client = Some(bit_reader.read_byte) },
      () => { result.weapon(0) = Some(bit_reader.read_bits(5)) },
      () => { result.view(2) = Some(bit_reader.read_float) },
      () => { result.grapple(0) = Some(bit_reader.read_float) },
      () => { result.grapple(1) = Some(bit_reader.read_float) },
      () => { result.grapple(2) = Some(bit_reader.read_float) },
      () => { result.entities(1) = Some(bit_reader.read_bits(10)) },
      () => { result.misc(3) = Some(bit_reader.read_short) }
    )

    val count = bit_reader.read_byte
    for (i <- 0 until count) {
      if (bit_reader.read_bit == 1) {
        readers(i)()
      }
    }

    if (bit_reader.read_bit == 1) {
      read_values(result.vitals, bit_reader.read_signed_short _)
      read_values(result.attributes, bit_reader.read_short _)
      read_values(result.ammunition, bit_reader.read_short _)
      read_values(result.powerups, bit_reader.read_int _)
    }

    result
  }

  def read_entity: Option[Entity] = {
    if (bit_reader.read_bit == 1) {
      return None
    }
    
    val result = new Entity
    if (bit_reader.read_bit == 0) {
      return Some(result)
    }
    
    val readers: Array[() => Unit] = Array(
      () => { result.trajectories(0).time = Some(read_int) },
      () => { result.trajectories(0).base(0) = Some(read_float) },
      () => { result.trajectories(0).base(1) = Some(read_float) },
      () => { result.trajectories(0).delta(0) = Some(read_float) },
      () => { result.trajectories(0).delta(1) = Some(read_float) },
      () => { result.trajectories(0).base(2) = Some(read_float) },
      () => { result.trajectories(1).base(1) = Some(read_float) },
      () => { result.trajectories(0).delta(2) = Some(read_float) },
      () => { result.trajectories(1).base(0) = Some(read_float) },
      () => { result.trajectories(0).gravity = Some(read_int) },
      () => { result.events(0) = Some(read_bits(10)) },
      () => { result.angles(1)(1) = Some(read_float) },
      () => { result.entity(1) = Some(read_byte) },
      () => { result.animations(0) = Some(read_byte) },
      () => { result.events(1) = Some(read_byte) },
      () => { result.animations(1) = Some(read_byte) },
      () => { result.entities(0) = Some(read_bits(10)) },
      () => { result.trajectories(0).mode = Some(read_byte) },
      () => { result.entity(0) = Some(read_bits(19)) },
      () => { result.entities(1) = Some(read_bits(10)) },
      () => { result.weapon = Some(read_byte) },
      () => { result.client = Some(read_byte) },
      () => { result.angles(0)(1) = Some(read_float) },
      () => { result.trajectories(0).duration = Some(read_int) },
      () => { result.trajectories(1).mode = Some(read_byte) },
      () => { result.origins(0)(0) = Some(read_float) },
      () => { result.origins(0)(1) = Some(read_float) },
      () => { result.origins(0)(2) = Some(read_float) },
      () => { result.misc(4) = Some(read_bits(24)) },
      () => { result.powerups = Some(read_short) },
      () => { result.model(0) = Some(read_byte) },
      () => { result.entities(2) = Some(read_bits(10)) },
      () => { result.misc(3) = Some(read_byte) },
      () => { result.misc(2) = Some(read_byte) },
      () => { result.origins(1)(2) = Some(read_float) },
      () => { result.origins(1)(0) = Some(read_float) },
      () => { result.origins(1)(1) = Some(read_float) },
      () => { result.model(1) = Some(read_byte) },
      () => { result.angles(0)(0) = Some(read_float) },
      () => { result.time(0) = Some(read_int) },
      () => { result.trajectories(1).time = Some(read_int) },
      () => { result.trajectories(1).duration = Some(read_int) },
      () => { result.trajectories(1).base(2) = Some(read_float) },
      () => { result.trajectories(1).delta(0) = Some(read_float) },
      () => { result.trajectories(1).delta(1) = Some(read_float) },
      () => { result.trajectories(1).delta(2) = Some(read_float) },
      () => { result.trajectories(1).gravity = Some(read_int) },
      () => { result.time(1) = Some(read_int) },
      () => { result.angles(0)(2) = Some(read_float) },
      () => { result.angles(1)(0) = Some(read_float) },
      () => { result.angles(1)(2) = Some(read_float) },
      () => { result.misc(0) = Some(read_int) },
      () => { result.misc(1) = Some(read_short) }
    )

    val count = bit_reader.read_byte
    for (i <- 0 until count) {
      if (bit_reader.read_bit == 1) {
        readers(i)()
      }
    }

    Some(result)
  }
}
