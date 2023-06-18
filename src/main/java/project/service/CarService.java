package project.service;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import project.model.car.Car;

public interface CarService {
    Car save(Car car);

    List<Car> findAllByParams(Map<String, String> params);

    Page<Car> findAll(Pageable pageable);

    Car findById(Long id);

    Car update(Long id, Car car);

    void delete(Long id);

    Car addCarToInventory(Long id);

    Car removeCarFromInventory(Long id);
}
