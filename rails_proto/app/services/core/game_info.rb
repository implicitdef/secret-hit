module Core
  class GameInfo
    attr_accessor :api
    attr_accessor :ws_client
    attr_accessor :game

    def initialize(api, ws_client, game)
      @api = api
      @ws_client = ws_client
      @game = game
    end

    def to_s
      "GameInfo[game=#{@game.id} api ws_client]"
    end

    def channel
      @game.channel_id
    end

    def send text
      @ws_client.send(
          text: text,
          to: channel
      )
    end

  end
end