
# --- !Ups

CREATE TABLE slack_teams(
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  slack_team_id TEXT NOT NULL PRIMARY KEY CHECK (char_length(slack_team_id) < 150),
  slack_api_token TEXT NOT NULL CHECK (char_length(slack_api_token) < 150),
  slack_bot_id TEXT NOT NULL CHECK (char_length(slack_bot_id) < 150)
);


CREATE TABLE games(
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  slack_team_id TEXT NOT NULL REFERENCES slack_teams,
  game_id SERIAL PRIMARY KEY,
  slack_channel_id TEXT NOT NULL CHECK (char_length(slack_channel_id) < 150),
  game_state JSONB NOT NULL,
  completed_at TIMESTAMPTZ
);


