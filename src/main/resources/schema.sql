CREATE TABLE IF NOT EXISTS salons
(
    id             BIGSERIAL PRIMARY KEY,
    name           VARCHAR(100) NOT NULL,
    social_reason  VARCHAR(100),
    fiscal_address VARCHAR(100),
    ruc_number     VARCHAR(20) UNIQUE,
    phone          VARCHAR(20),
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS branches
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(50),
    address    VARCHAR(100),
    city       VARCHAR(100),
    salon_id   BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_branch_salon
        FOREIGN KEY (salon_id)
            REFERENCES salons (id)
            ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS users
(
    id                    BIGSERIAL PRIMARY KEY,
    first_name            VARCHAR(255),
    last_name             VARCHAR(255),
    email                 VARCHAR(255) UNIQUE,
    password              VARCHAR(255),
    is_active             BOOLEAN,
    commission_percentage NUMERIC(6, 2),
    salon_id              BIGINT,
    created_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_salon
        FOREIGN KEY (salon_id)
            REFERENCES salons (id)
);

CREATE TABLE IF NOT EXISTS clients
(
    id              BIGSERIAL PRIMARY KEY,
    first_name      VARCHAR(100),
    last_name       VARCHAR(100),
    document_number VARCHAR(20),
    phone           VARCHAR(20),
    email           VARCHAR(100),
    birth_date      DATE,
    gender          VARCHAR(20),

    salon_id        BIGINT NOT NULL,

    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_client_salon
        FOREIGN KEY (salon_id)
            REFERENCES salons (id)
            ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS clinical_records
(
    id             BIGSERIAL PRIMARY KEY,

    client_id      BIGINT NOT NULL,
    user_id        BIGINT NOT NULL, -- doctor / quien atendió
    branch_id      BIGINT,          -- opcional (sucursal)

    diagnosis      TEXT,
    treatment      TEXT,
    observations   TEXT,

    -- weight         NUMERIC(5, 2),
    -- height         NUMERIC(5, 2),
    -- blood_pressure VARCHAR(20),

    session_date   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_record_client
        FOREIGN KEY (client_id)
            REFERENCES clients (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_record_user
        FOREIGN KEY (user_id)
            REFERENCES users (id),

    CONSTRAINT fk_record_branch
        FOREIGN KEY (branch_id)
            REFERENCES branches (id)
);