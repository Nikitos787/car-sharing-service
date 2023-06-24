package project.service.mapper.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import project.dto.request.PaymentRequestDto;
import project.dto.response.PaymentResponseDto;
import project.model.payment.Payment;
import project.model.payment.PaymentStatus;
import project.service.RentalService;
import project.service.mapper.RequestDtoMapper;
import project.service.mapper.ResponseDtoMapper;

@AllArgsConstructor
@Component
public class PaymentMapper implements ResponseDtoMapper<PaymentResponseDto, Payment>,
        RequestDtoMapper<PaymentRequestDto, Payment> {
    private RentalService rentalService;

    @Override
    public PaymentResponseDto mapToDto(Payment payment) {
        PaymentResponseDto dto = new PaymentResponseDto();
        dto.setId(payment.getId());
        dto.setStatus(payment.getStatus());
        dto.setType(payment.getType());
        dto.setRentalId(payment.getRental().getId());
        dto.setSessionUrl(payment.getUrl());
        dto.setSessionId(payment.getSessionId());
        dto.setAmount(payment.getPaymentAmount());
        return dto;
    }

    @Override
    public Payment mapToModel(PaymentRequestDto dto) {
        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.PENDING);
        payment.setType(dto.getType());
        payment.setRental(rentalService.findById(dto.getRentalId()));
        return payment;
    }
}
