
CREATE TABLE words (
  id SERIAL PRIMARY KEY,
  word VARCHAR(255) NOT NULL
);

CREATE TABLE games (
  id SERIAL PRIMARY KEY,
  word_id INTEGER NOT NULL REFERENCES words(id),
  guess_allowed INTEGER NOT NULL
);

CREATE TABLE guesses (
  id SERIAL PRIMARY KEY,
  game_id INTEGER NOT NULL REFERENCES games(id),
  guess CHAR NOT NULL
);

CREATE TABLE game_results (
  id SERIAL PRIMARY KEY,
  game_id INTEGER NOT NULL REFERENCES games(id),
  won boolean,
  forfeit boolean
);

CREATE UNIQUE INDEX words_lower ON words ((lower(word)));