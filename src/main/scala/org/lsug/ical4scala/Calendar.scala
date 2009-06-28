package org.lsug.ical4scala


import collection.jcl.Buffer
import joda.time.format.ISODateTimeFormat
import joda.time.{Interval, DateTime}
import net.fortuna.ical4j.model.{Parameter, Property, Calendar, Component}
/**
 * Created by kevin
 * 17:34:29 on 28-Jun-2009
 */

object Preamble {
  implicit def calendar2rich(calendar: Calendar) : RichCalendar = new RichCalendar(calendar)
  implicit def component2rich(component: Component) : RichComponent = new RichComponent(component)
  implicit def property2rich(property: Property) : RichProperty = new RichProperty(property)
}

class RichCalendar(val calendar : Calendar) extends Iterable[Component] {
  override val hasDefiniteSize = true

  override val isEmpty = calendar.getComponents.isEmpty

  override def elements : Iterator[Component] = {
    val componentList = calendar.getComponents.asInstanceOf[java.util.List[Component]]
    Buffer(componentList).elements
  }
}

class RichComponent(val component : Component) extends Iterable[Property] {
  override val hasDefiniteSize = true

  override val isEmpty = component.getProperties.isEmpty

  override def elements : Iterator[Property] = {
    val propList = component.getProperties.asInstanceOf[java.util.List[Property]]
    Buffer(propList).elements
  }

  def getProperty(name : String) : Option[String] = {
    val prop = component.getProperty(name)
    if (prop == null) None
    else Some(prop.getValue)
  }

  def getDateTimeProperty(name : String) : Option[DateTime] = {
    val dtFormat = ISODateTimeFormat.basicDateTimeNoMillis
    getProperty(name) map (dtFormat.parseDateTime(_))
  }

  def uid = getProperty(Property.UID)
  def tstamp = getDateTimeProperty(Property.DTSTAMP)
  def created = getDateTimeProperty(Property.CREATED)
  def modified = getDateTimeProperty(Property.LAST_MODIFIED)

  def start = getDateTimeProperty(Property.DTSTART)
  def end = getDateTimeProperty(Property.DTEND)
  def interval : Option[Interval] = {
    (start, end) match {
      case (Some(s), Some(e)) => Some(new Interval(s, e))
      case _ => None
    }
  }
  def location = getProperty(Property.LOCATION)
  def summary = getProperty(Property.SUMMARY)
  def description = getProperty(Property.DESCRIPTION)
}

class RichProperty(val property : Property) extends Iterable[Parameter] {
  override val hasDefiniteSize = true

  override val isEmpty = property.getParameters.isEmpty

  override def elements : Iterator[Parameter] = {
    val paramList = property.getParameters.asInstanceOf[java.util.List[Parameter]]
    Buffer(paramList).elements
  }
}