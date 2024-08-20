package mate.capsharingapp.mapper;

import mate.capsharingapp.config.MapperConfig;
import mate.capsharingapp.dto.payment.PaymentFullResponseDto;
import mate.capsharingapp.dto.payment.PaymentResponseDto;
import mate.capsharingapp.dto.payment.PaymentStatusResponseDto;
import mate.capsharingapp.model.Payment;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    PaymentResponseDto toDto(Payment payment);

    PaymentStatusResponseDto toStatusDto(Payment payment);

    PaymentFullResponseDto toFullDto(Payment payment);
}
