package org.quakelivedemoparser

import java.nio.ByteBuffer
import java.io.ByteArrayOutputStream
import com.google.common.base.Charsets

class BitReader(buffer: BitBufferFeed) {
  val logger = org.slf4j.LoggerFactory.getLogger(classOf[BitReader].getSimpleName)
  val bit_decoder = new BitDecoder(buffer)

  def read_bit = buffer.get

  def read_bits_sub8(count: Int): Int = {
    if (count == 0) 0 else read_bit | (read_bits_sub8(count - 1) << 1)
  }

  def read_bits(count: Int) = {
    val bits = count & BitBuffer.BYTE_MASK
    val bytes = count / BitBuffer.BITS_IN_BYTE
    val ret = read_bits_sub8(bits) | (read_bytes(bytes) << bits)
    ret
  }

  def read_byte = bit_decoder.apply

  def read_bytes(count: Int): Int = {
    if (count == 0) 0 else read_byte | (read_bytes(count - 1) << BitBuffer.BITS_IN_BYTE)
  }

  def read_short = read_bytes(BitBuffer.BYTES_IN_SHORT)
  
  def read_int = read_bytes(BitBuffer.BYTES_IN_INT)

  def read_signed_byte = {
    val tmp = read_byte
    val ret = tmp | (0xFFFFFF00 * ((tmp >> 7) & 1))
    ret
  }

  def read_signed_short = {
    val tmp = read_short
    val ret = tmp | (0xFFFF0000 * ((tmp >> 15) & 1))
    ret
  }

  def read_float: Float = {
    val ret = if (read_bit == 0) read_bits(13) - 4096 else read_int
    java.lang.Float.intBitsToFloat(ret)
  }

  def read_string: String = {
    val stream = new ByteArrayOutputStream
    while (true) {
      val ret = read_byte
      if (ret == 0) {
        return new String(stream.toByteArray, Charsets.UTF_8)
      }
      stream.write(ret)
    }
    null
  }

  def read_blob(input: Int): Array[Byte] = {
    val result = ByteBuffer.allocate(input)
    for (i <- 0 until input) {
      val ret = read_byte
      result.put(ret.asInstanceOf[Byte])
    }
    result.array
  }
}
