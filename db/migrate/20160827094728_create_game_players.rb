class CreateGamePlayers < ActiveRecord::Migration[5.0]
  def change
    create_table :game_players do |t|
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
  end
end
