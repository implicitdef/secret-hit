package db.slicksetup
import com.github.tminglei.slickpg._
import play.api.libs.json.{JsValue, Json}
import slick.driver.JdbcProfile
import slick.profile.Capability

// cf https://github.com/tminglei/slick-pg/
object CustomDriver extends ExPostgresDriver
  with PgArraySupport
  with PgDateSupportJoda
  with PgPlayJsonSupport {

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
  }


}
