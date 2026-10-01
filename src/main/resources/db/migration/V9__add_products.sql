CREATE TABLE product_categories
(
    id               BIGSERIAL PRIMARY KEY,
    name             VARCHAR(100) NOT NULL,
    description      TEXT,
    long_description TEXT,
    salon_id         BIGINT NOT NULL,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_product_category_salon
        FOREIGN KEY (salon_id) REFERENCES salons (id) ON DELETE CASCADE
);

CREATE TABLE products
(
    id               BIGSERIAL PRIMARY KEY,
    category_id      BIGINT NOT NULL,
    name             VARCHAR(150) NOT NULL,
    description      TEXT,
    long_description TEXT,
    price            NUMERIC(10, 2),
    stock_quantity   INTEGER NOT NULL DEFAULT 0,
    is_active        BOOLEAN DEFAULT TRUE,
    salon_id         BIGINT NOT NULL,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES product_categories (id) ON DELETE CASCADE,
    CONSTRAINT fk_product_salon FOREIGN KEY (salon_id) REFERENCES salons (id) ON DELETE CASCADE,
    CONSTRAINT chk_product_stock_quantity CHECK (stock_quantity >= 0)
);

-- A sale item is now either a service line or a product line, never both/neither.
ALTER TABLE sale_items
    ALTER COLUMN service_id DROP NOT NULL,
    ALTER COLUMN user_id DROP NOT NULL,
    ALTER COLUMN service_name DROP NOT NULL,
    ADD COLUMN product_id BIGINT REFERENCES products (id),
    ADD COLUMN product_name VARCHAR(150);

ALTER TABLE sale_items
    ADD CONSTRAINT chk_sale_item_reference
        CHECK (
            (service_id IS NOT NULL AND product_id IS NULL)
                OR (service_id IS NULL AND product_id IS NOT NULL)
        );
