package org.lsug.ical4scala

/**
 * Created by kevin
 * 21:47:54 on 01-Jul-2009
 */


case class VCalProperty[T](value : T, params : List[(String, String)]) {
  def this(value : T) = this(value, Nil)
}

object VCalProperty {
  def apply[T](value : T) = new VCalProperty(value, Nil)
}

