
# --- !Ups

CREATE TABLE slack_teams(
  slack_team_id TEXT NOT NULL PRIMARY KEY CHECK (char_length(slack_team_id) < 150),
  slack_api_token TEXT NOT NULL CHECK (char_length(slack_api_token) < 150),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE slack_users(
  slack_team_id TEXT NOT NULL REFERENCES slack_teams,
  slack_user_id TEXT NOT NULL PRIMARY KEY CHECK (char_length(slack_user_id) < 150),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);




/*
archive of rails stuff


create_table :games do |t|
      t.belongs_to :team, index: true

      t.string :channel_id

      # The policies arrays all contain strings which are either
      # "liberal" or "fascist"
      t.string :policies_stack, array: true
      t.string :discarded_stack, array: true
      t.string :played_policies, array: true

      # The current choice of the president, waiting
      # for a choice by the chancellor.
      # (nil/"liberal"/"fascist")
      t.string :president_choice
      # If the chancellor instead asked for a veto,
      # and we are waiting for the president answer.
      # (nil/true/false)
      t.boolean :chancellor_asks_for_veto


      t.integer :turns_without_elections

      # "game_setup"
      # => We wait for all players to declare themselves
      # => and for the start signal
      # => when we get it we pick the roles and players order
      # => we inform everybody and wait for a ready signal
      # "game_ready"
      # => when we get the ready signal we start the first election
      # => the candidate must choose its chancellor
      # "choosing_chancellor"
      # => the election can start
      # => and wait for all the votes ja/nein
      # "election"
      # => when we got all votes, if election unsuccessful
      # => we go to next candidate, etc.
      # => otherwise the president gets to make its choice
      # => for the legislative session
      # "president_choice"
      # => when the president chooses, we wait for the chancellor
      # "chancellor_choice"
      # => in case the chancellor chose veto, we wait for the president
      # "president_veto_choice"
      # => anyway we apply the final policy and rearrange the policies stacks
      # => the policy may trigger a presidental power
      # => in that case we need the president to make a choice
      # => "president_power_choice"
      # => when the president chose, we apply the power
      # => anyway at the end we go to the next election and loop again
      # ===> Anyway at anytime if we detect the game is over we
      # ===> notify everybody of who won, who was what role, and
      # ===> the step becomes :
      # "over"
      t.string :current_step

      t.timestamps


create_table :game_players do |t|
      t.belongs_to :game, index: true
      t.belongs_to :player, index: true

      # to organize the players in a circle
      # start from 0
      t.integer :order

      # "liberal"/"fascist"/"hitler"
      t.string :role

      # is he normal current candidate
      # (except if there's a special election)
      # tracks where we are in the circle
      t.boolean :is_candidate

      # if a special election has been called on him
      # next time the election will go back to the normal
      # player with is_candidate
      t.boolean :is_candidate_by_special_election

      # nominated by the candidate for the current election
      t.boolean :is_nominee_for_chancellor

      # self explanatory
      t.boolean :is_president
      t.boolean :is_chancellor
      t.boolean :is_dead
      t.boolean :is_voting_yes
      t.boolean :was_last_chancellor
      t.boolean :was_last_president
      t.boolean :has_been_investigated

      t.timestamps
    end
 */

