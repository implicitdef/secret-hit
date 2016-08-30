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

    def self.signal_game_setup_done game_info
      game_info.send "We're starting a new game of Secret Hitler. #{describe_game_players(game_info)}"
      game_info.send "Say 'join' or 'leave' to join or leave the game."
    end

    def self.update_players_list game_info
      game_info.send describe_game_players(game_info)
    end

    def self.dispatch_message game_info, ws_message
      #TODO debug all of that, its not working
      puts "Received message"
      case game_info.game.current_step
        when 'game_setup'
          puts "Correct step"
          if ws_message.channel == game_info.channel
            puts "Correct channel"
            case ws_message.text
              when 'join'
                puts "Correct text"
                handle_join game_info, ws_message.user
              when 'leave'
                handle_leave game_info, ws_message.user
              else
            end
          end
      else
      end
    end

    def self.handle_join game_info, user_id
      puts "Handling join message by #{user_id}"
      #TODO fix le pb des IDs
      #unless game_info.game.has_game_player player_id: user_id
      #  player = game_info.game.team.add_player_if_new id: user_id
      #  game_info.game.register_game_player player: player
      #  update_players_list game_info
      #end
    end

    def self.handle_leave game_info, user_id
      puts "Handling leave message by #{user_id}"
      #if game_info.game.has_game_player player_id: user_id
      #  game_info.game.remove_game_player player_id: user_id
      #  update_players_list game_info
      #end
    end

    private

    def self.describe_game_players game_info
      nb = game_info.game.game_players.size
      word = nb < 2 ? "player" : "player"
      mentions = game_info.game.game_players.map do |gp|
        "@" + game_info.api.find_user_name(gp.player.user_id)
      end
      "#{nb} #{word} so far : #{mentions.join(", ")}"
    end





  end
end