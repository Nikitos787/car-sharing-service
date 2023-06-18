package project.dto.response;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class RentalResponseDto {
    private Long id;
    private LocalDateTime returnDate;
    private LocalDateTime rentalDate;
    private LocalDateTime actualDate;
    private Long userId;
    private Long carId;
}
