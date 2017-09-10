CREATE OR REPLACE FUNCTION update_modified_column()
  RETURNS TRIGGER AS $$
BEGIN
  IF row(NEW.*) IS DISTINCT FROM row(OLD.*) THEN
    NEW.modified = now();
    RETURN NEW;
  ELSE
    RETURN OLD;
  END IF;
END;
$$ language 'plpgsql';

DROP TABLE  entities;
DROP TABLE roles;
DROP TABLE entity_roles;

CREATE TABLE entities (
  id INTEGER PRIMARY KEY ,
  name CHARACTER VARYING NOT NULL,
  created TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
  modified TIMESTAMP WITHOUT TIME ZONE,
  email CHARACTER VARYING NOT NULL,
  password_hash CHARACTER VARYING NOT NULL,
  is_enabled BOOLEAN DEFAULT false,
  token CHARACTER VARYING,
  fb_app_token CHARACTER VARYING
);

CREATE TRIGGER update_entities_modtime BEFORE UPDATE ON entities FOR EACH ROW EXECUTE PROCEDURE update_modified_column();

CREATE TABLE roles(
  id INTEGER PRIMARY KEY,
  label CHARACTER VARYING NOT NULL
);

CREATE TABLE entity_roles (
  id INTEGER PRIMARY KEY,
  entity_id INTEGER NOT NULL REFERENCES entities(id),
  role_id INTEGER NOT NULL REFERENCES roles(id)
)
