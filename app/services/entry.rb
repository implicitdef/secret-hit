class Entry


  puts "-- Running Entry"

  #TODO code pour restart la websocket si error
  #TODO code pour sleep la websocket si inactive depuis un moment
  #TODO code pour restart la websocket si on se fait mention, via l'events API

  api = Slack::Api.new(ENV["slack_bot_token"])
  ws_client = Slack::WebsocketClient.new(url: api.get_websocket_url, bot_id: api.get_own_id)
  random = api.find_channel_by_name(:random)
  emmanuel = api.find_user_by_name(:emmanuel)

  team = Core::Engine.setup_team(api, ENV["slack_bot_token"])
  game = team.setup_game channel_id: random, requester: emmanuel

  Core::Engine.signal_game_setup_done api, ws_client, game





#  api = Slack::Api.new(ENV["slack_bot_token"])
#  ws_url = api.get_websocket_url
#  bot_id = api.get_own_id
#  channel = api.find_channel_by_name(:random)
#
#  ws_client = Slack::WebsocketClient.new(url: ws_url, bot_id: bot_id)
#
#  #sleep(5.seconds)
#  #ws_client.send(text: "Hey guys I'm in", to: channel)
#  ws_client.on_message do |hash|
#    pp hash
#    unless hash[:is_direct_message]
#      ws_client.send(text: "shut up !", to: hash[:channel])
#    end
#  end
#
#  puts "-- Done"
#  sleep(10.minute)
end