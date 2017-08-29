CREATE TABLE email_result (
  id        INTEGER PRIMARY KEY DEFAULT nextval('common_seq'),
  email     TEXT      NOT NULL,
  messageId TEXT      NOT NULL,
  date      TIMESTAMP NOT NULL
);