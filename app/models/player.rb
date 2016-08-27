class Player < ApplicationRecord
  has_one :team
  has_many :game_players, dependent: :destroy
  has_many :games, through: :game_players
end
