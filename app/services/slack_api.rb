class SlackApi

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

  def find_channel_by_name(name)
    json = do_call!('channels.list', params: {
        exclude_archived: true
    })

    channel = json['channels'].find { |c| c['name'] == name.to_s}
    (channel || {})['id']
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
      raise "Got #{json["error"]} for #{url}"
    end
    json
  end



end