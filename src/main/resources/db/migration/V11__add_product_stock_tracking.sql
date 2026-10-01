ALTER TABLE products
    ADD COLUMN low_stock_threshold INTEGER NOT NULL DEFAULT 5;

CREATE TABLE product_stock_movements
(
    id                  BIGSERIAL PRIMARY KEY,
    product_id          BIGINT      NOT NULL,
    movement_type       VARCHAR(30) NOT NULL,
    quantity_delta      INTEGER     NOT NULL,
    resulting_stock     INTEGER     NOT NULL,
    reason              TEXT,
    sale_id             BIGINT,
    created_by_user_id  BIGINT,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_stock_movement_product
        FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE,
    CONSTRAINT fk_stock_movement_sale
        FOREIGN KEY (sale_id) REFERENCES sales (id),
    CONSTRAINT fk_stock_movement_user
        FOREIGN KEY (created_by_user_id) REFERENCES users (id),
    CONSTRAINT chk_stock_movement_type
        CHECK (movement_type IN ('RESTOCK', 'ADJUSTMENT', 'SALE', 'SALE_CANCELLED'))
);

CREATE INDEX idx_stock_movements_product ON product_stock_movements (product_id, created_at DESC);
