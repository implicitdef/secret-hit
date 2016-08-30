class ApplicationController < ActionController::API

  def index
    render json: {foo: 33}
  end

end
