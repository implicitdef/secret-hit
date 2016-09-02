package db.slicksetup

import org.joda.time.DateTime


object Tables {
  import CustomDriver.api._


  //TODO adapter pour tester les extensions postgres

  case class TeamRow(id: Int, name: String, creationDate: DateTime, maybe: Option[String] = None)

  class Team(tag: Tag) extends Table[TeamRow](tag, "team") {
    def * = (id, name, creationDate, maybe) <> (TeamRow.tupled, TeamRow.unapply)
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    val name: Rep[String] = column[String]("name")
    val creationDate: Rep[DateTime] = column[DateTime]("creation_date")
    val maybe: Rep[Option[String]] = column[Option[String]]("maybe", O.Default(None))
  }
  /** Collection-like TableQuery object for table Team */
  lazy val Teams = TableQuery[Team]
}
