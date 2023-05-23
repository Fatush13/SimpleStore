CREATE TABLE store_schema.sale
(
    id UUID PRIMARY KEY,
    item_id UUID NOT NULL,
    quantity_sold INTEGER NOT NULL,
    FOREIGN KEY (item_id) REFERENCES store_schema.item(id)
);