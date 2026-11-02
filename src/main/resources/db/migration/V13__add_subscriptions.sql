-- Subscriptions: every salon must have a trial or a paid plan to use the system.
-- Plans are paid tiers (BASICO < PROFESIONAL < PREMIUM). They differ only in their limits (branches and users);
-- every plan includes every feature. What each tier includes is data, so it can be changed with plain SQL
-- (or later a screen) without a deploy.
--
-- max_branches / max_users: NULL = unlimited.
-- Gateable features (feature_key): clinical_records, products, commissions, reports, whatsapp_reminders.
-- Today all of them are in every plan; to make one exclusive to a tier, delete its plan_features rows for the lower tiers.

CREATE TABLE plans
(
    id            BIGSERIAL PRIMARY KEY,
    code          VARCHAR(30)  NOT NULL UNIQUE,
    name          VARCHAR(60)  NOT NULL,
    price_monthly NUMERIC(10, 2),
    max_branches  INTEGER,
    max_users     INTEGER,
    sort_order    INTEGER      NOT NULL DEFAULT 0,
    is_active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_plan_max_branches CHECK (max_branches IS NULL OR max_branches > 0),
    CONSTRAINT chk_plan_max_users CHECK (max_users IS NULL OR max_users > 0)
);

CREATE TABLE plan_features
(
    plan_id     BIGINT      NOT NULL,
    feature_key VARCHAR(50) NOT NULL,
    PRIMARY KEY (plan_id, feature_key),
    CONSTRAINT fk_plan_feature_plan
        FOREIGN KEY (plan_id) REFERENCES plans (id) ON DELETE CASCADE
);

-- One row per salon. status is the STORED state (TRIAL or ACTIVE); the effective state (PAST_DUE, EXPIRED) is
-- derived from the dates when it is read, so no scheduled job is needed.
-- suspended is a flag of its own (blocked by hand): it does not overwrite status or the dates, so reactivating a salon
-- returns it to exactly the state it had.
-- current_period_end NULL on an ACTIVE row means "no expiry" (a salon that never needs a subscription).
-- A TRIAL row with trial_ends_at NULL has not started yet: the clock starts the first time a user of the salon opens
-- the app (trial_started_at), and the trial then lasts trial_days days. trial_days can be changed per salon.
CREATE TABLE salon_subscriptions
(
    salon_id           BIGINT PRIMARY KEY,
    plan_id            BIGINT      NOT NULL,
    status             VARCHAR(20) NOT NULL,
    trial_days         INTEGER     NOT NULL DEFAULT 14,
    trial_started_at   TIMESTAMP,
    trial_ends_at      TIMESTAMP,
    current_period_end TIMESTAMP,
    suspended          BOOLEAN     NOT NULL DEFAULT FALSE,
    notes              VARCHAR(500),
    created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_subscription_salon
        FOREIGN KEY (salon_id) REFERENCES salons (id) ON DELETE CASCADE,
    CONSTRAINT fk_subscription_plan
        FOREIGN KEY (plan_id) REFERENCES plans (id),
    CONSTRAINT chk_subscription_status CHECK (status IN ('TRIAL', 'ACTIVE')),
    CONSTRAINT chk_subscription_trial_days CHECK (trial_days BETWEEN 1 AND 365)
);

CREATE INDEX idx_salon_subscriptions_plan ON salon_subscriptions (plan_id);

-- Audit log: who gave days, changed a plan or suspended a salon.
CREATE TABLE subscription_events
(
    id         BIGSERIAL PRIMARY KEY,
    salon_id   BIGINT      NOT NULL,
    event_type VARCHAR(30) NOT NULL,
    detail     VARCHAR(500),
    user_id    BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_subscription_event_salon
        FOREIGN KEY (salon_id) REFERENCES salons (id) ON DELETE CASCADE
);

CREATE INDEX idx_subscription_events_salon ON subscription_events (salon_id, created_at DESC);

-- Initial plans. Prices are left empty on purpose (charging is manual for now).
INSERT INTO plans (code, name, max_branches, max_users, sort_order)
VALUES ('BASICO', 'Básico', 1, 3, 1),
       ('PROFESIONAL', 'Profesional', 3, 10, 2),
       ('PREMIUM', 'Premium', NULL, NULL, 3);

-- Every plan includes every feature: the tiers differ only in max_branches / max_users.
INSERT INTO plan_features (plan_id, feature_key)
SELECT p.id, f.feature_key
FROM plans p
         CROSS JOIN (VALUES ('products'),
                            ('commissions'),
                            ('whatsapp_reminders'),
                            ('clinical_records'),
                            ('reports')) AS f(feature_key);

-- Salons that already exist follow the same rule as new ones: a 14-day trial (trying the PREMIUM plan) that starts
-- the first time one of their users opens the app after this update. A salon that must never be charged is switched
-- to "active without expiry" from the SUPER_ADMIN screen.
INSERT INTO salon_subscriptions (salon_id, plan_id, status, trial_days)
SELECT s.id, (SELECT id FROM plans WHERE code = 'PREMIUM'), 'TRIAL', 14
FROM salons s;
