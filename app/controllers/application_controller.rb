class ApplicationController < ActionController::API

  def index
    render json: {foo: Slack.hello}
  end

end
