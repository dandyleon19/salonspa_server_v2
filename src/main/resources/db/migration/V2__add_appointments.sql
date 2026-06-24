ALTER TABLE services
    ADD COLUMN duration_minutes INTEGER NOT NULL DEFAULT 60;

CREATE TABLE appointments
(
    id              BIGSERIAL PRIMARY KEY,
    client_id       BIGINT       NOT NULL,
    user_id         BIGINT       NOT NULL,
    branch_id       BIGINT       NOT NULL,
    salon_id        BIGINT       NOT NULL,
    service_id      BIGINT,
    start_at        TIMESTAMP    NOT NULL,
    end_at          TIMESTAMP    NOT NULL,
    status          VARCHAR(30)  NOT NULL DEFAULT 'SCHEDULED',
    notes           TEXT,
    cancelled_at    TIMESTAMP,
    cancellation_reason TEXT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_appointment_client
        FOREIGN KEY (client_id) REFERENCES clients (id),
    CONSTRAINT fk_appointment_user
        FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_appointment_branch
        FOREIGN KEY (branch_id) REFERENCES branches (id),
    CONSTRAINT fk_appointment_salon
        FOREIGN KEY (salon_id) REFERENCES salons (id),
    CONSTRAINT fk_appointment_service
        FOREIGN KEY (service_id) REFERENCES services (id),
    CONSTRAINT chk_appointment_time
        CHECK (end_at > start_at),
    CONSTRAINT chk_appointment_status
        CHECK (status IN (
            'SCHEDULED', 'CONFIRMED', 'IN_PROGRESS',
            'COMPLETED', 'CANCELLED', 'NO_SHOW'
        ))
);

CREATE INDEX idx_appointments_user_start
    ON appointments (user_id, start_at)
    WHERE status NOT IN ('CANCELLED', 'NO_SHOW');

CREATE INDEX idx_appointments_branch_start
    ON appointments (branch_id, start_at);

CREATE INDEX idx_appointments_salon_start
    ON appointments (salon_id, start_at);

ALTER TABLE clinical_records
    ADD COLUMN follow_up_appointment_id BIGINT UNIQUE,
    ADD CONSTRAINT fk_record_follow_up_appointment
        FOREIGN KEY (follow_up_appointment_id) REFERENCES appointments (id);
