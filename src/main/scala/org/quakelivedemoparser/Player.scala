package org.quakelivedemoparser

class Player {
  val ammunition: Array[Option[Int]] = new Array(16)
  for (i <- 0 until ammunition.length)
    ammunition(i) = None
  val animations: Array[Array[Option[Int]]] = Array(Array(None, None), Array(None, None))
  val attributes: Array[Option[Int]] = new Array(16)
  for (i <- 0 until attributes.length)
    attributes(i) = None
  var client: Option[Int] = None
  val damage: Array[Option[Int]] = Array(None, None, None, None)
  val delta: Array[Option[Int]] = Array(None, None, None)
  val entities: Array[Option[Int]] = Array(None, None)
  val entity: Array[Option[Int]] = Array(None)
  val event: Array[Option[Int]] = Array(None, None, None)
  val events: Array[Option[Int]] = Array(None, None)
  val external: Array[Option[Int]] = Array(None, None)
  val grapple: Array[Option[Float]] = Array(None, None, None)
  val origin: Array[Option[Float]] = Array(None, None, None)
  val misc: Array[Option[Int]] = Array(None, None, None, None, None, None)
  val movement: Array[Option[Int]] = Array(None, None, None, None)
  val powerups: Array[Option[Int]] = new Array(16)
  for (i <- 0 until powerups.length)
    powerups(i) = None
  var time: Option[Int] = None
  val velocity: Array[Option[Float]] = Array(None, None, None)
  val view: Array[Option[Float]] = Array(None, None, None)
  val vitals: Array[Option[Int]] = new Array(16)
  for (i <- 0 until vitals.length)
    vitals(i) = None
  val weapon: Array[Option[Float]] = Array(None, None, None)
}