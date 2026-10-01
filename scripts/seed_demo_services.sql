-- Demo service categories + services for ONE salon, by id.
-- EDIT v_salon_id below before running. Safe to run twice: if that salon
-- already has service categories, it raises an exception instead of
-- silently doing nothing or duplicating data.

DO $$
DECLARE
    v_salon_id BIGINT := 1; -- <<< EDIT THIS: the target salon's id
    v_cat_manicure BIGINT;
    v_cat_pedicure BIGINT;
    v_cat_peinados BIGINT;
    v_cat_masajes BIGINT;
    v_cat_rostro BIGINT;
    v_cat_podologia BIGINT;
BEGIN
    IF NOT EXISTS (SELECT 1 FROM salons WHERE id = v_salon_id) THEN
        RAISE EXCEPTION 'No salon found with id %', v_salon_id;
    END IF;

    IF EXISTS (SELECT 1 FROM service_categories WHERE salon_id = v_salon_id) THEN
        RAISE EXCEPTION 'Salon % already has service categories — aborting to avoid duplicates', v_salon_id;
    END IF;

    INSERT INTO service_categories (name, description, long_description, salon_id)
    VALUES ('Manicure', 'Cuidado y diseño de uñas de manos',
            'Tratamientos de manicure con productos de calidad para unas manos impecables.', v_salon_id)
    RETURNING id INTO v_cat_manicure;

    INSERT INTO service_categories (name, description, long_description, salon_id)
    VALUES ('Pedicure', 'Cuidado y diseño de uñas de pies',
            'Tratamientos de pedicure spa para el cuidado completo de tus pies.', v_salon_id)
    RETURNING id INTO v_cat_pedicure;

    INSERT INTO service_categories (name, description, long_description, salon_id)
    VALUES ('Peinados y Estilismo', 'Peinados para toda ocasión',
            'Estilismo profesional para eventos y el día a día.', v_salon_id)
    RETURNING id INTO v_cat_peinados;

    INSERT INTO service_categories (name, description, long_description, salon_id)
    VALUES ('Masajes y Relajación', 'Masajes terapéuticos y de relajación',
            'Técnicas de masaje para aliviar tensión y promover el bienestar.', v_salon_id)
    RETURNING id INTO v_cat_masajes;

    INSERT INTO service_categories (name, description, long_description, salon_id)
    VALUES ('Tratamientos Faciales', 'Cuidado facial profesional',
            'Limpieza, hidratación y tratamientos anti-edad para tu rostro.', v_salon_id)
    RETURNING id INTO v_cat_rostro;

    INSERT INTO service_categories (name, description, long_description, salon_id)
    VALUES ('Podología', 'Cuidado profesional de pies',
            'Tratamientos podológicos con técnicas avanzadas y productos de primera calidad.', v_salon_id)
    RETURNING id INTO v_cat_podologia;

    INSERT INTO services (category_id, name, description, price, duration_minutes, is_active, salon_id) VALUES
        (v_cat_manicure, 'Manicure Clásico', 'Limado, cutícula y esmaltado tradicional', 25.00, 30, true, v_salon_id),
        (v_cat_manicure, 'Manicure Gel', 'Esmaltado semipermanente de larga duración', 35.00, 45, true, v_salon_id),
        (v_cat_manicure, 'Manicure Ojo de Gato', 'Efecto 3D con esmalte magnético', 40.00, 45, true, v_salon_id),
        (v_cat_manicure, 'Manicure Rubber Gel', 'Extensión y reforzado de uñas con gel', 45.00, 60, true, v_salon_id);

    INSERT INTO services (category_id, name, description, price, duration_minutes, is_active, salon_id) VALUES
        (v_cat_pedicure, 'Pedicure Clásico', 'Limado, cutícula y esmaltado tradicional', 30.00, 40, true, v_salon_id),
        (v_cat_pedicure, 'Pedicure Spa', 'Exfoliación, masaje e hidratación profunda', 45.00, 60, true, v_salon_id),
        (v_cat_pedicure, 'Pedicure Gel', 'Esmaltado semipermanente de larga duración', 40.00, 50, true, v_salon_id);

    INSERT INTO services (category_id, name, description, price, duration_minutes, is_active, salon_id) VALUES
        (v_cat_peinados, 'Peinado para Evento', 'Peinado profesional para ocasiones especiales', 60.00, 60, true, v_salon_id),
        (v_cat_peinados, 'Cepillado y Brushing', 'Secado y brushing profesional', 35.00, 30, true, v_salon_id),
        (v_cat_peinados, 'Alisado de Keratina', 'Tratamiento alisador con keratina', 150.00, 120, true, v_salon_id);

    INSERT INTO services (category_id, name, description, price, duration_minutes, is_active, salon_id) VALUES
        (v_cat_masajes, 'Masaje Relajante', 'Terapia de relajación de cuerpo completo', 80.00, 60, true, v_salon_id),
        (v_cat_masajes, 'Masaje Descontracturante', 'Enfocado en liberar tensión muscular', 90.00, 60, true, v_salon_id),
        (v_cat_masajes, 'Masaje con Piedras Calientes', 'Terapia con piedras volcánicas', 110.00, 75, true, v_salon_id);

    INSERT INTO services (category_id, name, description, price, duration_minutes, is_active, salon_id) VALUES
        (v_cat_rostro, 'Limpieza Facial Profunda', 'Limpieza con extracción e hidratación', 70.00, 60, true, v_salon_id),
        (v_cat_rostro, 'Hidratación Facial', 'Tratamiento hidratante intensivo', 60.00, 45, true, v_salon_id),
        (v_cat_rostro, 'Tratamiento Anti-edad', 'Tratamiento facial reafirmante', 120.00, 60, true, v_salon_id);

    INSERT INTO services (category_id, name, description, price, duration_minutes, is_active, salon_id) VALUES
        (v_cat_podologia, 'Tratamiento de Uñas Encarnadas', 'Corrección profesional de uñas encarnadas', 50.00, 40, true, v_salon_id),
        (v_cat_podologia, 'Eliminación de Callosidades', 'Remoción de durezas y callosidades', 45.00, 40, true, v_salon_id),
        (v_cat_podologia, 'Cuidado Preventivo del Pie Diabético', 'Cuidado especializado para pie diabético', 60.00, 50, true, v_salon_id),
        (v_cat_podologia, 'Tratamiento de Hongos', 'Tratamiento para hongos y afecciones', 50.00, 40, true, v_salon_id);

    RAISE NOTICE 'Seeded service categories + services for salon %', v_salon_id;
END $$;
