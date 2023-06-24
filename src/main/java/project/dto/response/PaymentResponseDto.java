package project.dto.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.model.payment.PaymentStatus;
import project.model.payment.PaymentType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseDto {
    private Long id;
    private PaymentStatus status;
    private PaymentType type;
    private Long rentalId;
    private String sessionUrl;
    private String sessionId;
    private BigDecimal amount;
}
