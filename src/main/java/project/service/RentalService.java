package project.service;

import java.time.LocalDateTime;
import java.util.List;
import project.model.Rental;

public interface RentalService {
    Rental save(Rental rental);

    List<Rental> findByIdAndIsActive(Long userId, boolean isActive);

    Rental findById(Long id);

    Rental returnRental(Long id);

    List<Rental> findOverdueRental(LocalDateTime dateTime);

    Rental update(Long id, Rental rental);

    List<Rental> findAll();
}
