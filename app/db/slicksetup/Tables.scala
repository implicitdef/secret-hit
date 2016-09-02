package db.slicksetup

import db.slicksetup.Enums.Genders.Gender
import org.joda.time.DateTime
import slick.lifted._
import slick.model._

object Enums {
  object Genders extends Enumeration {
    type Gender = Value
    val male, female = Value
  }
}

object Tables {
  import CustomDriver.api._


  case class TeamRow(
    id: Int,
    name: String,
    gender: Gender,
    creationDate: DateTime,
    maybe: Option[String] = None
  )

  class Team(tag: Tag) extends Table[TeamRow](tag, "team") {
    def * = (id, name, gender, creationDate, maybe) <> (TeamRow.tupled, TeamRow.unapply)
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    val name: Rep[String] = column[String]("name")
    val gender: Rep[Gender] = column[Gender]("gender")
    val creationDate: Rep[DateTime] = column[DateTime]("creation_date")
    val maybe: Rep[Option[String]] = column[Option[String]]("maybe", O.Default(None))
  }
  /** Collection-like TableQuery object for table Team */
  lazy val Teams = TableQuery[Team]
}
