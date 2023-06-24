package project.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.model.payment.PaymentType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestDto {
    private Long rentalId;
    private PaymentType type;
}
