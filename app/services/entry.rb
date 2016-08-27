class Entry

  puts "-- Running Entry"


  api = SlackApi.new(ENV["slack_bot_token"])
  ws_url = api.get_websocket_url
  channel = api.find_channel_by_name(:random)

  ws_client = SlackWebsocketClient.new(ws_url)
  #ws_client.send(text: "Hey guys", to: channel)

  puts "-- Done"
  sleep(3.seconds)
end