CREATE TABLE nodes (
  id              SERIAL PRIMARY KEY,
  name            VARCHAR(255) NOT NULL,
  address         VARCHAR(15)  NOT NULL UNIQUE,
  port            INTEGER      NOT NULL,
  update_interval INTEGER DEFAULT 0
);

CREATE TYPE pin_setting_type AS ENUM (
  'NONE',
  'READ_DIGITAL',
  'READ_ANALOG',
  'SWITCH',
  'TEMPERATURE',
  'HUMIDITY'
);

CREATE TABLE pin_settings (
  id            SERIAL PRIMARY KEY,
  node_id       INTEGER  NOT NULL REFERENCES Nodes (id),
  pin_index     SMALLINT NOT NULL,
  pin_number    SMALLINT NOT NULL,
  type          SMALLINT NOT NULL, -- TODO pin_setting_type
  configuration INTEGER  NOT NULL DEFAULT 0,
  CONSTRAINT uk_node_and_index UNIQUE (node_id, pin_index)
);

CREATE TABLE temperatures (
  id        BIGSERIAL PRIMARY KEY,
  node_id   INTEGER                     NOT NULL REFERENCES nodes ON DELETE RESTRICT,
  timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  value     NUMERIC(5, 2)               NOT NULL,
  UNIQUE (node_id, timestamp)
);

CREATE OR REPLACE FUNCTION
  temperatures_partition_function()
  RETURNS TRIGGER AS
  $BODY$
  DECLARE
    _new_time          INT;
    _tablename         TEXT;
    _startdate         TEXT;
    _enddate           TEXT;
    result             RECORD;
  BEGIN
    -- vezme aktualni hodnotu sloupce create_time a vyhodnoti jestli je to pulnoc
    _new_time := (extract(EPOCH FROM NEW.timestamp) :: BIGINT / 86400) :: INT * 86400;
    _startdate := to_char(to_timestamp(_new_time), 'YYYYMMDD');
    _tablename := 'temperatures' || '_' || _startdate;

    -- kontrola zda existuje pozadovana partition
    PERFORM 1
    FROM pg_catalog.pg_class c
      JOIN pg_catalog.pg_namespace n ON n.OID = c.relnamespace
    WHERE c.relkind = 'r'
          AND c.relname = _tablename;

    -- pokud partition neexistuje, musime ji vytvorit
    IF NOT FOUND
    THEN
      _enddate := _startdate :: TIMESTAMP + INTERVAL '1 day';
      EXECUTE 'CREATE TABLE ' || quote_ident(_tablename) ||
              ' (CHECK(timestamp >= ' || quote_literal(_startdate) ||
              ' AND timestamp < ' || quote_literal(_enddate) ||
              ')) INHERITS (temperatures)';
    END IF;

    -- insert aktualni radek do spravne partition
    EXECUTE 'INSERT INTO ' || quote_ident(_tablename) || ' VALUES ($1.*)'
    USING NEW;

    RETURN NULL;
  END;
  $BODY$
LANGUAGE plpgsql;

CREATE TRIGGER temperatures_partition_trigger
BEFORE INSERT ON temperatures
FOR EACH ROW EXECUTE PROCEDURE temperatures_partition_function();