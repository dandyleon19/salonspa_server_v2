-- Demo product categories + products for ONE salon, by id.
-- EDIT v_salon_id below before running. Safe to run twice: if that salon
-- already has product categories, it raises an exception instead of
-- silently doing nothing or duplicating data.

DO $$
DECLARE
    v_salon_id BIGINT := 1; -- <<< EDIT THIS: the target salon's id
    v_cat_capilar BIGINT;
    v_cat_unas BIGINT;
    v_cat_facial BIGINT;
    v_cat_corporal BIGINT;
BEGIN
    IF NOT EXISTS (SELECT 1 FROM salons WHERE id = v_salon_id) THEN
        RAISE EXCEPTION 'No salon found with id %', v_salon_id;
    END IF;

    IF EXISTS (SELECT 1 FROM product_categories WHERE salon_id = v_salon_id) THEN
        RAISE EXCEPTION 'Salon % already has product categories — aborting to avoid duplicates', v_salon_id;
    END IF;

    INSERT INTO product_categories (name, description, long_description, salon_id)
    VALUES ('Cuidado Capilar', 'Shampoos, acondicionadores y tratamientos',
            'Productos profesionales para el cuidado y nutrición del cabello.', v_salon_id)
    RETURNING id INTO v_cat_capilar;

    INSERT INTO product_categories (name, description, long_description, salon_id)
    VALUES ('Cuidado de Uñas', 'Esmaltes y tratamientos para uñas',
            'Productos para el cuidado y decoración de uñas en casa.', v_salon_id)
    RETURNING id INTO v_cat_unas;

    INSERT INTO product_categories (name, description, long_description, salon_id)
    VALUES ('Cuidado Facial', 'Cremas, serums y limpiadores',
            'Línea de productos faciales para una rutina de cuidado completa.', v_salon_id)
    RETURNING id INTO v_cat_facial;

    INSERT INTO product_categories (name, description, long_description, salon_id)
    VALUES ('Cuidado Corporal', 'Lociones, exfoliantes y aceites',
            'Productos para hidratar y cuidar la piel del cuerpo.', v_salon_id)
    RETURNING id INTO v_cat_corporal;

    INSERT INTO products (category_id, name, description, price, stock_quantity, is_active, salon_id) VALUES
        (v_cat_capilar, 'Shampoo Hidratante 250ml', 'Limpieza suave con hidratación profunda', 35.00, 20, true, v_salon_id),
        (v_cat_capilar, 'Acondicionador Reparador 250ml', 'Repara puntas abiertas y aporta brillo', 38.00, 20, true, v_salon_id),
        (v_cat_capilar, 'Mascarilla Capilar Nutritiva', 'Tratamiento intensivo semanal', 45.00, 15, true, v_salon_id),
        (v_cat_capilar, 'Serum Anti-Frizz', 'Controla el frizz y sella la cutícula', 40.00, 12, true, v_salon_id);

    INSERT INTO products (category_id, name, description, price, stock_quantity, is_active, salon_id) VALUES
        (v_cat_unas, 'Esmalte Semipermanente', 'Larga duración, amplia gama de colores', 25.00, 30, true, v_salon_id),
        (v_cat_unas, 'Top Coat Brillante', 'Sellador de alto brillo y protección', 20.00, 25, true, v_salon_id),
        (v_cat_unas, 'Aceite para Cutículas', 'Hidrata y suaviza la cutícula', 15.00, 20, true, v_salon_id),
        (v_cat_unas, 'Removedor de Esmalte', 'Fórmula sin acetona', 12.00, 25, true, v_salon_id);

    INSERT INTO products (category_id, name, description, price, stock_quantity, is_active, salon_id) VALUES
        (v_cat_facial, 'Crema Hidratante Facial', 'Hidratación diaria para todo tipo de piel', 55.00, 15, true, v_salon_id),
        (v_cat_facial, 'Limpiador Facial Suave', 'Limpieza diaria sin resecar la piel', 40.00, 18, true, v_salon_id),
        (v_cat_facial, 'Serum Facial Vitamina C', 'Ilumina y uniforma el tono de piel', 65.00, 10, true, v_salon_id),
        (v_cat_facial, 'Protector Solar Facial SPF50', 'Protección diaria de amplio espectro', 48.00, 20, true, v_salon_id);

    INSERT INTO products (category_id, name, description, price, stock_quantity, is_active, salon_id) VALUES
        (v_cat_corporal, 'Loción Corporal Hidratante', 'Hidratación de cuerpo completo', 32.00, 20, true, v_salon_id),
        (v_cat_corporal, 'Exfoliante Corporal', 'Elimina células muertas y suaviza la piel', 30.00, 15, true, v_salon_id),
        (v_cat_corporal, 'Aceite Corporal Relajante', 'Ideal para masajes, aroma relajante', 38.00, 12, true, v_salon_id);

    RAISE NOTICE 'Seeded product categories + products for salon %', v_salon_id;
END $$;
