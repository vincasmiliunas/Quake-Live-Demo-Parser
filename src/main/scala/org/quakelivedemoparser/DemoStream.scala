package org.quakelivedemoparser

trait DemoStream extends Traversable[DemoEvent] {
  def next: Option[DemoEvent]

  def foreach[U](input: (DemoEvent) ⇒ U): Unit = {
    while (true) {
      val ret = next
      ret match {
        case None => return
        case Some(x) => input(x)
      }
    }
  }
}

class DemoEvent

class StartDemoEvent extends DemoEvent
class EndDemoEvent extends DemoEvent

class StartMessageEvent(val parent: StartDemoEvent, val id: Int, val code: Int) extends DemoEvent
class MessageCommandEvent(val parent: StartMessageEvent, val id: Int, val str: String) extends DemoEvent
class EndMessageEvent(val parent: StartDemoEvent, val id: Int) extends DemoEvent

class StartGamestateEvent(val parent: StartMessageEvent, val id: Int) extends DemoEvent
class BaselineConfigEvent(val parent: StartGamestateEvent, val id: Int, val str: String) extends DemoEvent
class BaselineEntityEvent(val parent: StartGamestateEvent, val id: Int, val entity: Entity) extends DemoEvent
class EndGamestateEvent(val parent: StartMessageEvent, val id: Int, val client: Int, val checksum: Int) extends DemoEvent

class StartSnapshotEvent(val parent: StartMessageEvent, val time: Int, val delta: Int, val flags: Int, val blob: Array[Byte], val player: Player) extends DemoEvent
class SnapshotEntityEvent(val parent: StartSnapshotEvent, val id: Int, val entity: Entity) extends DemoEvent
class SnapshotEntityRemovedEvent(val parent: StartSnapshotEvent, val id: Int) extends DemoEvent
class EndSnapshotEvent(val parent: StartMessageEvent, val time: Int) extends DemoEvent
