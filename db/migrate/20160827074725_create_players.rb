class CreatePlayers < ActiveRecord::Migration[5.0]
  def change
    create_table :players do |t|
      t.belongs_to :team, index: true
      t.string :user_id
      t.timestamps
    end
  end
end
