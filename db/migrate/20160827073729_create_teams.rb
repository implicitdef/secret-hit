class CreateTeams < ActiveRecord::Migration[5.0]
  def change
    create_table :teams do |t|
      t.string :api_token
      t.string :team_id
      t.string :team_name
      t.string :bot_id
      t.timestamps
    end
  end
end
