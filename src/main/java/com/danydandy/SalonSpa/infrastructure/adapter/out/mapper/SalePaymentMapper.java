package com.danydandy.SalonSpa.infrastructure.adapter.out.mapper;

import com.danydandy.SalonSpa.application.dto.response.SalePaymentResponse;
import com.danydandy.SalonSpa.domain.model.PaymentMethod;
import com.danydandy.SalonSpa.domain.model.SalePayment;
import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.SalePaymentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface SalePaymentMapper {

    @Mapping(target = "paymentMethod", source = "paymentMethod", qualifiedByName = "stringToMethod")
    SalePayment toDomain(SalePaymentEntity entity);

    @Mapping(target = "paymentMethod", source = "paymentMethod", qualifiedByName = "methodToString")
    @Mapping(target = "createdAt", ignore = true)
    SalePaymentEntity toEntity(SalePayment domain);

    @Mapping(target = "paymentMethod", source = "paymentMethod", qualifiedByName = "methodToResponseString")
    SalePaymentResponse toResponse(SalePayment domain);

    @Named("stringToMethod")
    default PaymentMethod stringToMethod(String paymentMethod) {
        return paymentMethod != null ? PaymentMethod.valueOf(paymentMethod) : null;
    }

    @Named("methodToString")
    default String methodToString(PaymentMethod paymentMethod) {
        return paymentMethod != null ? paymentMethod.name() : null;
    }

    @Named("methodToResponseString")
    default String methodToResponseString(PaymentMethod paymentMethod) {
        return paymentMethod != null ? paymentMethod.name() : null;
    }
}
