CREATE TABLE sales
(
    id                      BIGSERIAL PRIMARY KEY,
    salon_id                BIGINT         NOT NULL,
    branch_id               BIGINT         NOT NULL,
    client_id               BIGINT         NOT NULL,
    registered_by_user_id   BIGINT         NOT NULL,
    appointment_id          BIGINT UNIQUE,
    subtotal                NUMERIC(12, 2) NOT NULL DEFAULT 0,
    discount_amount         NUMERIC(12, 2) NOT NULL DEFAULT 0,
    total_amount            NUMERIC(12, 2) NOT NULL DEFAULT 0,
    amount_paid             NUMERIC(12, 2) NOT NULL DEFAULT 0,
    status                  VARCHAR(30)    NOT NULL DEFAULT 'COMPLETED',
    notes                   TEXT,
    sold_at                 TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cancelled_at            TIMESTAMP,
    cancellation_reason     TEXT,
    created_at              TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sale_salon
        FOREIGN KEY (salon_id) REFERENCES salons (id),
    CONSTRAINT fk_sale_branch
        FOREIGN KEY (branch_id) REFERENCES branches (id),
    CONSTRAINT fk_sale_client
        FOREIGN KEY (client_id) REFERENCES clients (id),
    CONSTRAINT fk_sale_registered_by
        FOREIGN KEY (registered_by_user_id) REFERENCES users (id),
    CONSTRAINT fk_sale_appointment
        FOREIGN KEY (appointment_id) REFERENCES appointments (id),
    CONSTRAINT chk_sale_status
        CHECK (status IN ('COMPLETED', 'PARTIALLY_PAID', 'CANCELLED')),
    CONSTRAINT chk_sale_amounts
        CHECK (subtotal >= 0 AND discount_amount >= 0 AND total_amount >= 0 AND amount_paid >= 0)
);

CREATE TABLE sale_items
(
    id               BIGSERIAL PRIMARY KEY,
    sale_id          BIGINT         NOT NULL,
    service_id       BIGINT         NOT NULL,
    user_id          BIGINT         NOT NULL,
    appointment_id   BIGINT,
    service_name     VARCHAR(150)   NOT NULL,
    quantity         INTEGER        NOT NULL DEFAULT 1,
    unit_price       NUMERIC(10, 2) NOT NULL,
    discount_amount  NUMERIC(10, 2) NOT NULL DEFAULT 0,
    line_total       NUMERIC(12, 2) NOT NULL,
    created_at       TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sale_item_sale
        FOREIGN KEY (sale_id) REFERENCES sales (id) ON DELETE CASCADE,
    CONSTRAINT fk_sale_item_service
        FOREIGN KEY (service_id) REFERENCES services (id),
    CONSTRAINT fk_sale_item_user
        FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_sale_item_appointment
        FOREIGN KEY (appointment_id) REFERENCES appointments (id),
    CONSTRAINT chk_sale_item_quantity
        CHECK (quantity > 0),
    CONSTRAINT chk_sale_item_amounts
        CHECK (unit_price >= 0 AND discount_amount >= 0 AND line_total >= 0)
);

CREATE TABLE sale_payments
(
    id              BIGSERIAL PRIMARY KEY,
    sale_id         BIGINT         NOT NULL,
    amount          NUMERIC(12, 2) NOT NULL,
    payment_method  VARCHAR(30)    NOT NULL,
    reference       VARCHAR(100),
    paid_at         TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at      TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sale_payment_sale
        FOREIGN KEY (sale_id) REFERENCES sales (id) ON DELETE CASCADE,
    CONSTRAINT chk_sale_payment_method
        CHECK (payment_method IN ('CASH', 'CARD', 'TRANSFER', 'OTHER')),
    CONSTRAINT chk_sale_payment_amount
        CHECK (amount > 0)
);

CREATE INDEX idx_sales_salon_sold_at ON sales (salon_id, sold_at);
CREATE INDEX idx_sales_branch_sold_at ON sales (branch_id, sold_at);
CREATE INDEX idx_sales_client ON sales (client_id);
CREATE INDEX idx_sale_items_sale ON sale_items (sale_id);
CREATE INDEX idx_sale_items_user ON sale_items (user_id);
CREATE INDEX idx_sale_payments_sale ON sale_payments (sale_id);
