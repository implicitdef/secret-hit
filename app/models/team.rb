class Team < ApplicationRecord
  has_many :players, dependent: :destroy
  has_many :games, dependent: :destroy

  def setup_game(channel_id:, requester:)
    close_all_games
    game = games.create!(channel_id: channel_id)
    first_player = add_player_if_new id: requester
    game.register_game_player player: first_player
    game
  end

  def add_player_if_new(id:)
    players.where(user_id: id).first_or_create!(user_id: id)
  end


  private

  def close_all_games
    games.update_all(current_step: :over)
  end


end
