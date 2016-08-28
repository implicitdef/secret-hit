class Game < ApplicationRecord
  belongs_to :team
  has_many :game_players, dependent: :destroy
  has_many :players, through: :game_players

  after_initialize :set_defaults

  def register_game_player(player:)
    game_players.create!({
      player_id: player.id,
      order: next_order_number_for_creation
    }.merge(GamePlayer.defaults_attrs))
  end

  private

  def next_order_number_for_creation
    max = game_players.map {|gp| gp.order }.max
    max.nil? ? 1 : max + 1
  end

  def set_defaults
    self.policies_stack ||= (
      Array.new(6, :liberal).concat(Array.new(11, :fascist)).shuffle
    )
    self.discarded_stack ||= []
    self.played_policies ||= []
    self.turns_without_elections ||= 0
    self.current_step ||= :game_setup
  end

end
