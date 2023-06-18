package project.controler;

import com.stripe.model.checkout.Session;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Operation(summary = "endpoint for create paymenet",
            description = "endpoint for creating payment")
    public PaymentResponseDto createPaymentSession(
            @Parameter(schema = @Schema(type = "String",
                    defaultValue = "{\n"
                            + "\"rentalId\":1, \n"
                            + "\"type\":\"PAYMENT\" \n"
                            + "}"))
            @RequestBody PaymentRequestDto dto) {
        Payment payment = requestDtoMapper.mapToModel(dto);

        BigDecimal moneyToPay = paymentCalculationService.calculatePaymentAmount(payment);
        BigDecimal moneyToFine = paymentCalculationService.calculateFineAmount(payment.getRental());

        payment.setPaymentAmount(moneyToPay);
        paymentService.save(payment);

        Session session = paymentProvider.createPaymentSession(payment.getPaymentAmount(),
                moneyToFine, payment);
        payment.setSessionId(session.getId());
        payment.setUrl(session.getUrl());

        payment = paymentService.save(payment);
        notificationService
                .sendMessageToAdministrators(String
                        .format("Payment for rental with id: %s start", dto.getRentalId()));

        notificationService
                .sendMessageAboutPaymentToUser(payment, String
                        .format("You start payment for rental with id: %s", dto.getRentalId()));

        return responseDtoMapper.mapToDto(payment);
    }

    @GetMapping
    @Operation(summary = "endpoint for get paymenets by user id",
            description = "endpoint for getting payment by user id for manager")
    public List<PaymentResponseDto> getByUserId(
            @Parameter(schema = @Schema(type = "Integer", description = "user id"))
            @RequestParam Long userId) {
        return paymentService.getByUserId(userId).stream()
                .map(responseDtoMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/my-payments")
    @Operation(summary = "endpoint for getting own payments",
            description = "endpoint for getting own payments")
    public List<PaymentResponseDto> findAllMyPayments(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return paymentService.findAll().stream()
                .filter(p -> p.getRental().getUser().getEmail().equals(userDetails.getUsername()))
                .map(responseDtoMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/success/{id}")
    @Operation(description = "endpoint that you get after success payment")
    public PaymentResponseDto getSucceed(
            @Parameter(schema = @Schema(type = "Integer", description = "payment id"))
            @PathVariable Long id) {
        Payment payment = paymentService.getById(id);
        payment.setStatus(PaymentStatus.PAID);
        return responseDtoMapper.mapToDto(paymentService.save(payment));
    }

    @GetMapping("/cancel/{id}")
    @Operation(description = "endpoint that you get after decline your payment")
    public PaymentResponseDto getCanceled(
            @Parameter(schema = @Schema(type = "Integer", description = "payment id"))
            @PathVariable Long id) {
        return responseDtoMapper.mapToDto(paymentService.save(paymentService.getById(id)));
    }
}
