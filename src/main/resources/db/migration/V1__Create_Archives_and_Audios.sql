CREATE TABLE IF NOT EXISTS archives (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name VARCHAR UNIQUE,
  path VARCHAR,
  syncDate Date
);

CREATE TABLE IF NOT EXISTS audios (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  archive_id INTEGER,
  name VARCHAR,
  path VARCHAR,
  playbackTime INTEGER,
  album_id INTEGER,
  album_order INTEGER,
  FOREIGN KEY(archive_id) REFERENCES archives(id)
);
