package project.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import project.model.car.Car;

@Repository
public interface CarRepository extends JpaRepository<Car, Long>,
        JpaSpecificationExecutor<Car> {
    @Query("FROM Car c where c.isDeleted = false AND c.id = :id")
    Optional<Car> findByIdAndDeletedFalse(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("UPDATE Car c SET c.isDeleted = true WHERE c.id = :id")
    void safeDelete(Long id);

    @Query("FROM Car c WHERE c.isDeleted = false")
    Page<Car> findAll(Pageable pageable);
}
