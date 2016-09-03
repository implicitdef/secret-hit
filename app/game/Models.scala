package game

import enumeratum.EnumEntry
import enumeratum.EnumEntry.Lowercase
import play.api.libs.json.{Format, JsResult, JsValue, Json}
import enumeratum._

object Models {

  case class GameState(
    step: GameStep,
    stack: Seq[Policy],
    discarded: Seq[Policy],
    played: Seq[Policy],
    inLegislativeSession: Seq[Policy],
    // all registerd players, including those who died
    players: Seq[Player],
    // during an election
    votes: Map[PlayerId, Boolean],
    // if a special election has been called on a player
    candidateBySpecialElection: Option[PlayerId],
    // these two represents
    // the current candidate/nominee if we are voting
    // or the actual president/chancellor if they've
    // been elected
    president: Option[PlayerId],
    chancellor: Option[PlayerId],
    // the last ones
    lastPresident: Option[PlayerId],
    lastChancellor: Option[PlayerId],
    // those that have been investigated
    investigated: Seq[PlayerId],
    // those who died
    dead: Seq[PlayerId]
  )

  sealed trait Policy extends EnumEntry with Lowercase
  object Policy extends Enum[Policy] with PlayJsonEnum[Policy] {
    val values = findValues
    case object Liberal extends Policy
    case object Fascist extends Policy
  }

  sealed trait Role extends EnumEntry with Lowercase
  object Role extends Enum[Role] with PlayJsonEnum[Role]  {
    val values = findValues
    case object Liberal extends Role
    case object Fascist extends Role
    case object Hitler extends Role
  }


  sealed trait GameStep extends EnumEntry with Lowercase

  object GameStep extends Enum[GameStep] with PlayJsonEnum[GameStep]  {
    val values = findValues
    // We wait for all players to declare themselves
    // and for the start signal (if enough players)
    // When we get it we prepare the players roles and orders
    // and move to the next step
    case object RegisteringPlayers extends GameStep
    // The candidate must choose its chancellor
    // When we get it we move to the next step
    case object ChoosingChancellor extends GameStep
    // We start the election
    // and wait for all votes ja/nein in privates channels
    // when we get all votes, depending on the result,
    // we either move to the next candidate or
    // move to the next step
    case object Electing extends GameStep
    // We wait for the president to discard one of 3 policies
    // and move to the next step
    case object PresidentDiscardingPolicy extends GameStep
    // We wait for the chancellor to discard one of 2 policies
    // apply it and move to the next step
    // If the chancellor asks for a veto though,
    // we go back to the president
    case object ChancellorDiscardingPolicyOrVeto extends GameStep
    // we wait for the president to validate
    // or reject the veto.
    // If accepted, we go to the next candidate
    // If rejected, the chancellor must just discard a policy
    case object PresidentConsideringVeto extends GameStep
    // We wait for the chancellor to discard one of 2 policies
    // apply it and move to the next step
    // Here the chancellor can not veto, either because it's not
    // that stage of the game or because it has been denied by
    // the president
    case object ChancellorDiscardingPolicy extends GameStep
    // The president may have a special power this turn
    // and thus we have to wait for his choice,
    // and apply it
    case object PresidentUsingPower extends GameStep
    // When the game is over
    case object WonByLiberals extends GameStep
    case object WonByFascists extends GameStep
  }

  case class Player(
     id: PlayerId,
     slackUserName: String,
     role: Role
  )

  case class PlayerId(
    slackUserId: String
  )

  import Json._
  implicit val playerIdFormat = format[PlayerId]
  implicit val playerFormat = format[Player]
  implicit val votesMapFormat = new Format[Map[PlayerId, Boolean]] {
    // we convert to/from a Map[String, Seq[PlayerId]]
    // where the Strings are "true"/"false"
    // so it can be represented in JSON
    override def writes(o: Map[PlayerId, Boolean]): JsValue = {
      val translation = o.groupBy(_._2.toString).mapValues(_.keys)
      Json.toJson(translation)
    }
    override def reads(json: JsValue): JsResult[Map[PlayerId, Boolean]] = {
      json.validate[Map[String, Seq[PlayerId]]].map { translation =>
        Seq(true, false).flatMap { boolean =>
          translation.getOrElse(boolean.toString, Nil).map { id =>
            id -> boolean
          }
        }.toMap
      }
    }
  }
  implicit val gameStateFormat = format[GameState]
}

