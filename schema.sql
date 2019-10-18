DROP TABLE IF EXISTS lift;
DROP TABLE IF EXISTS resort;

CREATE TABLE lift(
    lift_id INT PRIMARY KEY AUTOINCREMENT,
    resort_id INT NOT NULL,
    season_id TEXT NOT NULL,
    day_id TEXT NOT NULL,
    skier_id INT NOT NULL,
    lift_time INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
);
CREATE TABLE resort(
    resort_id INT PRIMARY KEY AUTOINCREMENT,
    resort_name TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
);

