package db.slicksetup

object Enums {

  object Policies extends Enumeration {
    type Policy = Value
    val liberal, fascist = Value
  }
  object GameSteps extends Enumeration {
    type GameStep = Value
    val
    game_setup,
    game_ready,
    choosing_chancellor,
    election,
    president_choice,
    chancellor_choice,
    president_veto_choice,
    president_power_choice,
    over = Value
  }

}

