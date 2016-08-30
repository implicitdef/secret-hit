module Slack
  class Api
    BASE = 'https://slack.com/api/'

    def initialize(token)
      @token = token
    end

    def get_websocket_url
      json = do_call!('rtm.start', params: {
          simple_latest: true,
          no_unreads: true
      })
      json["url"]
    end
    def get_own_id
      json = do_call!('auth.test')
      json['user_id']
    end
    def get_team_name
      json = do_call!('auth.test')
      json['team']
    end
    def get_team_id
      json = do_call!('auth.test')
      json['team_id']
    end
    def find_channel_by_name(name)
      json = do_call!('channels.list', params: {
          exclude_archived: true
      })
      channel = json['channels'].find { |c| c['name'] == name.to_s}
      (channel || {})['id']
    end
    def find_user_by_name(name)
      json = do_call!('users.list', params: {
          exclude_archived: true
      })
      channel = json['members'].find { |c| c['name'] == name.to_s}
      (channel || {})['id']
    end
    def find_user_name(id)
      json = do_call!('users.info', params: {
          user: id
      })
      json['user']['name']
    end

    private

    def do_call!(path, params: {})
      url = BASE + path
      params[:token] = @token
      response = HTTP.get(url, params: params)
      unless response.code.between?(200, 299)
        raise "Got #{response.code} for #{url}"
      end
      json = JSON.parse(response.to_s)
      unless json["ok"]
        raise "Got error #{json["error"]} for #{url}"
      end
      json
    end
  end
end