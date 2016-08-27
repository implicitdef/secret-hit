class Entry

  puts "-- Running Entry"


  api = SlackApi.new(ENV["slack_bot_token"])
  ws_url = api.get_websocket_url
  bot_id = api.get_own_id
  channel = api.find_channel_by_name(:random)

  ws_client = SlackWebsocketClient.new(url: ws_url, bot_id: bot_id)

  #sleep(5.seconds)
  #ws_client.send(text: "Hey guys I'm in", to: channel)
  ws_client.on_message do |hash|
    pp hash
    unless hash[:is_direct_message]
      ws_client.send(text: "shut up !", to: hash[:channel])
    end
  end

  puts "-- Done"
  sleep(10.minute)
end