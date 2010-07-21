CREATE TABLE language(
  id INT NOT NULL,
  text VARCHAR(256) NOT NULL,  
  PRIMARY KEY(id)
);

CREATE INDEX language_text ON language(text);

CREATE TABLE symbol(
  id BIGINT NOT NULL,
  resource BOOLEAN NOT NULL, 
  lexical VARCHAR(1024) NOT NULL,
  datatype BIGINT NULL,
  lang INT NULL, 
  integer BIGINT NULL,
  floating DOUBLE NULL,
  datetime TIMESTAMP NULL,  
  PRIMARY KEY(id),
  CONSTRAINT FK_LANG FOREIGN KEY(lang) REFERENCES language(id)
);

CREATE INDEX symbol_lexical ON symbol(lexical);

CREATE TABLE statement(
  model BIGINT NULL,
  subject BIGINT NOT NULL,
  predicate BIGINT NOT NULL,
  object BIGINT NOT NULL,  
  CONSTRAINT FK_MODEL FOREIGN KEY(model) REFERENCES symbol(id),
  CONSTRAINT FK_SUBJECT FOREIGN KEY(subject) REFERENCES symbol(id),
  CONSTRAINT FK_PREDICATE FOREIGN KEY(predicate) REFERENCES symbol(id),
  CONSTRAINT FK_OBJECT FOREIGN KEY(object) REFERENCES symbol(id)  
);

CREATE INDEX statement_ms ON statement(model, subject);

CREATE INDEX statement_mo ON statement(model, object);

CREATE INDEX statement_msp ON statement(model, subject, predicate);

CREATE INDEX statement_mpo ON statement(model, predicate, object);