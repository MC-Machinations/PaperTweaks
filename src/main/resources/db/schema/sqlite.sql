CREATE TABLE IF NOT EXISTS homes (
    id INTEGER PRIMARY KEY,
    player varchar(32) NOT NULL,
    world varchar(32) NOT NULL,
    name varchar(50) NOT NULL,
    x INTEGER NOT NULL,
    y INTEGER NOT NULL,
    z INTEGER NOT NULL
);

CREATE INDEX IF NOT EXISTS homes_by_player ON homes (player, name);
