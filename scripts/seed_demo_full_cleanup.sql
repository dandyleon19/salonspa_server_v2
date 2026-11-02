-- Removes what seed_demo_full.sql created for ONE salon: the demo staff, the demo clients and everything that hangs
-- from them (appointments, sales, payments, stock movements of those sales, clinical records, commissions).
-- It does NOT remove the branches, services, products or categories the seed may have created, nor revert the stock.
-- EDIT v_salon_id below.

DO
$$
DECLARE
    v_salon_id BIGINT := 1; -- <<< EDIT THIS: the target salon's id
    v_clients  BIGINT[];
    v_staff    BIGINT[];
BEGIN
    IF NOT EXISTS (SELECT 1 FROM salons WHERE id = v_salon_id) THEN
        RAISE EXCEPTION 'No salon found with id %', v_salon_id;
    END IF;

    SELECT array_agg(id) INTO v_clients FROM clients
    WHERE salon_id = v_salon_id AND email LIKE 'demo.cliente%.' || v_salon_id || '@demo.test';
    SELECT array_agg(id) INTO v_staff FROM users WHERE email LIKE 'demo.' || v_salon_id || '.staff%@demo.test';

    DELETE FROM product_stock_movements
    WHERE sale_id IN (SELECT id FROM sales WHERE client_id = ANY (v_clients))
       OR created_by_user_id = ANY (v_staff);
    DELETE FROM sales WHERE client_id = ANY (v_clients);                       -- items and payments cascade
    DELETE FROM clinical_records WHERE client_id = ANY (v_clients) OR user_id = ANY (v_staff);
    DELETE FROM appointments WHERE client_id = ANY (v_clients) OR user_id = ANY (v_staff);
    DELETE FROM clients WHERE id = ANY (v_clients);
    DELETE FROM users WHERE id = ANY (v_staff);                                -- commissions cascade

    RAISE NOTICE 'Demo data removed for salon %', v_salon_id;
END
$$;
