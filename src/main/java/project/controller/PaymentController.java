package project.controller;

import com.stripe.model.checkout.Session;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.dto.request.PaymentRequestDto;
import project.dto.response.PaymentResponseDto;
import project.model.payment.Payment;
import project.model.payment.PaymentStatus;
import project.service.NotificationService;
import project.service.PaymentCalculationService;
import project.service.PaymentService;
import project.service.mapper.RequestDtoMapper;
import project.service.mapper.ResponseDtoMapper;
import project.stripe.PaymentProvider;

@AllArgsConstructor
@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentProvider paymentProvider;
    private final PaymentService paymentService;
    private final PaymentCalculationService paymentCalculationService;
    private final RequestDtoMapper<PaymentRequestDto, Payment> requestDtoMapper;
    private final ResponseDtoMapper<PaymentResponseDto, Payment> responseDtoMapper;
    private final NotificationService notificationService;

    @PostMapping
    @Operation(summary = "Endpoint for creating a payment",
            description = "Endpoint for creating a payment")
    public PaymentResponseDto createPaymentSession(
            @Parameter(schema = @Schema(implementation = PaymentRequestDto.class))
            @Valid @RequestBody PaymentRequestDto paymentRequestDto) {
        Payment payment = requestDtoMapper.mapToModel(paymentRequestDto);

        BigDecimal moneyToPay = paymentCalculationService.calculatePaymentAmount(payment);
        BigDecimal moneyToFine = paymentCalculationService.calculateFineAmount(payment.getRental());

        payment.setPaymentAmount(moneyToPay);

        Session session = paymentProvider.createPaymentSession(payment.getPaymentAmount(),
                moneyToFine, payment);
        payment.setSessionId(session.getId());
        payment.setUrl(session.getUrl());

        payment = paymentService.save(payment);
        notificationService.sendMessageToAdministrators(String
                .format("Payment for rental with id: %s started", paymentRequestDto.getRentalId()));
        notificationService.sendMessageAboutPaymentToUser(payment, String
                .format("You started payment for rental with id: %s", paymentRequestDto
                        .getRentalId()));
        return responseDtoMapper.mapToDto(payment);
    }

    @GetMapping
    @Operation(summary = "Endpoint for getting payments by user id",
            description = "Endpoint for getting payments by user id")
    public List<PaymentResponseDto> getByUserId(
            @Parameter(description = "User id", required = true)
            @RequestParam Long userId) {
        return paymentService.getByUserId(userId).stream()
                .map(responseDtoMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/my-payments")
    @Operation(summary = "Endpoint for getting own payments",
            description = "Endpoint for getting own payments")
    public List<PaymentResponseDto> findAllMyPayments(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String userEmail = userDetails.getUsername();
        return paymentService.findAll().stream()
                .filter(p -> p.getRental().getUser().getEmail().equals(userEmail))
                .map(responseDtoMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/success/{id}")
    @Operation(description = "Endpoint that you get after successful payment")
    public PaymentResponseDto getSucceed(
            @Parameter(description = "Payment id", required = true)
            @PathVariable Long id) {
        Payment payment = paymentService.getById(id);
        payment.setStatus(PaymentStatus.PAID);
        notificationService.sendMessageAboutPaymentToUser(payment, String
                .format("You successfully paid for your rent: %s", payment.getRental().toString()));
        return responseDtoMapper.mapToDto(paymentService.save(payment));
    }

    @GetMapping("/cancel/{id}")
    @Operation(description = "Endpoint that you get after declining your payment")
    public PaymentResponseDto getCanceled(
            @Parameter(description = "Payment id", required = true)
            @PathVariable Long id) {
        notificationService.sendMessageAboutPaymentToUser(paymentService.getById(id), String
                .format("You cancel payment process. Please pay for your rent with id: %s", id));
        return responseDtoMapper.mapToDto(paymentService.save(paymentService.getById(id)));
    }
}
