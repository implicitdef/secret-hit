package utils

import com.github.implicitdef.toolbox.Pimp
import game.extras._
import game.extras.slackextras.SlackExtras
import play.api.libs.ws.WSResponse

import scala.concurrent.Future

trait Utils extends Pimp with StateExtras with IncomingMessageExtras with SlackExtras with GameRowExtras {
  def fsucc[A](a: A) = Future.successful(a)
  def fnone = fsucc(None)
  def funit = fsucc(())


  implicit class RichWsResponse(r: WSResponse){
    def isGoodStatus = (200 to 299) contains r.status
  }

  implicit class RichSeqString(seq: Seq[String]){
    def commas =
      seq.mkString(", ")
  }


}
