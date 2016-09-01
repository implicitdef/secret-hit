import com.github.implicitdef.toolbox.Pimp
import play.api.libs.ws.WSResponse

import scala.concurrent.Future

package object utils extends Pimp {

  def fsucc[A](a: A) = Future.successful(a)
  def fnone = fsucc(None)
  def funit = fsucc(())

  implicit class RichWsResponse(r: WSResponse){
    def isGoodStatus = (200 to 299) contains r.status
  }

}