ALTER TABLE users DROP column approved;

CREATE TABLE company_reviews (
  company_review_id bigserial CONSTRAINT company_reviews_pk PRIMARY KEY,
  bid_id bigint NOT NULL CONSTRAINT company_reviews_bid_id_fk REFERENCES bids (bid_id),
  rating smallint NOT NULL,
  review text NOT NULL,
  update_time timestamp DEFAULT now() NOT NULL
);

CREATE UNIQUE INDEX company_reviews_bid_id_uidx ON company_reviews (bid_id);
CREATE INDEX company_reviews_update_time_idx ON company_reviews (update_time);

CREATE TABLE system_reviews (
  system_review_id bigserial CONSTRAINT system_reviews_pk PRIMARY KEY,
  user_id bigint NOT NULL CONSTRAINT system_reviews_user_id_fk REFERENCES users (user_id),
  rating smallint NOT NULL,
  review text NOT NULL,
  update_time timestamp DEFAULT now() NOT NULL
);

CREATE UNIQUE INDEX system_reviews_user_id_uidx ON system_reviews (user_id);
CREATE INDEX system_reviews_update_time_idx ON system_reviews (update_time);