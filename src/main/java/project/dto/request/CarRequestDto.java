package project.dto.request;

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
    private CarType carType;
    private int inventory;
}
