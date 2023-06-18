package project.dto.request;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RentalRequestDto {
    private LocalDateTime returnDate;
    private LocalDateTime rentalDate;
    private Long carId;
}
