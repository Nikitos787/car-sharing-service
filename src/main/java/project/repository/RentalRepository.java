package project.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import project.model.Rental;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    @Query("FROM Rental r WHERE (:isActive = false AND r.user.id =:userId "
            + "AND r.actualDate IS NOT NULL) "
            + "OR (:isActive = true AND r.user.id =:userId AND r.actualDate IS NULL)")
    List<Rental> findByIdAndIsActive(@Param("userId") Long userId,
                                     @Param("isActive") boolean isActive);

    @Query("FROM Rental r WHERE r.actualDate is NULL AND r.returnDate < :date")
    List<Rental> findOverdueRental(@Param("date") LocalDateTime dateTime);
}
