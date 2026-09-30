ALTER TABLE appointments
    ADD COLUMN deposit_amount NUMERIC(12, 2),
    ADD COLUMN deposit_payment_method VARCHAR(30),
    ADD COLUMN deposit_paid_at TIMESTAMP;

ALTER TABLE appointments
    ADD CONSTRAINT chk_appointment_deposit_amount
        CHECK (deposit_amount IS NULL OR deposit_amount > 0),
    ADD CONSTRAINT chk_appointment_deposit_method
        CHECK (deposit_payment_method IS NULL
            OR deposit_payment_method IN ('CASH', 'CARD', 'TRANSFER', 'YAPE', 'PLIN', 'OTHER'));
