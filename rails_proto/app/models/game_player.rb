class GamePlayer < ApplicationRecord
  belongs_to :game
  belongs_to :player

  def self.defaults_attrs
    {
        is_candidate: false,
        is_candidate_by_special_election: false,
        is_nominee_for_chancellor: false,
        is_president: false,
        is_chancellor: false,
        is_dead: false,
        was_last_chancellor: false,
        was_last_president: false,
        has_been_investigated: false
    }
  end


end
