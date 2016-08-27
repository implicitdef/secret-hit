class SlackWebsocketClient



  def initialize(url)
    @id_counter = 0
    @disconnected = false
    @url = url
    @callbacks = []
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
        json = JSON.parse(msg)
        type = json['type'].to_sym
        if type == :error
          puts "Websocket error #{json}"
        else
          puts "Received a #{type}"
          @callbacks
            .filter {|t, block| t == type}
            .each {|t, block| block.call }

        end
      end
    end
  end

  def on(type, &block)
    @callbacks.push({type => block})
  end

  def send(text:, to:)
    if @ws.nil?
      raise "Can't send, websocket not connected yet"
    end
    if @disconnected
      raise "Cna't send, websocket is disconnected"
    end
    @ws.send({
      id: @id_counter,
      type: :message,
      channel: to,
      text: text
    }.to_json)
    @id_counter += 1
  end



end