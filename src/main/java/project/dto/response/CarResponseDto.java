package project.dto.response;

import java.math.BigDecimal;
import lombok.Data;
import project.model.car.CarType;

@Data
public class CarResponseDto {
    private Long id;
    private String model;
    private String brand;
    private BigDecimal dailyFee;
    private CarType carType;
    private int inventory;
    private boolean isDeleted;
}
