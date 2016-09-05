import com.github.implicitdef.toolbox.Pimp
import play.api.libs.ws.WSResponse
import slick.dbio.{DBIOAction, Effect, NoStream}

import scala.concurrent.Future

package object utils extends Pimp {

  def fsucc[A](a: A) = Future.successful(a)
  def fnone = fsucc(None)
  def funit = fsucc(())

  def dunit = DBIOAction.successful(())

  implicit class RichWsResponse(r: WSResponse){
    def isGoodStatus = (200 to 299) contains r.status
  }

  implicit class RichFuture2[A](f: Future[A]){
    def asDBIOAction: DBIOAction[A, NoStream, Effect] =
      DBIOAction.from(f)
  }





}