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
  id SERIAL PRIMARY KEY,
  password_hash VARCHAR,
  created TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
  modified TIMESTAMP WITHOUT TIME ZONE,
  token CHARACTER VARYING,
  is_enabled BOOLEAN DEFAULT false,

  name CHARACTER VARYING NOT NULL,
  email CHARACTER VARYING NOT NULL,
  picture_url CHARACTER VARYING NOT NULL,
  gender CHARACTER VARYING,
  fb_id BIGINT NOT NULL,
  fb_access_token CHARACTER VARYING NOT NULL
);

CREATE TRIGGER update_entities_modtime BEFORE UPDATE ON entities FOR EACH ROW EXECUTE PROCEDURE update_modified_column();

CREATE TABLE roles(
  id SERIAL PRIMARY KEY,
  label CHARACTER VARYING NOT NULL
);

CREATE TABLE entity_roles (
  id SERIAL PRIMARY KEY,
  entity_id INTEGER NOT NULL REFERENCES entities(id),
  role_id INTEGER NOT NULL REFERENCES roles(id)
);


INSERT INTO roles (id, label) VALUES (1, 'USER');
