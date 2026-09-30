ALTER TABLE sale_payments
    DROP CONSTRAINT chk_sale_payment_method;

ALTER TABLE sale_payments
    ADD CONSTRAINT chk_sale_payment_method
        CHECK (payment_method IN ('CASH', 'CARD', 'TRANSFER', 'YAPE', 'PLIN', 'OTHER'));
