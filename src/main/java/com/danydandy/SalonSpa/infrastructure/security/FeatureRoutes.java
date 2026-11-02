package com.danydandy.SalonSpa.infrastructure.security;

import com.danydandy.SalonSpa.domain.model.Feature;
import org.springframework.http.HttpMethod;

import java.util.regex.Pattern;

/**
 * Which API routes belong to a plan feature. One place to look when a module becomes premium: add its route here and
 * its key to plan_features. Routes not listed are core and available in every plan.
 */
public final class FeatureRoutes {

    private static final Pattern CLIENT_CLINICAL_RECORDS = Pattern.compile("^/api/clients/\\d+/clinical-records(/.*)?$");
    private static final Pattern USER_COMMISSIONS = Pattern.compile("^/api/users/\\d+/service-commissions(/.*)?$");
    private static final Pattern APPOINTMENT_REMINDER = Pattern.compile("^/api/appointments/\\d+/reminder-sent$");

    private FeatureRoutes() {
    }

    /** The feature a request needs, or null when the route is core. */
    public static String requiredFeature(HttpMethod method, String path) {
        if (path.startsWith("/api/clinical-records") || CLIENT_CLINICAL_RECORDS.matcher(path).matches()) {
            return Feature.CLINICAL_RECORDS;
        }
        if (path.startsWith("/api/products") || path.startsWith("/api/product-categories")) {
            return Feature.PRODUCTS;
        }
        if (path.startsWith("/api/commissions") || USER_COMMISSIONS.matcher(path).matches()) {
            return Feature.COMMISSIONS;
        }
        if (path.equals("/api/sales/report")) {
            return Feature.REPORTS;
        }
        if (HttpMethod.POST.equals(method) && APPOINTMENT_REMINDER.matcher(path).matches()) {
            return Feature.WHATSAPP_REMINDERS;
        }
        return null;
    }
}
