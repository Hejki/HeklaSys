CREATE TABLE rooms
(
  id    SERIAL NOT NULL PRIMARY KEY,
  name  VARCHAR(255),
  floor SMALLINT
) WITHOUT OIDS;

CREATE TABLE devices
(
  id         SERIAL       NOT NULL PRIMARY KEY,
  type       INTEGER      NOT NULL,
  identifier VARCHAR(255) NOT NULL,
  node_id    INTEGER,
  room_id    INTEGER      NOT NULL REFERENCES rooms (id) ON DELETE RESTRICT,
  created    TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
) WITHOUT OIDS;

ALTER TABLE devices ADD CONSTRAINT device_node_fk
FOREIGN KEY (node_id) REFERENCES devices (id)
ON DELETE CASCADE;

CREATE TABLE device_properties
(
  id           SERIAL      NOT NULL PRIMARY KEY,
  device_id    INTEGER     NOT NULL REFERENCES devices (id) ON DELETE CASCADE,
  name         VARCHAR(32) NOT NULL,
  value        TEXT
);

CREATE TABLE messages
(
  id               SERIAL  NOT NULL PRIMARY KEY,
  type             SMALLINT NOT NULL,
  identifier       SMALLINT NOT NULL,
  node_id          INTEGER NOT NULL REFERENCES devices (id) ON DELETE CASCADE,
  request_data     BYTEA,
  request_send     TIMESTAMP WITHOUT TIME ZONE,
  response_receive TIMESTAMP WITHOUT TIME ZONE,
  response_type    SMALLINT,
  response_data    BYTEA
) WITHOUT OIDS;