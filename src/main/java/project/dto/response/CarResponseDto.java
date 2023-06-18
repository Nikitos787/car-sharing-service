package project.dto.response;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.math.BigDecimal;
import lombok.Data;
import project.model.car.CarType;

@Data
public class CarResponseDto {
    private Long id;
    private String model;
    private String brand;
    private BigDecimal dailyFee;
    @Enumerated(EnumType.STRING)
    private CarType carType;
    private int inventory;
    private boolean isDeleted;
}
