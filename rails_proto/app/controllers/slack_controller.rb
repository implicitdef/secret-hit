class SlackController < ApplicationController

  def index
    body = JSON.parse(request.body.read)
    type = body['type']
    case type
      when 'url_verification'
        render plain: body['challenge']
      when 'minute_rate_limited'
        render plain: 'ok'
        logger.warn ">> warning [#{type}] #{im.text}"
      when 'message.channels', 'message.groups', 'message.im'
        im = Slack::IncomingMessage
        im.user = body['user']
        im.text = body['text']
        im.channel = body['channel']
        im.is_direct_message = im.channel.start_with?("D")
        logger.info ">> incoming [#{type}] #{im.text}"
        # TODO here send the message to our service
        render plain: 'ok'
      else
        render plain: "unhandled message type #{type}"
    end
  end


end
