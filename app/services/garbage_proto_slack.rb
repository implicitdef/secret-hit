class GarbageProtoSlack



  def self.hello
    "Hello my dear"
  end


  def self.prepare
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


  def self.start_websocket
    WebSocket::Client::Simple.connect @@websocket_url do |ws|
      ws.on :message do |m|
        msg = JSON.parse(m.data)
        type = msg["type"]
        if type == "error"
          puts "ws [ERROR] >>> #{msg}"
        else
          puts "ws [#{type}]>> #{msg}"
        end
      end
      ws.on :close do |e|
        puts "Websocket connection closed"
        puts e
      end
      ws.on :error do |e|
        puts "Websocket connection error"
        p e
      end
    end
  end



  puts "foo"
  self.prepare
  self.start_websocket
  sleep(2.seconds)

end