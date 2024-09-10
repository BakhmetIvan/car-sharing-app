package com.carsharingapp.mapper;

import com.carsharingapp.config.MapperConfig;
import com.carsharingapp.dto.payment.PaymentFullResponseDto;
import com.carsharingapp.dto.payment.PaymentResponseDto;
import com.carsharingapp.dto.payment.PaymentStatusResponseDto;
import com.carsharingapp.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    PaymentResponseDto toDto(Payment payment);

    PaymentStatusResponseDto toStatusDto(Payment payment);

    @Mapping(target = "rentalId", source = "rental.id")
    PaymentFullResponseDto toFullDto(Payment payment);
}
