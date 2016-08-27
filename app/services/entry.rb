class Entry

  puts "-- Running Entry"


  api = SlackApi.new(ENV["slack_bot_token"])
  ws_url = api.get_websocket_url
  pp api.get_own_id
  channel = api.find_channel_by_name(:random)

  ws_client = SlackWebsocketClient.new(ws_url)

  sleep(5.seconds)
  ws_client.send(text: "Hey guys I'm in", to: channel)
  ws_client.on_message do |hash|
    puts ">>>> received a message :"
    pp hash
    hash['text']
    hash['channel']
  end

  puts "-- Done"
  sleep(10.minute)
end