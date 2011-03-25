package org.quakelivedemoparser

import java.nio.ByteBuffer

class BitBufferFeed(buffer: ByteBuffer) extends AbstractBitBuffer(buffer) {
  def get: Int = {
    val (bit_offset, byte_offset) = offsets
    offset += 1
    val ret = buffer.get(byte_offset) >> bit_offset
    ret & BitBuffer.BIT_MASK
  }
}
