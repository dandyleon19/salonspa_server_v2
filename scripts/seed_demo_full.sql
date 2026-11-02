-- Demo data for ONE salon, by id: branches, services, products, staff, clients, appointments (past, today and
-- upcoming), sales with payments, stock movements, clinical records and commissions. Meant to fill the tables, the
-- dashboard and the reports with something to look at.
--
-- 1. EDIT v_salon_id below (the target salon's id, the only value to change). Covers 4 months back and 4 months ahead.
-- 2. Run the WHOLE script in one go (it creates a temporary helper function that disappears when the session ends).
--
-- What it does NOT touch: plans, subscriptions, the salon itself and its existing users.
-- What it reuses when the salon already has them: branches (needs 2, adds up to that), service categories + services,
--   product categories + products. If the salon has none it creates them.
-- What it always creates (marked so it can be undone): 5 staff users (email demo.<salon_id>.staffN@demo.test, same
--   password as the salon's first ADMIN_USER), 30 clients (email demo.cliente<N>.<salon_id>@demo.test) and everything
--   that hangs from them. Running it twice is blocked. To remove it, use seed_demo_full_cleanup.sql.
--
-- Times use the Lima clock (v_now). Change it if your server runs on another time zone and "today" looks off.

CREATE OR REPLACE FUNCTION pg_temp.seed_sale(
    p_salon BIGINT,
    p_branch BIGINT,
    p_client BIGINT,
    p_seller BIGINT,
    p_appt BIGINT,
    p_service BIGINT,
    p_product BIGINT,
    p_qty INT,
    p_when TIMESTAMP,
    p_kind TEXT -- NORMAL | PARTIAL | CANCELLED
) RETURNS NUMERIC
    LANGUAGE plpgsql AS
$fn$
DECLARE
    v_sale          BIGINT;
    v_svc_name      TEXT;
    v_svc_price     NUMERIC := 0;
    v_prod_name     TEXT;
    v_prod_price    NUMERIC := 0;
    v_prod_stock    INT;
    v_new_stock     INT;
    v_discount      NUMERIC := 0;
    v_subtotal      NUMERIC;
    v_total         NUMERIC;
    v_paid          NUMERIC;
    v_first         NUMERIC;
    v_product       BIGINT  := p_product;
    v_methods       TEXT[]  := ARRAY ['CASH','CARD','YAPE','PLIN','TRANSFER','CASH','YAPE'];
    v_method        TEXT;
BEGIN
    IF p_service IS NOT NULL THEN
        SELECT name, COALESCE(price, 50) INTO v_svc_name, v_svc_price FROM services WHERE id = p_service;
        IF random() < 0.12 THEN
            v_discount := round(v_svc_price * 0.10, 2);
        END IF;
    END IF;

    IF v_product IS NOT NULL THEN
        SELECT name, COALESCE(price, 20), stock_quantity INTO v_prod_name, v_prod_price, v_prod_stock
        FROM products WHERE id = v_product;
        IF p_kind = 'CANCELLED' THEN
            -- A cancelled sale never moves stock.
            v_product := NULL;
        ELSIF v_prod_stock < p_qty THEN
            -- Out of stock: the shop restocks (four months of sales would otherwise empty every product).
            UPDATE products SET stock_quantity = stock_quantity + 50 WHERE id = v_product
            RETURNING stock_quantity INTO v_prod_stock;
            INSERT INTO product_stock_movements (product_id, movement_type, quantity_delta, resulting_stock, reason,
                                                 created_by_user_id, created_at)
            VALUES (v_product, 'RESTOCK', 50, v_prod_stock, 'Reposición de proveedor', p_seller, p_when - interval '1 hour');
        END IF;
    END IF;

    IF p_service IS NULL AND v_product IS NULL THEN
        RETURN 0;
    END IF;

    v_subtotal := v_svc_price + CASE WHEN v_product IS NULL THEN 0 ELSE v_prod_price * p_qty END;
    v_total := v_subtotal - v_discount;
    v_paid := CASE WHEN p_kind = 'PARTIAL' THEN round(v_total * 0.5, 2) ELSE v_total END;

    INSERT INTO sales (salon_id, branch_id, client_id, registered_by_user_id, appointment_id, subtotal,
                       discount_amount, total_amount, amount_paid, status, notes, sold_at, cancelled_at,
                       cancellation_reason, created_at, updated_at)
    VALUES (p_salon, p_branch, p_client, p_seller, p_appt, v_subtotal, v_discount, v_total, v_paid,
            CASE p_kind WHEN 'PARTIAL' THEN 'PARTIALLY_PAID' WHEN 'CANCELLED' THEN 'CANCELLED' ELSE 'COMPLETED' END,
            CASE p_kind WHEN 'PARTIAL' THEN 'Cliente paga el resto en su próxima visita' END,
            p_when,
            CASE WHEN p_kind = 'CANCELLED' THEN p_when + interval '25 minutes' END,
            CASE WHEN p_kind = 'CANCELLED' THEN 'Venta registrada por error' END,
            p_when, p_when)
    RETURNING id INTO v_sale;

    IF p_service IS NOT NULL THEN
        INSERT INTO sale_items (sale_id, service_id, user_id, appointment_id, service_name, quantity, unit_price,
                                discount_amount, line_total, created_at)
        VALUES (v_sale, p_service, p_seller, p_appt, v_svc_name, 1, v_svc_price, v_discount,
                v_svc_price - v_discount, p_when);
    END IF;

    IF v_product IS NOT NULL THEN
        INSERT INTO sale_items (sale_id, product_id, product_name, quantity, unit_price, discount_amount, line_total,
                                created_at)
        VALUES (v_sale, v_product, v_prod_name, p_qty, v_prod_price, 0, v_prod_price * p_qty, p_when);

        UPDATE products SET stock_quantity = stock_quantity - p_qty WHERE id = v_product
        RETURNING stock_quantity INTO v_new_stock;

        INSERT INTO product_stock_movements (product_id, movement_type, quantity_delta, resulting_stock, reason,
                                             sale_id, created_by_user_id, created_at)
        VALUES (v_product, 'SALE', -p_qty, v_new_stock, 'Venta #' || v_sale, v_sale, p_seller, p_when);
    END IF;

    IF v_paid > 0 THEN
        v_method := v_methods[1 + floor(random() * array_length(v_methods, 1))::int];
        IF p_kind = 'NORMAL' AND v_paid >= 60 AND random() < 0.15 THEN
            -- Paid in two methods.
            v_first := round(v_paid / 2, 2);
            INSERT INTO sale_payments (sale_id, amount, payment_method, reference, paid_at, created_at)
            VALUES (v_sale, v_first, 'CASH', NULL, p_when, p_when),
                   (v_sale, v_paid - v_first, 'YAPE', 'OP-' || (100000 + floor(random() * 899999)::int), p_when, p_when);
        ELSE
            INSERT INTO sale_payments (sale_id, amount, payment_method, reference, paid_at, created_at)
            VALUES (v_sale, v_paid, v_method,
                    CASE WHEN v_method = 'CASH' THEN NULL ELSE 'OP-' || (100000 + floor(random() * 899999)::int) END,
                    p_when, p_when);
        END IF;
    END IF;

    RETURN v_total;
END
$fn$;

DO
$$
DECLARE
    v_salon_id    BIGINT    := 1;    -- <<< EDIT THIS: the target salon's id
    v_days_back   INT       := 120;  -- 4 months of history
    v_days_ahead  INT       := 120;  -- 4 months of upcoming appointments

    v_now         TIMESTAMP := (now() AT TIME ZONE 'America/Lima');
    v_today       DATE      := (now() AT TIME ZONE 'America/Lima')::date;

    v_password    TEXT;
    v_admin_id    BIGINT;
    v_branch_ids  BIGINT[];
    v_service_ids BIGINT[];
    v_product_ids BIGINT[];
    v_staff_ids   BIGINT[]  := ARRAY []::BIGINT[];
    v_client_ids  BIGINT[]  := ARRAY []::BIGINT[];

    v_cat_1       BIGINT;
    v_cat_2       BIGINT;
    v_cat_3       BIGINT;
    v_id          BIGINT;
    i             INT;

    v_first_names TEXT[] := ARRAY ['María','Lucía','Carmen','Rosa','Ana','Sofía','Valeria','Camila','Daniela',
        'Fiorella','Gabriela','Paola','Andrea','Karen','Milagros','Jimena','Renata','Alejandra','Patricia','Mónica',
        'Carolina','Verónica','Claudia','Natalia','Silvana','Isabel','Diana','Jessica','Lorena','Pilar'];
    v_last_names  TEXT[] := ARRAY ['Quispe','Flores','Rojas','Mendoza','Castillo','Vargas','Torres','Ramírez',
        'Gutiérrez','Salazar','Herrera','Chávez','Paredes','Espinoza','Rivera','Cárdenas','Delgado','Navarro',
        'Aguilar','Medina'];

    v_slots       INT[]  := ARRAY [540, 660, 810, 930, 1050];   -- 09:00, 11:00, 13:30, 15:30, 17:30
    v_diagnoses   TEXT[] := ARRAY ['Cabello seco y quebradizo','Piel mixta con zonas de deshidratación',
        'Uñas debilitadas por uso constante de esmalte','Tensión muscular en zona lumbar','Cuero cabelludo sensible',
        'Manchas leves por exposición solar','Puntas abiertas y falta de brillo'];
    v_treatments  TEXT[] := ARRAY ['Hidratación profunda y sellado de puntas','Limpieza facial con mascarilla calmante',
        'Fortalecimiento con base reparadora','Masaje descontracturante de 60 minutos','Lavado con pH neutro y masaje',
        'Peeling suave y protector solar','Corte de puntas y tratamiento de keratina'];
    v_cancel_why  TEXT[] := ARRAY ['La clienta avisó que no podía asistir','Reprogramada por la clienta',
        'Cruce con otra cita','Enfermedad'];
    v_dep_methods TEXT[] := ARRAY ['YAPE','PLIN','TRANSFER','CASH'];

    -- appointment loop
    v_day         INT;
    v_date        DATE;
    v_staff       BIGINT;
    v_staff_idx   INT;
    v_slot        INT;
    v_start       TIMESTAMP;
    v_end         TIMESTAMP;
    v_duration    INT;
    v_service     BIGINT;
    v_client      BIGINT;
    v_branch      BIGINT;
    v_status      TEXT;
    v_r           DOUBLE PRECISION;
    v_appt        BIGINT;
    v_follow_rec  BIGINT;
    v_follow_client BIGINT;
    v_old_stock   INT;
    v_record      BIGINT;
    v_when        TIMESTAMP;
    v_product     BIGINT;
    v_kind        TEXT;
    v_walkins     INT;
    v_total       NUMERIC;
BEGIN
    PERFORM setseed(0.42);

    IF NOT EXISTS (SELECT 1 FROM salons WHERE id = v_salon_id) THEN
        RAISE EXCEPTION 'No salon found with id %', v_salon_id;
    END IF;

    IF EXISTS (SELECT 1 FROM users WHERE email LIKE 'demo.' || v_salon_id || '.staff%@demo.test') THEN
        RAISE EXCEPTION 'Demo data was already loaded for salon % — run seed_demo_full_cleanup.sql first', v_salon_id;
    END IF;

    SELECT id, password INTO v_admin_id, v_password
    FROM users
    WHERE salon_id = v_salon_id AND role = 'ADMIN_USER' AND password IS NOT NULL
    ORDER BY id
    LIMIT 1;
    IF v_admin_id IS NULL THEN
        RAISE EXCEPTION 'Salon % has no ADMIN_USER to copy the password from — create one first', v_salon_id;
    END IF;

    -- ------------------------------------------------------------------ branches (at least 2)
    SELECT array_agg(id ORDER BY id) INTO v_branch_ids FROM branches WHERE salon_id = v_salon_id;
    IF COALESCE(array_length(v_branch_ids, 1), 0) < 2 THEN
        INSERT INTO branches (name, address, city, salon_id)
        SELECT b.name, b.address, 'Lima', v_salon_id
        FROM (VALUES ('Sede Miraflores', 'Av. Larco 1234'), ('Sede San Isidro', 'Av. Conquistadores 456')) AS b (name, address)
        WHERE NOT EXISTS (SELECT 1 FROM branches x WHERE x.salon_id = v_salon_id AND x.name = b.name);
        SELECT array_agg(id ORDER BY id) INTO v_branch_ids FROM branches WHERE salon_id = v_salon_id;
    END IF;

    -- ------------------------------------------------------------------ services
    SELECT array_agg(id ORDER BY id) INTO v_service_ids FROM services WHERE salon_id = v_salon_id AND is_active;
    IF v_service_ids IS NULL THEN
        INSERT INTO service_categories (name, description, salon_id) VALUES ('Cabello', 'Cortes, color y tratamientos', v_salon_id)
        RETURNING id INTO v_cat_1;
        INSERT INTO service_categories (name, description, salon_id) VALUES ('Uñas', 'Manicure y pedicure', v_salon_id)
        RETURNING id INTO v_cat_2;
        INSERT INTO service_categories (name, description, salon_id) VALUES ('Facial y corporal', 'Cuidado de la piel y masajes', v_salon_id)
        RETURNING id INTO v_cat_3;

        INSERT INTO services (category_id, name, description, price, duration_minutes, is_active, salon_id) VALUES
            (v_cat_1, 'Corte de cabello', 'Corte y peinado', 45.00, 45, TRUE, v_salon_id),
            (v_cat_1, 'Tinte completo', 'Aplicación de color', 160.00, 100, TRUE, v_salon_id),
            (v_cat_1, 'Tratamiento de keratina', 'Alisado y brillo', 220.00, 100, TRUE, v_salon_id),
            (v_cat_2, 'Manicure clásico', 'Limado, cutículas y esmalte', 30.00, 40, TRUE, v_salon_id),
            (v_cat_2, 'Manicure semipermanente', 'Esmaltado de larga duración', 55.00, 60, TRUE, v_salon_id),
            (v_cat_2, 'Pedicure spa', 'Pedicure con exfoliación', 65.00, 60, TRUE, v_salon_id),
            (v_cat_3, 'Limpieza facial profunda', 'Limpieza, extracción y mascarilla', 110.00, 70, TRUE, v_salon_id),
            (v_cat_3, 'Masaje relajante', 'Masaje de cuerpo completo', 130.00, 60, TRUE, v_salon_id),
            (v_cat_3, 'Depilación de cejas', 'Perfilado con cera', 25.00, 20, TRUE, v_salon_id);
        SELECT array_agg(id ORDER BY id) INTO v_service_ids FROM services WHERE salon_id = v_salon_id AND is_active;
    END IF;

    -- ------------------------------------------------------------------ products
    SELECT array_agg(id ORDER BY id) INTO v_product_ids FROM products WHERE salon_id = v_salon_id AND is_active;
    IF v_product_ids IS NULL THEN
        INSERT INTO product_categories (name, description, salon_id) VALUES ('Cuidado capilar', 'Shampoos y tratamientos', v_salon_id)
        RETURNING id INTO v_cat_1;
        INSERT INTO product_categories (name, description, salon_id) VALUES ('Cuidado facial y corporal', 'Cremas y aceites', v_salon_id)
        RETURNING id INTO v_cat_2;

        INSERT INTO products (category_id, name, description, price, stock_quantity, low_stock_threshold, is_active, salon_id) VALUES
            (v_cat_1, 'Shampoo hidratante 250ml', 'Limpieza suave', 35.00, 60, 8, TRUE, v_salon_id),
            (v_cat_1, 'Acondicionador reparador 250ml', 'Repara puntas', 38.00, 60, 8, TRUE, v_salon_id),
            (v_cat_1, 'Mascarilla nutritiva', 'Tratamiento semanal', 45.00, 50, 6, TRUE, v_salon_id),
            (v_cat_1, 'Serum anti-frizz', 'Controla el frizz', 40.00, 50, 6, TRUE, v_salon_id),
            (v_cat_2, 'Crema facial hidratante', 'Uso diario', 72.00, 40, 5, TRUE, v_salon_id),
            (v_cat_2, 'Aceite corporal de almendras', 'Hidrata la piel', 55.00, 40, 5, TRUE, v_salon_id),
            (v_cat_2, 'Esmalte semipermanente', 'Gama de colores', 25.00, 80, 10, TRUE, v_salon_id),
            (v_cat_2, 'Protector solar facial', 'SPF 50', 62.00, 40, 5, TRUE, v_salon_id);
        SELECT array_agg(id ORDER BY id) INTO v_product_ids FROM products WHERE salon_id = v_salon_id AND is_active;

        -- Opening stock, dated before the history starts.
        INSERT INTO product_stock_movements (product_id, movement_type, quantity_delta, resulting_stock, reason,
                                             created_by_user_id, created_at)
        SELECT id, 'RESTOCK', stock_quantity, stock_quantity, 'Stock inicial', v_admin_id,
               v_now - make_interval(days => v_days_back + 2)
        FROM products
        WHERE id = ANY (v_product_ids);
    END IF;

    -- ------------------------------------------------------------------ staff (5; the 5th is inactive)
    FOR i IN 1..5
        LOOP
            INSERT INTO users (first_name, last_name, email, password, is_active, commission_percentage, role, salon_id)
            VALUES ((ARRAY ['Lucero','Gianella','Renzo','Brenda','Jimmy'])[i],
                    (ARRAY ['Paredes','Ríos','Castañeda','Aguirre','Salas'])[i],
                    'demo.' || v_salon_id || '.staff' || i || '@demo.test',
                    v_password,
                    i < 5,
                    (ARRAY [30, 35, 40, 25, 30])[i],
                    'STAFF_USER',
                    v_salon_id)
            RETURNING id INTO v_id;
            v_staff_ids := v_staff_ids || v_id;
        END LOOP;

    -- Custom commission per service for two of them (the rest use their general percentage).
    INSERT INTO user_service_commissions (user_id, service_id, commission_percentage)
    SELECT v_staff_ids[s.staff_idx], v_service_ids[s.svc_idx], s.pct
    FROM (VALUES (1, 1, 50.00), (1, 2, 45.00), (1, 3, 40.00), (2, 1, 20.00), (2, 2, 25.00)) AS s (staff_idx, svc_idx, pct)
    WHERE s.svc_idx <= array_length(v_service_ids, 1);

    -- ------------------------------------------------------------------ clients (30)
    FOR i IN 1..30
        LOOP
            INSERT INTO clients (first_name, last_name, document_number, phone, email, birth_date, gender, salon_id,
                                 created_at, updated_at)
            VALUES (v_first_names[i],
                    v_last_names[1 + (i * 7) % 20] || ' ' || v_last_names[1 + (i * 3) % 20],
                    lpad((40000000 + i * 137531)::text, 8, '0'),
                    CASE WHEN i % 9 = 0 THEN NULL ELSE '9' || lpad((10000000 + (i * 2345671) % 89999999)::text, 8, '0') END,
                    'demo.cliente' || i || '.' || v_salon_id || '@demo.test',
                    date '1965-01-01' + ((i * 811) % 12500),
                    CASE WHEN i % 10 = 0 THEN 'MALE' WHEN i % 17 = 0 THEN 'OTHER' ELSE 'FEMALE' END,
                    v_salon_id,
                    v_now - make_interval(days => (i * 2) % (v_days_back + 10)),
                    v_now)
            RETURNING id INTO v_id;
            v_client_ids := v_client_ids || v_id;
        END LOOP;

    -- ------------------------------------------------------------------ appointments, sales, clinical records
    FOR v_day IN -v_days_back..v_days_ahead
        LOOP
            v_date := v_today + v_day;
            CONTINUE WHEN extract(dow FROM v_date) = 0; -- closed on Sundays

            FOR v_staff_idx IN 1..4
                LOOP
                    v_staff := v_staff_ids[v_staff_idx];
                    v_branch := v_branch_ids[1 + (v_staff_idx - 1) % array_length(v_branch_ids, 1)];

                    FOREACH v_slot IN ARRAY v_slots
                        LOOP
                            CONTINUE WHEN random() > CASE WHEN v_day = 0 THEN 0.75 WHEN v_day > 0 THEN 0.45 ELSE 0.55 END;

                            v_service := v_service_ids[1 + floor(random() * array_length(v_service_ids, 1))::int % array_length(v_service_ids, 1)];
                            v_client := v_client_ids[1 + floor(random() * array_length(v_client_ids, 1))::int % array_length(v_client_ids, 1)];
                            v_start := v_date + v_slot * interval '1 minute';
                            SELECT LEAST(duration_minutes, 110) INTO v_duration FROM services WHERE id = v_service;
                            v_end := v_start + v_duration * interval '1 minute';

                            v_r := random();
                            IF v_end <= v_now THEN
                                v_status := CASE WHEN v_r < 0.78 THEN 'COMPLETED' WHEN v_r < 0.89 THEN 'CANCELLED' ELSE 'NO_SHOW' END;
                            ELSIF v_start <= v_now THEN
                                v_status := 'IN_PROGRESS';
                            ELSE
                                v_status := CASE WHEN v_r < 0.06 THEN 'CANCELLED' WHEN v_r < 0.55 THEN 'CONFIRMED' ELSE 'SCHEDULED' END;
                            END IF;

                            -- Some upcoming appointments are the follow-up of an earlier clinical record.
                            v_follow_rec := NULL;
                            IF v_start > v_now AND v_status IN ('SCHEDULED', 'CONFIRMED') AND random() < 0.2 THEN
                                SELECT cr.id, cr.client_id INTO v_follow_rec, v_follow_client
                                FROM clinical_records cr
                                WHERE cr.user_id = ANY (v_staff_ids)
                                  AND cr.follow_up_appointment_id IS NULL
                                  AND cr.session_date < v_now
                                ORDER BY random()
                                LIMIT 1;
                                IF FOUND THEN
                                    v_client := v_follow_client;
                                ELSE
                                    v_follow_rec := NULL;
                                END IF;
                            END IF;

                            INSERT INTO appointments (client_id, user_id, branch_id, salon_id, service_id, start_at, end_at,
                                                      status, notes, cancelled_at, cancellation_reason, deposit_amount,
                                                      deposit_payment_method, deposit_paid_at, reminder_sent_at,
                                                      created_at, updated_at)
                            VALUES (v_client, v_staff, v_branch, v_salon_id, v_service, v_start, v_end, v_status,
                                    CASE WHEN random() < 0.2 THEN
                                             (ARRAY ['Prefiere atención con música suave','Alergia a ciertos esmaltes',
                                                 'Viene con su hija','Pidió confirmar por WhatsApp'])[1 + floor(random() * 3.99)::int] END,
                                    CASE WHEN v_status = 'CANCELLED' THEN LEAST(v_now, v_start - interval '1 day') END,
                                    CASE WHEN v_status = 'CANCELLED' THEN v_cancel_why[1 + floor(random() * 3.99)::int] END,
                                    CASE WHEN v_status IN ('SCHEDULED', 'CONFIRMED') AND random() < 0.3 THEN 20.00 END,
                                    NULL, NULL,
                                    CASE WHEN v_status IN ('SCHEDULED', 'CONFIRMED') AND v_start < v_now + interval '2 days'
                                              AND random() < 0.5 THEN v_now - interval '3 hours' END,
                                    LEAST(v_now, v_start - interval '2 days'),
                                    LEAST(v_now, v_start - interval '1 day'))
                            RETURNING id INTO v_appt;

                            -- The deposit method and date only exist when there is a deposit.
                            UPDATE appointments
                            SET deposit_payment_method = v_dep_methods[1 + floor(random() * 3.99)::int],
                                deposit_paid_at        = v_now - interval '1 day'
                            WHERE id = v_appt AND deposit_amount IS NOT NULL;

                            IF v_follow_rec IS NOT NULL THEN
                                UPDATE clinical_records SET follow_up_appointment_id = v_appt WHERE id = v_follow_rec;
                            END IF;

                            IF v_status = 'COMPLETED' THEN
                                v_product := CASE WHEN random() < 0.2 THEN
                                                      v_product_ids[1 + floor(random() * array_length(v_product_ids, 1))::int % array_length(v_product_ids, 1)] END;
                                PERFORM pg_temp.seed_sale(v_salon_id, v_branch, v_client, v_staff, v_appt, v_service,
                                                          v_product, 1, v_end,
                                                          CASE WHEN random() < 0.08 THEN 'PARTIAL' ELSE 'NORMAL' END);

                                IF random() < 0.35 THEN
                                    i := 1 + floor(random() * 6.99)::int;
                                    INSERT INTO clinical_records (client_id, user_id, branch_id, diagnosis, treatment, observations,
                                                                  session_date, created_at, updated_at)
                                    VALUES (v_client, v_staff, v_branch, v_diagnoses[i], v_treatments[i],
                                            'Se recomienda control en 3 semanas y cuidado en casa.', v_start, v_end, v_end)
                                    RETURNING id INTO v_record;
                                    INSERT INTO clinical_record_services (clinical_record_id, service_id, notes, created_at)
                                    VALUES (v_record, v_service, 'Sin reacciones adversas', v_end);
                                END IF;
                            END IF;
                        END LOOP;
                END LOOP;

            -- Walk-in sales (no appointment): a service, a product, or both.
            IF v_date <= v_today THEN
                v_walkins := floor(random() * 3.99)::int;
                FOR i IN 1..v_walkins
                    LOOP
                        v_when := v_date + (570 + floor(random() * 600)::int) * interval '1 minute';
                        CONTINUE WHEN v_when > v_now;

                        v_client := v_client_ids[1 + floor(random() * array_length(v_client_ids, 1))::int % array_length(v_client_ids, 1)];
                        v_staff := v_staff_ids[1 + floor(random() * 3.99)::int];
                        v_branch := v_branch_ids[1 + floor(random() * array_length(v_branch_ids, 1))::int % array_length(v_branch_ids, 1)];
                        v_product := v_product_ids[1 + floor(random() * array_length(v_product_ids, 1))::int % array_length(v_product_ids, 1)];
                        v_r := random();
                        v_kind := CASE WHEN random() < 0.04 THEN 'CANCELLED' WHEN random() < 0.07 THEN 'PARTIAL' ELSE 'NORMAL' END;
                        v_service := CASE WHEN v_r < 0.5 THEN NULL
                                          ELSE v_service_ids[1 + floor(random() * array_length(v_service_ids, 1))::int % array_length(v_service_ids, 1)] END;
                        -- A product-only sale needs a seller who is not credited a service.
                        PERFORM pg_temp.seed_sale(v_salon_id, v_branch, v_client,
                                                  CASE WHEN v_service IS NULL THEN v_admin_id ELSE v_staff END,
                                                  NULL, v_service,
                                                  CASE WHEN v_service IS NULL OR v_r > 0.8 THEN v_product END,
                                                  1 + floor(random() * 1.99)::int, v_when, v_kind);
                    END LOOP;
            END IF;
        END LOOP;

    -- ------------------------------------------------------------------ low stock to look at
    IF array_length(v_product_ids, 1) >= 3 THEN
        SELECT stock_quantity INTO v_old_stock FROM products WHERE id = v_product_ids[1];
        UPDATE products SET stock_quantity = 3 WHERE id = v_product_ids[1];
        INSERT INTO product_stock_movements (product_id, movement_type, quantity_delta, resulting_stock, reason, created_by_user_id)
        VALUES (v_product_ids[1], 'ADJUSTMENT', 3 - v_old_stock, 3, 'Conteo de inventario', v_admin_id);

        SELECT stock_quantity INTO v_old_stock FROM products WHERE id = v_product_ids[3];
        UPDATE products SET stock_quantity = 0 WHERE id = v_product_ids[3];
        INSERT INTO product_stock_movements (product_id, movement_type, quantity_delta, resulting_stock, reason, created_by_user_id)
        VALUES (v_product_ids[3], 'ADJUSTMENT', 0 - v_old_stock, 0, 'Se agotó el stock', v_admin_id);
    END IF;

    RAISE NOTICE 'Demo data loaded for salon %: % branches, % services, % products, % staff, % clients',
        v_salon_id, array_length(v_branch_ids, 1), array_length(v_service_ids, 1), array_length(v_product_ids, 1),
        array_length(v_staff_ids, 1), array_length(v_client_ids, 1);
    RAISE NOTICE '% appointments, % sales, % clinical records',
        (SELECT count(*) FROM appointments WHERE salon_id = v_salon_id AND user_id = ANY (v_staff_ids)),
        (SELECT count(*) FROM sales WHERE salon_id = v_salon_id AND client_id = ANY (v_client_ids)),
        (SELECT count(*) FROM clinical_records WHERE user_id = ANY (v_staff_ids));
    RAISE NOTICE 'Staff sign in with demo.%.staff1@demo.test (… staff4) and the same password as the salon''s admin', v_salon_id;
END
$$;

DROP FUNCTION IF EXISTS pg_temp.seed_sale(BIGINT, BIGINT, BIGINT, BIGINT, BIGINT, BIGINT, BIGINT, INT, TIMESTAMP, TEXT);
