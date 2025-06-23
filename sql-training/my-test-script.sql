CREATE TABLE IF NOT EXISTS products (
    product_id bigint,
    some_id bigint,
    product_name text,
    PRIMARY KEY (product_id, some_id)
);

CREATE TABLE IF NOT EXISTS "order" (
    order_id bigint PRIMARY KEY,
    product_id bigint,
    some_id bigint,
    FOREIGN KEY (product_id, some_id) REFERENCES products (product_id, some_id)
);

ALTER TABLE products ADD COLUMN description text constraint desc_check CHECK (description <> '');
ALTER TABLE products DROP COLUMN product_name;