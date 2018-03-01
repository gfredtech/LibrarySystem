CREATE TABLE IF NOT EXISTS item (
  price INT NOT NULL CHECK (price >= 0),
  title TEXT NOT NULL,
  keywords TEXT[],
  copy_num INT NOT NULL CHECK (copy_num >= 1)
);

CREATE SEQUENCE IF NOT EXISTS item_id_seq;


CREATE TABLE IF NOT EXISTS book (
  book_id INT4 DEFAULT nextval('item_id_seq') PRIMARY KEY ,
  authors TEXT[] CHECK (array_length(authors, 1) >= 1),
  publication_date DATE NOT NULL,
  publisher TEXT NOT NULL,
  is_bestseller BOOLEAN DEFAULT FALSE,
  is_reference BOOLEAN DEFAULT FALSE,
  CHECK (NOT(is_bestseller = TRUE AND is_reference = TRUE))
) INHERITS (item);



CREATE TABLE IF NOT EXISTS journal_issue (
  issue_id INT4 DEFAULT nextval('item_id_seq') PRIMARY KEY,
  editors TEXT[] CHECK (array_length(editors, 1) >= 1),
  publisher TEXT NOT NULL,
  publication_date DATE NOT NULL,
  is_reference BOOLEAN DEFAULT FALSE
) INHERITS (item);



CREATE TABLE IF NOT EXISTS article (
  title TEXT NOT NULL,
  authors TEXT[] CHECK (array_length(authors, 1) >= 1),
  keywords TEXT[],
  journal_id SERIAL REFERENCES journal_issue(journal_issue_id) ON DELETE CASCADE,
  PRIMARY KEY (title, journal_id)
);



CREATE TABLE IF NOT EXISTS av_material (
  material_id INT4 DEFAULT nextval('item_id_seq') NOT NULL,
  authors TEXT[] CHECK (array_length(authors, 1) >= 1)
) INHERITS (item);



CREATE TABLE IF NOT EXISTS user_card (
  user_id SERIAL PRIMARY KEY,
  login TEXT NOT NULL,
  password_hash INT NOT NULL,
  name TEXT NOT NULL,
  address TEXT NOT NULL,
  phone_number TEXT NOT NULL,
  type TEXT NOT NULL,
  subtype TEXT
);



CREATE TABLE IF NOT EXISTS checkout (
  item_id INT4,
  item_type TEXT,
  user_id INT4 REFERENCES user_card,
  due_date DATE NOT NULL
);
