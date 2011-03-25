package org.quakelivedemoparser

import java.nio.ByteBuffer

abstract class AbstractBitBuffer(val buffer: ByteBuffer) {
  protected var offset: Int = 0

  def offsets: (Int, Int) = {
    val bit_offset = offset & BitBuffer.BYTE_MASK
    val byte_offset = offset / BitBuffer.BITS_IN_BYTE
    (bit_offset, byte_offset)
  }

  def position: Int = offset
  def capacity: Int = buffer.limit() * BitBuffer.BITS_IN_BYTE
  def remaining: Int = capacity - position
  def is_empty: Boolean = position == capacity
}

object BitBuffer {
  val BYTES_IN_INT = 4
  val BYTES_IN_SHORT = 2
  val BITS_IN_BYTE = 8
  val BYTE_MASK = 0x07
  val BIT_MASK = 0x01
}
