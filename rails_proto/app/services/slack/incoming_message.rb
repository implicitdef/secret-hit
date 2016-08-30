module Slack
  class IncomingMessage
    attr_accessor :user
    attr_accessor :channel
    attr_accessor :text
    attr_accessor :is_direct_message
  end
end