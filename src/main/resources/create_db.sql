START TRANSACTION;

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

  CREATE OR REPLACE FUNCTION check_modifier_is_not_set_to_null()
    RETURNS TRIGGER AS $$
  BEGIN
    IF row(NEW.*) IS DISTINCT FROM row(OLD.*) AND row(NEW.modifier_id) IS NULL THEN
      NEW.modifier_id = OLD.modifier_id;
      RETURN NEW;
    ELSE
      RETURN NEW;
    END IF;
  END;
  $$ language 'plpgsql';

  CREATE TABLE entities(
    -- my metadata
    id SERIAL PRIMARY KEY,
    created TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
    modified TIMESTAMP WITHOUT TIME ZONE,
    token CHARACTER VARYING,
    is_enabled BOOLEAN DEFAULT false,

    -- user info
    name CHARACTER VARYING NOT NULL,
    email CHARACTER VARYING UNIQUE NOT NULL,
    picture_url CHARACTER VARYING,
    gender CHARACTER VARYING,
    birthday DATE NOT NULL,
    password_hash VARCHAR NOT NULL,

    -- fb metadata
    fb_id BIGINT UNIQUE, -- NOT NULL,
    fb_access_token CHARACTER VARYING, -- NOT NULL
    CONSTRAINT unique_email UNIQUE(email),
    CONSTRAINT valid_password_length CHECK(CHAR_LENGTH(password_hash) > 8)
  );

  CREATE TRIGGER update_entities_modtime BEFORE UPDATE ON entities FOR EACH ROW EXECUTE PROCEDURE update_modified_column();

  CREATE TABLE roles(
    id SERIAL PRIMARY KEY,
    label VARCHAR(20) NOT NULL,
    CONSTRAINT unique_label UNIQUE(label)
  );

  CREATE TABLE entity_roles (
    id SERIAL PRIMARY KEY,
    entity_id INT NOT NULL REFERENCES entities(id),
    role_id INT NOT NULL REFERENCES roles(id),
    CONSTRAINT unique_entity_roles UNIQUE(entity_id, role_id)
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
    description VARCHAR(40000),
    s3bucket VARCHAR NOT NULL,
    s3key VARCHAR NOT NULL,
    date_taken TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
    created TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
    modified TIMESTAMP WITHOUT TIME ZONE,

    CONSTRAINT valid_s3_bucket_length CHECK(CHAR_LENGTH(s3bucket) > 2 AND CHAR_LENGTH(s3bucket) < 64),
    CONSTRAINT valid_s3_key_length CHECK(CHAR_LENGTH(s3key) > 0 AND CHAR_LENGTH(s3key) < 1024),
    CONSTRAINT unique_s3_bucket_and_key UNIQUE(s3bucket, s3key)
  );

  CREATE TRIGGER update_topic_content_modtime BEFORE UPDATE ON topic_content FOR EACH ROW EXECUTE PROCEDURE update_modified_column();

  CREATE TABLE topic_member_statuses(
    id SERIAL PRIMARY KEY,
    label VARCHAR(20) NOT NULL
  );

  CREATE TABLE topic_members(
    id SERIAL PRIMARY KEY,
    entity_id INT NOT NULL REFERENCES entities(id),
    topic_id  INT NOT NULL REFERENCES topics(id),
    -- controls whether a topic member has sent an invite, the receiver has accepted or rejected it,
    -- or if the sender canceled the invite
    status_id INT NOT NULL REFERENCES topic_member_statuses(id) DEFAULT 1, -- default to pending

    creator_id  INT NOT NULL REFERENCES entities(id),
    modifier_id INT REFERENCES entities(id),
    created     TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
    modified    TIMESTAMP WITHOUT TIME ZONE,

    CONSTRAINT unique_topic_member UNIQUE(entity_id, topic_id)
  );

  CREATE TRIGGER check_topic_member_modifier_is_not_set_to_null BEFORE UPDATE ON topic_members FOR EACH ROW EXECUTE PROCEDURE check_modifier_is_not_set_to_null();
  CREATE TRIGGER update_topic_members_modtime BEFORE UPDATE ON topic_members FOR EACH ROW EXECUTE PROCEDURE update_modified_column();

  CREATE TABLE topic_permissions(
    id SERIAL PRIMARY KEY,
    label VARCHAR(20) NOT NULL
  );

  CREATE TABLE topic_entity_permissions(
    id SERIAL PRIMARY KEY,
    entity_id INT NOT NULL REFERENCES  entities(id),
    topic_id  INT NOT NULL REFERENCES topics(id),
    topic_permission_id INT NOT NULL REFERENCES topic_permissions(id),
    created  TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
    modified TIMESTAMP WITHOUT TIME ZONE,

    CONSTRAINT unique_permission UNIQUE(entity_id, topic_id, topic_permission_id)
  );

  CREATE TRIGGER update_topic_role_modtime BEFORE UPDATE ON topic_entity_permissions FOR EACH ROW EXECUTE PROCEDURE update_modified_column();

  INSERT INTO
    roles (id, label)
  VALUES
    (1, 'USER'),
    (2, 'ADMIN');

  INSERT INTO
    topic_member_statuses(id, label)
  VALUES
    (1, 'PENDING'),
    (2, 'REJECTED'),
    (3, 'ACCEPTED'),
    (4, 'RESCINDED'),
    (5, 'LEFT_TOPIC');

COMMIT;
