module Slack
  #Represents an incoming message
  class WebsocketMessage
    attr_accessor :user
    attr_accessor :channel
    attr_accessor :text
    attr_accessor :is_direct_message
    attr_accessor :is_with_mention

    def initialize
      pp "INITIALIZE"
    end
  end
end