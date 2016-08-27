class GamePlayer < ApplicationRecord
  has_one :game
  has_one :player

end
