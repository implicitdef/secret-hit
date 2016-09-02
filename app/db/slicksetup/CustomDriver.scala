package db.slicksetup
import com.github.tminglei.slickpg._
import db.slicksetup.Enums._
import slick.driver.JdbcProfile
import slick.profile.Capability

// cf https://github.com/tminglei/slick-pg/
object CustomDriver extends ExPostgresDriver
  with PgArraySupport
  with PgDateSupportJoda
  with PgPlayJsonSupport
  with PgEnumSupport {

  def pgjson = "jsonb"

  // Add back `capabilities.insertOrUpdate` to enable native `upsert` support; for postgres 9.5+
  override protected def computeCapabilities: Set[Capability] =
    super.computeCapabilities + JdbcProfile.capabilities.insertOrUpdate

  override val api = CustomAPI

  object CustomAPI extends API
    with ArrayImplicits
    with DateTimeImplicits
    with JsonImplicits {
    // not sure if we need this ?
    implicit val strListTypeMapper = new SimpleArrayJdbcType[String]("text").to(_.toList)

    implicit val policyTypeMapper = createEnumJdbcType("policy", Policies)
    implicit val policyListTypeMapper = createEnumListJdbcType("policy", Policies)
    implicit val policyColumnExtensionMethodsBuilder = createEnumColumnExtensionMethodsBuilder(Policies)
    implicit val policyOptionColumnExtensionMethodsBuilder = createEnumOptionColumnExtensionMethodsBuilder(Policies)

    implicit val gameStepTypeMapper = createEnumJdbcType("game_step", GameSteps)
    implicit val gameStepListTypeMapper = createEnumListJdbcType("game_step", GameSteps)
    implicit val gameStepColumnExtensionMethodsBuilder = createEnumColumnExtensionMethodsBuilder(GameSteps)
    implicit val gameStepOptionColumnExtensionMethodsBuilder = createEnumOptionColumnExtensionMethodsBuilder(GameSteps)

  }


}
