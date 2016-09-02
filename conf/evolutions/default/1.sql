
# --- !Ups

CREATE TYPE gender AS ENUM ('male', 'female');

CREATE TABLE team(
  id SERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  gender GENDER NOT NULL,
  creation_date TIMESTAMP WITH TIME ZONE NOT NULL,
  maybe TEXT CHECK (char_length(maybe) < 150)
);
