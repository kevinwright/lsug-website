package bootstrap.liftweb

import net.liftweb.util._
import net.liftweb.http._
import net.liftweb.sitemap._
import net.liftweb.sitemap.Loc._
import Helpers._
import net.liftweb.mapper.{DB, ConnectionManager, Schemifier, DefaultConnectionIdentifier, ConnectionIdentifier}
import java.sql.{Connection, DriverManager}
import org.lsug.website.model._
 
/**
  * A class that's instantiated early and run.  It allows the application
  * to modify lift's environment
  */
class Boot {
  def boot {
    DefaultConnectionIdentifier.jndiName = "jdbc/scalausergroup"
    
    if (!DB.jndiJdbcConnAvailable_?) DB.defineConnectionManager(DefaultConnectionIdentifier, DBVendor)
    // where to search snippet
    LiftRules.addToPackages("org.lsug.website")
    Schemifier.schemify(true, Log.infoF _, User)

    // Build SiteMap
    val entries =
      Menu(Loc("Home", List("index"), "Home")) ::
      Menu(Loc("Resources", List("resources"), "Resources")) ::
      Nil
      // :: User.sitemap
    LiftRules.setSiteMap(SiteMap(entries:_*))
  }
}


object DBVendor extends ConnectionManager {
  def newConnection(name: ConnectionIdentifier): Box[Connection] = {
    try {
      Class.forName("org.apache.derby.jdbc.EmbeddedDriver")
      val dm = DriverManager.getConnection("jdbc:derby:lift_example;create=true")
      Full(dm)
    } catch {
      case e : Exception => e.printStackTrace; Empty
    }
  }
  def releaseConnection(conn: Connection) {conn.close}
}

