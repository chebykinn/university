CREATE TABLE users (
  user_id bigserial CONSTRAINT users_pk PRIMARY KEY,
  email text NOT NULL,
  password text NOT NULL,
  name text NOT NULL,
  role text NOT NULL,
  approved boolean DEFAULT FALSE NOT NULL
);

CREATE UNIQUE INDEX users_email_uidx
  ON users (email);

CREATE TABLE bids (
  bid_id bigserial CONSTRAINT bids_pk PRIMARY KEY,
  customer_id bigint NOT NULL CONSTRAINT bids_customer_id_fk REFERENCES users (user_id),
  employee_id bigint CONSTRAINT bids_owner_id_fk REFERENCES users (user_id),
  state text NOT NULL,
  update_time timestamp DEFAULT now() NOT NULL,
  description jsonb NOT NULL
);
