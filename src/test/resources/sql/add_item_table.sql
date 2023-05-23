CREATE TABLE store_schema.item
(
    id UUID PRIMARY KEY,
    name TEXT NOT NULL UNIQUE,
    price DECIMAL(10,2) NOT NULL,
    quantity INTEGER NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);