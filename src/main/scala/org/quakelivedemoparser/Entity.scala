package org.quakelivedemoparser

class Entity {
  val animations: Array[Option[Int]] = Array(None, None)
  val angles: Array[Array[Option[Float]]] = Array(Array(None, None), Array(None, None))
  var client: Option[Int] = None
  val entities: Array[Option[Int]] = Array(None, None, None)
  val entity: Array[Option[Int]] = Array(None, None)
  val events: Array[Option[Int]] = Array(None, None)
  val misc: Array[Option[Int]] = Array(None, None, None, None, None)
  val model: Array[Option[Int]] = Array(None, None)
  val origins: Array[Array[Option[Float]]] = Array(Array(None, None, None), Array(None, None, None))
  var powerups: Option[Int] = None
  val time: Array[Option[Int]] = Array(None, None)
  val trajectories: Array[Trajectory] = Array(new Trajectory, new Trajectory)
  var weapon: Option[Int] = None
}

class Trajectory {
  val base: Array[Option[Float]] = Array(None, None, None)
  val delta: Array[Option[Float]] = Array(None, None, None)
  var duration: Option[Int] = None
  var gravity: Option[Int] = None
  var mode: Option[Int] = None
  var time: Option[Int] = None
}
