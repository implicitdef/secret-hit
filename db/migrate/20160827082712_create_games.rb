class CreateGames < ActiveRecord::Migration[5.0]
  def change
    create_table :games do |t|
      t.belongs_to :team, index: true

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
    end
  end
end
