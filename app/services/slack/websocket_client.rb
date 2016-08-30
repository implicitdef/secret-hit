module Slack
  class WebsocketClient

    def initialize(url:, bot_id:)
      @id_counter = 0
      @disconnected = false
      @url = url
      @bot_id = bot_id
      @ws = WebSocket::Client::Simple.connect @url do |ws|
        ws.on :close do |e|
          puts "Websocket connection closed"
          @disconnected = true
        end
        ws.on :error do |e|
          puts "Websocket connection error"
          p e
        end
        ws.on :message do |msg|
          begin
            json = JSON.parse(msg.data)
            if json['type'] == 'error'
              puts "Websocket error #{json}"
            end
          rescue JSON::ParserError
            # we get some empty messages sometimes for some reason
          end
        end
      end
    end

    def on_message
      raise "You need to provide a block" unless block_given?
      raise "Can't listen, websocket is disconnected" if @ws.nil?
      bot_id = @bot_id
      # TODO change to a better websocket gem
      # TODO Why do we need this line first to be able to call the same constructor in the 'on' part
      #Slack::WebsocketMessage.new
      @ws.on :message do |msg|
        begin
          puts "ws > #{msg.data}"
          json = JSON.parse(msg.data)
          if json['type'] == 'message' && !json['text'].nil?
            user = json['user']
            channel = json['channel']
            text = json['text']
            unless user == bot_id
              begin
                puts "OVER"
                m = Slack::WebsocketMessage.new
                puts "UNDER"
              rescue
                puts "RESCUED"
              end
              m.user = user
              m.channel = channel
              m.text = text
              m.is_direct_message =  channel.start_with?("D")
              m.is_with_mention = text.include?("<@#{bot_id}>")
              yield m
            end
          end
        rescue JSON::ParserError
          # we get some empty messages sometimes for some reason
        end
      end
    end

    def send(text:, to:)
      raise "Can't listen, websocket is disconnected" if @ws.nil?
      @ws.send({
        id: @id_counter,
        type: :message,
        channel: to,
        text: text
      }.to_json)
      @id_counter += 1
    end



  end
end
