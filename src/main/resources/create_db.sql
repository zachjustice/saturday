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

--DROP TABLE entities CASCADE;
--DROP TABLE roles CASCADE;
--DROP TABLE entity_roles CASCADE;

CREATE TABLE entities (
  -- my metadata
  id SERIAL PRIMARY KEY,
  created TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
  modified TIMESTAMP WITHOUT TIME ZONE,
  token CHARACTER VARYING,
  is_enabled BOOLEAN DEFAULT false,

  -- user info
  name CHARACTER VARYING, -- NOT NULL,
  email CHARACTER VARYING UNIQUE, --NOT NULL,
  picture_url CHARACTER VARYING, --NOT NULL,
  gender CHARACTER VARYING,
  birthday DATE,
  password_hash VARCHAR,

  -- fb metadata
  fb_id BIGINT UNIQUE, -- NOT NULL,
  fb_access_token CHARACTER VARYING -- NOT NULL
);

CREATE TRIGGER update_entities_modtime BEFORE UPDATE ON entities FOR EACH ROW EXECUTE PROCEDURE update_modified_column();

CREATE TABLE roles(
  id SERIAL PRIMARY KEY,
  label VARCHAR(20) NOT NULL
);

CREATE TABLE entity_roles (
  id SERIAL PRIMARY KEY,
  entity_id INT NOT NULL REFERENCES entities(id),
  role_id INT NOT NULL REFERENCES roles(id)
);

CREATE TABLE topics(
  id SERIAL PRIMARY KEY,
  name VARCHAR(20) NOT NULL,
  description VARCHAR(150),
  creator_id INT NOT NULL REFERENCES entities(id),
  created TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
  modified TIMESTAMP WITHOUT TIME ZONE
);

CREATE TRIGGER update_topics_modtime BEFORE UPDATE ON topics FOR EACH ROW EXECUTE PROCEDURE update_modified_column();

CREATE TABLE topic_content (
  id SERIAL PRIMARY KEY,
  topic_id INT NOT NULL REFERENCES topics(id),
  creator_id INT NOT NULL REFERENCES entities(id),
  title VARCHAR(20),
  description VARCHAR(40000),
  s3url VARCHAR NOT NULL,
  created TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
  modified TIMESTAMP WITHOUT TIME ZONE
);

CREATE TRIGGER update_topic_content_modtime BEFORE UPDATE ON topic_content FOR EACH ROW EXECUTE PROCEDURE update_modified_column();

CREATE TABLE topic_members(
  id SERIAL PRIMARY KEY,
  entity_id INT NOT NULL REFERENCES  entities(id),
  topic_id INT NOT NULL REFERENCES topics(id),
  created TIMESTAMP WITHOUT TIME ZONE DEFAULT now()
);

CREATE TABLE topic_permissions(
  id INT PRIMARY KEY,
  label VARCHAR(20) NOT NULL
);

CREATE TABLE topic_entity_permissions(
  id SERIAL PRIMARY KEY,
  entity_id INT NOT NULL REFERENCES  entities(id),
  topic_id INT NOT NULL REFERENCES topics(id),
  topic_permission_id INT NOT NULL REFERENCES topic_permissions(id),
  created TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
  modified TIMESTAMP WITHOUT TIME ZONE
);

CREATE TRIGGER update_topic_role_modtime BEFORE UPDATE ON topic_entity_permissions FOR EACH ROW EXECUTE PROCEDURE update_modified_column();

CREATE TABLE topic_invites(
  id SERIAL PRIMARY KEY,
  invitee_id INT NOT NULL REFERENCES  entities(id),
  invitor_id INT NOT NULL REFERENCES  entities(id),
  topic_id INT NOT NULL REFERENCES topics(id),
  created TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
  roles INT[]
);


INSERT INTO roles (id, label) VALUES (1, 'USER');
INSERT INTO roles (id, label) VALUES (2, 'MODERATOR');

delete from entity_roles;
delete from entities;
select * from entities;
