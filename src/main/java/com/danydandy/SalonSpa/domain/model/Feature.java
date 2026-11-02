package com.danydandy.SalonSpa.domain.model;

import java.util.List;

/** Keys of the features a plan can include (stored in plan_features). Core modules are not gated. */
public final class Feature {

    public static final String CLINICAL_RECORDS = "clinical_records";
    public static final String PRODUCTS = "products";
    public static final String COMMISSIONS = "commissions";
    public static final String REPORTS = "reports";
    public static final String WHATSAPP_REMINDERS = "whatsapp_reminders";

    public static final List<String> ALL = List.of(
            CLINICAL_RECORDS, PRODUCTS, COMMISSIONS, REPORTS, WHATSAPP_REMINDERS
    );

    private Feature() {
    }
}
