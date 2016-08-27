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
      @ws.on :message do |msg|
        begin
          json = JSON.parse(msg.data)
          if json['type'] == 'message' && !json['text'].nil?
            user = json['user']
            channel = json['channel']
            text = json['text']
            unless user == bot_id
              yield({
                user: user,
                channel: channel,
                text: text,
                is_direct_message:  channel.start_with?("D"),
                is_with_mention: text.include?("<@#{bot_id}>")
              })
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
