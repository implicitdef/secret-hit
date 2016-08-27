class Entry

  puts "-- Running Entry"

  #TODO code pour restart la websocket si error
  #TODO code pour sleep la websocket si inactive depuis un moment
  #TODO code pour restart la websocket si on se fait mention, via l'events API

  team = Core::Engine.setup_team(ENV["slack_bot_token"])
  puts "OK bots we setup'd the team"



  # Protocole a mettre en place :
  # recevoir un declenchement d'une team (simule l'integration externe)

  # demarrer une websocket
  # ecouter les messages pour declencher un game



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