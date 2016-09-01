package setup

import javax.inject.{Inject, Singleton}

import play.api.Configuration
import utils._

@Singleton
class Settings @Inject() (
  configuration: Configuration
) {

  val slackApiToken = configuration.getString("slackApiToken").getOrElse(err("Missing slack api token in config"))


}
