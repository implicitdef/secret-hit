class Game < ApplicationRecord
  has_one :team
  has_many :game_players, dependent: :destroy
  has_many :players, through: :game_players
end
