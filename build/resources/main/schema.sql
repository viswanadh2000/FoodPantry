-- src/main/resources/schema.sql

-- Site table
CREATE SEQUENCE IF NOT EXISTS site_id_seq;

CREATE TABLE IF NOT EXISTS site (
  id BIGINT PRIMARY KEY DEFAULT nextval('site_id_seq'),
  name VARCHAR(255) NOT NULL,
  address VARCHAR(255),
  city VARCHAR(100),
  state VARCHAR(2),
  zip VARCHAR(20)
);

ALTER TABLE site ALTER COLUMN id SET DEFAULT nextval('site_id_seq');
ALTER SEQUENCE site_id_seq OWNED BY site.id;
SELECT setval('site_id_seq',
              GREATEST(COALESCE((SELECT MAX(id) FROM site), 0) + 1, 1),
              false);

-- Inventory Item table
CREATE TABLE IF NOT EXISTS inventory_item (
  id BIGSERIAL PRIMARY KEY,
  site_id BIGINT REFERENCES site(id),
  sku VARCHAR(100),
  name VARCHAR(255),
  tags TEXT,
  qty INTEGER,
  unit VARCHAR(50)
);

-- Queue Token table
CREATE TABLE IF NOT EXISTS queue_token (
  id BIGSERIAL PRIMARY KEY,
  site_id BIGINT REFERENCES site(id),
  token_number VARCHAR(50) UNIQUE NOT NULL,
  status VARCHAR(20) NOT NULL,
  contact_name VARCHAR(255),
  contact_phone VARCHAR(50),
  estimated_wait_minutes INTEGER,
  created_at TIMESTAMP NOT NULL,
  called_at TIMESTAMP,
  completed_at TIMESTAMP
);

-- Audit Log table
CREATE TABLE IF NOT EXISTS audit_log (
  id BIGSERIAL PRIMARY KEY,
  username VARCHAR(255),
  action VARCHAR(100),
  entity VARCHAR(50),
  entity_id BIGINT,
  details TEXT,
  timestamp TIMESTAMP NOT NULL
);

-- Webhook table
CREATE TABLE IF NOT EXISTS webhook (
  id BIGSERIAL PRIMARY KEY,
  url VARCHAR(500) NOT NULL,
  active BOOLEAN NOT NULL,
  description TEXT,
  created_at TIMESTAMP NOT NULL,
  last_triggered_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS webhook_events (
  webhook_id BIGINT REFERENCES webhook(id) ON DELETE CASCADE,
  event_type VARCHAR(100)
);

-- Status table (if exists from original schema)
CREATE TABLE IF NOT EXISTS status (
  id BIGSERIAL PRIMARY KEY,
  site_id BIGINT REFERENCES site(id),
  status_type VARCHAR(50),
  value VARCHAR(255),
  updated_at TIMESTAMP
);

-- Seed data (idempotent)
INSERT INTO site (id, name, address, city, state, zip) VALUES
    (1, 'Westside Pantry', '123 Oak Ave', 'Austin', 'TX', '78701')
ON CONFLICT (id) DO NOTHING;
