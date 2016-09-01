package db.models
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.driver.PostgresDriver
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.driver.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = PlayEvolutions.schema ++ PlayEvolutionsLock.schema ++ Team.schema
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table PlayEvolutions
   *  @param id Database column id SqlType(int4), PrimaryKey
   *  @param hash Database column hash SqlType(varchar), Length(255,true)
   *  @param appliedAt Database column applied_at SqlType(timestamp)
   *  @param applyScript Database column apply_script SqlType(text), Default(None)
   *  @param revertScript Database column revert_script SqlType(text), Default(None)
   *  @param state Database column state SqlType(varchar), Length(255,true), Default(None)
   *  @param lastProblem Database column last_problem SqlType(text), Default(None) */
  case class PlayEvolutionsRow(id: Int, hash: String, appliedAt: java.sql.Timestamp, applyScript: Option[String] = None, revertScript: Option[String] = None, state: Option[String] = None, lastProblem: Option[String] = None)
  /** GetResult implicit for fetching PlayEvolutionsRow objects using plain SQL queries */
  implicit def GetResultPlayEvolutionsRow(implicit e0: GR[Int], e1: GR[String], e2: GR[java.sql.Timestamp], e3: GR[Option[String]]): GR[PlayEvolutionsRow] = GR{
    prs => import prs._
    PlayEvolutionsRow.tupled((<<[Int], <<[String], <<[java.sql.Timestamp], <<?[String], <<?[String], <<?[String], <<?[String]))
  }
  /** Table description of table play_evolutions. Objects of this class serve as prototypes for rows in queries. */
  class PlayEvolutions(_tableTag: Tag) extends Table[PlayEvolutionsRow](_tableTag, "play_evolutions") {
    def * = (id, hash, appliedAt, applyScript, revertScript, state, lastProblem) <> (PlayEvolutionsRow.tupled, PlayEvolutionsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(hash), Rep.Some(appliedAt), applyScript, revertScript, state, lastProblem).shaped.<>({r=>import r._; _1.map(_=> PlayEvolutionsRow.tupled((_1.get, _2.get, _3.get, _4, _5, _6, _7)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(int4), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column hash SqlType(varchar), Length(255,true) */
    val hash: Rep[String] = column[String]("hash", O.Length(255,varying=true))
    /** Database column applied_at SqlType(timestamp) */
    val appliedAt: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("applied_at")
    /** Database column apply_script SqlType(text), Default(None) */
    val applyScript: Rep[Option[String]] = column[Option[String]]("apply_script", O.Default(None))
    /** Database column revert_script SqlType(text), Default(None) */
    val revertScript: Rep[Option[String]] = column[Option[String]]("revert_script", O.Default(None))
    /** Database column state SqlType(varchar), Length(255,true), Default(None) */
    val state: Rep[Option[String]] = column[Option[String]]("state", O.Length(255,varying=true), O.Default(None))
    /** Database column last_problem SqlType(text), Default(None) */
    val lastProblem: Rep[Option[String]] = column[Option[String]]("last_problem", O.Default(None))
  }
  /** Collection-like TableQuery object for table PlayEvolutions */
  lazy val PlayEvolutions = new TableQuery(tag => new PlayEvolutions(tag))

  /** Entity class storing rows of table PlayEvolutionsLock
   *  @param lock Database column lock SqlType(int4), PrimaryKey */
  case class PlayEvolutionsLockRow(lock: Int)
  /** GetResult implicit for fetching PlayEvolutionsLockRow objects using plain SQL queries */
  implicit def GetResultPlayEvolutionsLockRow(implicit e0: GR[Int]): GR[PlayEvolutionsLockRow] = GR{
    prs => import prs._
    PlayEvolutionsLockRow(<<[Int])
  }
  /** Table description of table play_evolutions_lock. Objects of this class serve as prototypes for rows in queries. */
  class PlayEvolutionsLock(_tableTag: Tag) extends Table[PlayEvolutionsLockRow](_tableTag, "play_evolutions_lock") {
    def * = lock <> (PlayEvolutionsLockRow, PlayEvolutionsLockRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = Rep.Some(lock).shaped.<>(r => r.map(_=> PlayEvolutionsLockRow(r.get)), (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column lock SqlType(int4), PrimaryKey */
    val lock: Rep[Int] = column[Int]("lock", O.PrimaryKey)
  }
  /** Collection-like TableQuery object for table PlayEvolutionsLock */
  lazy val PlayEvolutionsLock = new TableQuery(tag => new PlayEvolutionsLock(tag))

  /** Entity class storing rows of table Team
   *  @param id Database column id SqlType(serial), AutoInc, PrimaryKey
   *  @param name Database column name SqlType(text)
   *  @param maybe Database column maybe SqlType(text), Default(None) */
  case class TeamRow(id: Int, name: String, maybe: Option[String] = None)
  /** GetResult implicit for fetching TeamRow objects using plain SQL queries */
  implicit def GetResultTeamRow(implicit e0: GR[Int], e1: GR[String], e2: GR[Option[String]]): GR[TeamRow] = GR{
    prs => import prs._
    TeamRow.tupled((<<[Int], <<[String], <<?[String]))
  }
  /** Table description of table team. Objects of this class serve as prototypes for rows in queries. */
  class Team(_tableTag: Tag) extends Table[TeamRow](_tableTag, "team") {
    def * = (id, name, maybe) <> (TeamRow.tupled, TeamRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(name), maybe).shaped.<>({r=>import r._; _1.map(_=> TeamRow.tupled((_1.get, _2.get, _3)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column name SqlType(text) */
    val name: Rep[String] = column[String]("name")
    /** Database column maybe SqlType(text), Default(None) */
    val maybe: Rep[Option[String]] = column[Option[String]]("maybe", O.Default(None))
  }
  /** Collection-like TableQuery object for table Team */
  lazy val Team = new TableQuery(tag => new Team(tag))
}
