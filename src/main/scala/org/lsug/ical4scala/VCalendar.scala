package org.lsug.ical4scala


/**
 * Created by kevin
 * 20:40:06 on 29-Jun-2009
 */

case class VCalendar(
  prodid : VCalProperty[String],
  version : VCalProperty[String],
  scale : Option[VCalProperty[String]],
  method : Option[VCalProperty[String]],
  x_calname : Option[VCalProperty[String]],
  x_timezone : Option[VCalProperty[String]],
  x_caldesc : Option[VCalProperty[String]],
  components : List[AnyRef])
{

}

object VCalendar {
  def apply(istream : java.io.InputStream) : VCalendar = {
    val calendarBuilder = new net.fortuna.ical4j.data.CalendarBuilder
    val underlying = calendarBuilder.build(istream);
    this.apply(underlying)
  }

  def apply(underlying : net.fortuna.ical4j.model.Calendar) : VCalendar = {
    new VCalendar(
      VCalProperty(underlying.getProductId.getValue),
      VCalProperty(underlying.getVersion.getValue),
      Some(VCalProperty(underlying.getCalendarScale.getValue)),
      Some(VCalProperty(underlying.getMethod.getValue)),
      Some(VCalProperty(underlying.getProperty("X-WR-CALNAME").getValue)),
      Some(VCalProperty(underlying.getProperty("X-WR-TIMEZONE").getValue)),
      Some(VCalProperty(underlying.getProperty("X-WR-CALDESC").getValue)),
      Nil)
  }
}