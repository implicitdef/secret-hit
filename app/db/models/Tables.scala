package db.models



object Tables {
  val profile = slick.driver.PostgresDriver
  import profile.api._


  //TODO adapter pour tester les extensions postgres

  case class TeamRow(id: Int, name: String, creationDate: java.sql.Timestamp, maybe: Option[String] = None)

  class Team(tag: Tag) extends Table[TeamRow](tag, "team") {
    def * = (id, name, creationDate, maybe) <> (TeamRow.tupled, TeamRow.unapply)
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    val name: Rep[String] = column[String]("name")
    val creationDate: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("creation_date")
    val maybe: Rep[Option[String]] = column[Option[String]]("maybe", O.Default(None))
  }
  /** Collection-like TableQuery object for table Team */
  lazy val Teams = TableQuery[Team]
}
