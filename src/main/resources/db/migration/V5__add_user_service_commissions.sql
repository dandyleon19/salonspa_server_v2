CREATE TABLE user_service_commissions
(
    id                    BIGSERIAL PRIMARY KEY,
    user_id               BIGINT        NOT NULL,
    service_id            BIGINT        NOT NULL,
    commission_percentage NUMERIC(6, 2) NOT NULL,
    created_at            TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_service_commission_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_service_commission_service
        FOREIGN KEY (service_id) REFERENCES services (id) ON DELETE CASCADE,
    CONSTRAINT uq_user_service_commission
        UNIQUE (user_id, service_id),
    CONSTRAINT chk_user_service_commission_percentage
        CHECK (commission_percentage >= 0 AND commission_percentage <= 100)
);

CREATE INDEX idx_user_service_commissions_user ON user_service_commissions (user_id);
CREATE INDEX idx_user_service_commissions_service ON user_service_commissions (service_id);
