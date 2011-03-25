package org.quakelivedemoparser

import org.junit._
import Assert._
import com.google.common.io.Resources
import java.nio.channels.Channels

class DemoReaderTest {
  val logger = org.slf4j.LoggerFactory.getLogger(classOf[DemoReaderTest].getSimpleName)

  def scan_stream(instance: DemoReader): Boolean = {
    var gged = false
    while (true) {
      val next = instance.next
      next match {
        case None => return gged
        case Some(input: MessageCommandEvent) if input.str.endsWith(" ^2gg\"") => gged = true
        case _ =>
      }
    }
    false
  }

  @Test
  def should_parse_the_demo: Unit = {
    val url = Resources.getResource("org/quakelivedemoparser/duel.dm_73")
    val supplier = Resources.newInputStreamSupplier(url)
    val channel = Channels.newChannel(supplier.getInput)
    val instance = new DemoReader(channel)
    val ret = scan_stream(instance)
    assertTrue(ret)
  }

}
