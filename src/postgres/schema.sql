--
-- This schema has 2 vector tables. The first one is the default vector table the
-- spring ai framework knows how to use. The second one is a custom vector table that has
-- extra fields for hybrid search. The extra fields are for storing more metadata for
-- hybrid queries and maintaining the sync of the record with external store.
-- The demo app uses only the first vector table, but the second one works if you want to
-- write custom vector store code for it.
--

-- Database: aidemo

-- DROP DATABASE IF EXISTS aidemo;

CREATE DATABASE aidemo
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.utf8'
    LC_CTYPE = 'en_US.utf8'
    LOCALE_PROVIDER = 'libc'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;

-- temp table just to check database works with non-vector tables too.
create table if not exists Person (
    id serial primary key,
    name varchar(100) not null,
    age int not null
);

-- Run this only once to create the required extensions and tables.
CREATE EXTENSION IF NOT EXISTS vector;
CREATE EXTENSION IF NOT EXISTS hstore;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- This is the vector table that spring ai uses when using their
-- VectorStore. We are creating one here so we can use VectorStore supported
-- by the frameork.
CREATE TABLE IF NOT EXISTS vector_store (
	id uuid DEFAULT uuid_generate_v4() PRIMARY KEY,
	content text,
	metadata json,
	-- 1536 is the default embedding dimension
	embedding vector(1536)
);

CREATE INDEX ON vector_store USING HNSW (embedding vector_cosine_ops);



-- We also create an example vector store table with extra fields on it for hybrid search
-- 1536 is the default embedding dimension.
-- One of the use cases will be that we get a hit on vector search, but the
-- content in the vector is not what we want to use as the response. Example,
-- finding a restaurant, what we need is the restaurant_id and a fresh query to get
-- the restaurant info for filling out a card in the UI.
CREATE TABLE IF NOT EXISTS demo_vector_store (
  id uuid DEFAULT uuid_generate_v4() PRIMARY KEY,
  train_code char(3) DEFAULT 'ALL', -- the train this data applies to, used as a filter, 'ALL' means all trains.
  tags text[],    -- tags for searching content. need enum value range.
  source json,    -- app-defined metadata for the original src of data.
  response json,  -- app-defined short response data to use in case gen ai should not use the vector data for response.
  kvp hstore,     -- hstore for key/value pairs. key and value must be strings.
  content text,   -- the content stored in vector
  embedding vector(1536),
  created TIMESTAMP DEFAULT NOW(),
  updated TIMESTAMP DEFAULT NOW() -- use trigger to update value.
);

CREATE INDEX ON demo_vector_store USING HNSW (embedding vector_cosine_ops);


-- ===================================
-- Trigger to update the updated timestamp on update.
-- ===================================
CREATE OR REPLACE FUNCTION update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_updated_timestamp
BEFORE UPDATE ON demo_vector_store
FOR EACH ROW
EXECUTE FUNCTION update_timestamp();
-- ===================================