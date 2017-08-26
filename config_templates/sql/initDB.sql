DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS groups CASCADE;
DROP TABLE IF EXISTS projects CASCADE;
DROP TABLE IF EXISTS cities CASCADE;
DROP TABLE IF EXISTS grouprefs CASCADE;
DROP TABLE IF EXISTS project_groups CASCADE;
DROP TYPE IF EXISTS USER_FLAG;
DROP TYPE IF EXISTS GROUP_TYPE;
DROP SEQUENCE IF EXISTS user_seq;
DROP SEQUENCE IF EXISTS all_seq;


CREATE TYPE USER_FLAG AS ENUM ('active', 'deleted', 'superuser');

CREATE SEQUENCE user_seq START 100000;
CREATE SEQUENCE all_seq START 1000;

CREATE TABLE cities (
  id    TEXT PRIMARY KEY,
  value TEXT NOT NULL
);

CREATE TABLE users (
  id        INTEGER PRIMARY KEY DEFAULT nextval('user_seq'),
  full_name TEXT      NOT NULL,
  email     TEXT      NOT NULL,
  city      TEXT REFERENCES cities (id) ON DELETE RESTRICT,
  flag      USER_FLAG NOT NULL
);

CREATE UNIQUE INDEX email_idx
  ON users (email);

CREATE TYPE GROUP_TYPE AS ENUM ('REGISTERING', 'CURRENT', 'FINISHED');

CREATE TABLE groups (
  id   INTEGER PRIMARY KEY DEFAULT nextval('all_seq'),
  name TEXT       NOT NULL,
  type GROUP_TYPE NOT NULL
);

CREATE TABLE projects (
  id          INTEGER PRIMARY KEY DEFAULT nextval('all_seq'),
  name        TEXT NOT NULL,
  description TEXT NOT NULL
  --   p_groups INTEGER REFERENCES groups(id) ON DELETE RESTRICT
);


CREATE TABLE project_groups (
  project_id INTEGER REFERENCES projects (id) ON DELETE RESTRICT,
  group_id   INTEGER REFERENCES groups (id) ON DELETE CASCADE,
  PRIMARY KEY (project_id, group_id)
);

CREATE TABLE groupRefs (
  user_id  INTEGER REFERENCES users (id) ON DELETE RESTRICT,
  group_id INTEGER REFERENCES groups (id) ON DELETE RESTRICT,
  PRIMARY KEY (user_id, group_id)
);
