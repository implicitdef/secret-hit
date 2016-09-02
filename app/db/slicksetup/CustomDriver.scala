package db.slicksetup
import com.github.tminglei.slickpg._
import db.slicksetup.Enums.Genders
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

  override val api = new API
    with ArrayImplicits
    with DateTimeImplicits
    with JsonImplicits {
    // not sure if we need this ?
    implicit val strListTypeMapper = new SimpleArrayJdbcType[String]("text").to(_.toList)

    implicit val genderTypeMapper = createEnumJdbcType("gender", Genders)
    //implicit val genderColumnExtensionMethodsBuilder = createEnumColumnExtensionMethodsBuilder(Genders)
  }


}