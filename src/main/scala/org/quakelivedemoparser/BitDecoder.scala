package org.quakelivedemoparser

import com.google.common.io.Resources
import java.io.{ByteArrayInputStream, ObjectInputStream}

class BitDecoder(buffer: BitBufferFeed) {
  def apply: Int = apply(BitDecoder.root)

  def apply(current: Node): Int = {
    if (current.value.isDefined) {
      current.value.get
    } else {
      val ret = current.children(buffer.get)
      if (ret.isEmpty) 0 else apply(ret.get)
    }
  }
}

@serializable
class Node(val value: Option[Int], val children: Array[Option[Node]]) {
}

object BitDecoder {
  lazy val root = load_root

  def load_root: Node = {
    val url = Resources.getResource("org/quakelivedemoparser/decoder-tree")
    val bytes = Resources.toByteArray(url)
    val stream = new ByteArrayInputStream(bytes)
    val objects = new ObjectInputStream(stream)
    val ret = objects.readObject.asInstanceOf[Node]
    objects.close
    ret
  }
}
