package org.lsug.ical4scala


import scalatest.matchers.MustMatchers
import scalatest.testng.TestNGSuite
import testng.annotations.Test

/**
 * Created by kevin
 * 21:56:12 on 01-Jul-2009
 */

//class VCalendarSpecTest extends JUnit4(VCalendarSpec)

class VCalendarSpec extends TestNGSuite with MustMatchers {
    @Test def canConstructCleanlyFromInputStream = {
      val instream = this.getClass.getClassLoader.getResourceAsStream("test-calendar.ics")
      val cal = VCalendar(instream)
      ()
    }

  @Test def hasValidName = {
    val instream = this.getClass.getClassLoader.getResourceAsStream("test-calendar.ics")
    val cal = VCalendar(instream)
    cal.x_calname match {
      case Some(nameProp) => nameProp.value must equal ("Scala Events in London")
      case _ => error ("name not found")
    }

  }
    
}