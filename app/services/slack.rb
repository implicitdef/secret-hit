class Slack



  def self.hello
    "Hello my dear"
  end


  def self.connect
    url = 'https://slack.com/api/rtm.start'
    response = HTTP.get(url, params: {
        token: ENV["slack_bot_token"],
        simple_latest: true,
        no_unreads: true
    })
    if response.code.between?(200,299)
      json = JSON.parse(response.to_s)
      if json["ok"]
        @@team = {
            id: json["team"]["id"],
            name: json["team"]["name"]
        }
        @@websocket_url = json["url"]
      else
        raise "Got #{json["error"]} for #{url}"
      end
    else
      raise "Got #{response.code} for #{url}"
    end
  end


  puts "foo"
  self.connect
  pp @@team
  pp @@websocket_url
  pp Slack.instance_variables
  #self.connect

end