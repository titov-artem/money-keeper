CREATE TABLE IF NOT EXISTS account (
  id          BIGSERIAL    NOT NULL PRIMARY KEY,
  name        VARCHAR(512) NOT NULL,
  parser_type VARCHAR(128) NOT NULL
);

CREATE UNIQUE INDEX ON account (name);

CREATE TABLE IF NOT EXISTS category (
  id           BIGSERIAL       NOT NULL PRIMARY KEY,
  name         VARCHAR(512)    NOT NULL,
  alternatives VARCHAR(521) [] NOT NULL
);

CREATE UNIQUE INDEX ON category (name);

CREATE TABLE IF NOT EXISTS store (
  id          BIGSERIAL    NOT NULL PRIMARY KEY,
  name        VARCHAR(512) NOT NULL,
  category_id BIGINT       NOT NULL REFERENCES category (id)
);

CREATE TABLE IF NOT EXISTS sale_point (
  id           BIGSERIAL    NOT NULL PRIMARY KEY,
  name         VARCHAR(512) NOT NULL,
  raw_category VARCHAR(521) NOT NULL,
  store_id     BIGINT DEFAULT NULL REFERENCES store (id)
);

CREATE TABLE IF NOT EXISTS transaction (
  id            BIGSERIAL      NOT NULL PRIMARY KEY,
  account_id    BIGINT         NOT NULL  REFERENCES account (id),
  date          DATE           NOT NULL,
  sale_point_id BIGINT         NOT NULL REFERENCES sale_point (id),
  amount        NUMERIC(15, 5) NOT NULL,
  file_hash     VARCHAR(256)   NOT NULL,
  upload_id     VARCHAR(128)   NOT NULL
);

CREATE TABLE IF NOT EXISTS category_manual_mapping (
  store_id    BIGINT NOT NULL REFERENCES store (id),
  category_id BIGINT NOT NULL REFERENCES category (id),
  PRIMARY KEY (store_id, category_id)
);