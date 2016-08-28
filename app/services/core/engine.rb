module Core
  class Engine

    def self.setup_team(api, api_token)
      team_id = api.get_team_id
      team = Team.find_by(team_id: team_id)
      attrs = {
        api_token: api_token,
        team_id: team_id,
        team_name: api.get_team_name,
        bot_id: api.get_own_id
      }
      if team.nil?
        team = Team.new(attrs)
        team.save!
      else
        team.update!(attrs)
      end
      team
    end

    def self.signal_game_setup_done api, ws_client, game
      ws_client.send text: "We're starting a new game of Secret Hitler. #{describe_game_players(api, game)}", to: game.channel_id
      ws_client.send text: "Say 'join' or 'leave' to join or leave the game.", to: game.channel_id
    end





    private

    def self.describe_game_players api, game
      nb = game.game_players.size
      word = nb < 2 ? "player" : "player"
      mentions = game.game_players.map do |gp|
        "@" + api.find_user_name(gp.player.user_id)
      end
      "#{nb} #{word} so far : #{mentions.join(", ")}"
    end





  end
end