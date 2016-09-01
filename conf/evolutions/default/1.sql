
# --- !Ups

CREATE TABLE team(
  id SERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  maybe TEXT CHECK (char_length(maybe) < 150)
);
