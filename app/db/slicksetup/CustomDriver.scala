package db.slicksetup
import com.github.tminglei.slickpg._
import game.Models
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

  override val api = CustomAPI

  object CustomAPI extends API
    with ArrayImplicits
    with DateTimeImplicits
    with JsonImplicits {

    import Models._
    implicit val gameStateJsonTypeMapper = MappedJdbcType.base[GameState, JsValue](Json.toJson(_), _.as[GameState])


  }


}
