package project.dto.request;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.model.car.CarType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarRequestDto {
    private String model;
    private String brand;
    private BigDecimal dailyFee;
    @Enumerated(EnumType.STRING)
    private CarType carType;
    private int inventory;
}
