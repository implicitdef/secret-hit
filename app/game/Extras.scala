package game

import game.extras.slackextras.SlackExtras
import game.extras.{IncomingMessageExtras, StateExtras}

object Extras extends StateExtras with IncomingMessageExtras with SlackExtras {

}
