-- When each user last signed in or renewed their session. The platform admin sees, per salon, the most recent one.
ALTER TABLE users
    ADD COLUMN last_login_at TIMESTAMP;

-- History: the most recent session token issued to each user is the last time they signed in or renewed.
UPDATE users u
SET last_login_at = (SELECT MAX(r.created_at) FROM refresh_tokens r WHERE r.user_id = u.id);

CREATE INDEX idx_users_salon_last_login ON users (salon_id, last_login_at);
