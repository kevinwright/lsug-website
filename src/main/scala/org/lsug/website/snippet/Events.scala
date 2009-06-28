package org.lsug.website.snippet


import collection.jcl.{Buffer, Conversions}
import joda.time.{DateTime, Interval}
import org.lsug.ical4scala._
import org.lsug.ical4scala.Preamble._
import java.io.{InputStreamReader, InputStream}
import java.net.{URL, URI}
import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model.{Parameter, Property, Component, Calendar}
import scala.xml.{Text, NodeSeq}
/**
 * Created by IntelliJ IDEA.
 * User: kevin
 * Date: 28-Jun-2009
 * Time: 00:06:10
 * To change this template use File | Settings | File Templates.
 */

class Events {
  def openCalendar() : Calendar = {
    val url = new URL("http://www.google.com/calendar/ical/lsug.org_i75n49pgdv5kq9sqjmqbch6q3s%40group.calendar.google.com/public/basic.ics")
    val istream = url.openStream()
    val calendarBuilder = new CalendarBuilder();
    val calendar = calendarBuilder.build(istream);
    istream.close()
    calendar
  }

  def summary = {
    val cal : RichCalendar = openCalendar()
    <div>{cal.map(componentToContent _)}</div>
  }

  def mkPreNode(content: Option[String]) : NodeSeq = {
    content match {
      case Some(text) => <pre>{text}</pre>
      case None => NodeSeq.Empty
    }
  }

  def mkTextNode(content: Option[String]) : NodeSeq = {
    content match {
      case Some(text) => Text(text)
      case None => NodeSeq.Empty
    }
  }

  def mkListNode(title: String, content: Option[AnyRef]) : NodeSeq = {
    content match {
      case Some(cnt) => <li><b>{title}:</b> {cnt toString}</li>
      case None => NodeSeq.Empty
    }
  }

  def formatTimeRange(start: Option[DateTime], end: Option[DateTime]) : Option[String] = {
    import joda.time.format.DateTimeFormat._
    (start, end) match {
      case (Some(s), Some(e)) => Some(
        "" +
        fullDate.print(s) +
        " - " +
        mediumTime.print(s) +
        " to " +
        mediumTime.print(e)
        )
      case _ => None
    }
  }

  private def componentToContent(comp : Component) : NodeSeq = {
    //<div>{comp.getName}: {comp.map(prop => <p><b>{prop.getName} = {prop.getValue}</b></p>)}</div>
    <div>
      <b>{mkTextNode(comp.summary)}</b>
      <p>{mkPreNode(comp.description)}</p>
      <ul>
        {mkListNode("When", formatTimeRange(comp.start, comp.end))}
        {mkListNode("Where", comp.location)}
      </ul>
    </div>
  }

  private def paramsForProp(prop : Property) : Iterator[Pair[String,String]] = {
    def paramIteratorWrapper(iter: java.util.Iterator[_]): Iterator[Parameter] = {
      new Iterator[Parameter] {
        def hasNext: Boolean = iter.hasNext
        def next(): Parameter = iter.next.asInstanceOf[Parameter]
        def remove(): Unit = throw new UnsupportedOperationException()
      }
    }

    val params = paramIteratorWrapper(prop.getParameters.iterator)
    params.map(p => (p.getName -> p.getValue))
  }
}