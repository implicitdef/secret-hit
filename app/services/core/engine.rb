module Core
  class Engine

    def self.setup_team(api_token)
      api = Slack::Api.new(api_token)
      team_id = api.get_team_id
      team = Team.find_by(team_id: team_id)
      attrs = {
        api_token: api_token,
        team_id: team_id,
        team_name: api.get_team_name,
        bot_id: api.get_own_id
      }
      pp attrs
      if team.nil?
        team = Team.new(attrs)
        team.save!
      else
        team.update!(attrs)
      end
      team
    end




  end
end