CREATE TABLE IF NOT EXISTS homes (
    id int AUTO_INCREMENT PRIMARY KEY,
    player uuid NOT NULL,
    world uuid NOT NULL,
    name varchar(50) NOT NULL,
    x int NOT NULL,
    y int NOT NULL,
    z int NOT NULL
);

CREATE INDEX IF NOT EXISTS homes_by_player ON homes (player, name);
