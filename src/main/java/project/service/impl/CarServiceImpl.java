package project.service.impl;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import project.model.car.Car;
import project.repository.CarRepository;
import project.repository.specification.CarSpecificationManager;
import project.service.CarService;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarSpecificationManager carSpecificationManager;

    @Override
    public Car save(Car car) {
        return carRepository.save(car);
    }

    @Override
    public List<Car> findAllByParams(Map<String, String> params) {
        Specification<Car> specification = null;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            Specification<Car> sp = carSpecificationManager.get(entry.getKey(),
                    entry.getValue().split(","));
            specification = specification == null ? Specification.where(sp) : specification.and(sp);
        }
        return carRepository.findAll(specification);
    }

    @Override
    public Page<Car> findAll(Pageable pageable) {
        return carRepository.findAll(pageable);
    }

    @Override
    public Car findById(Long id) {
        return carRepository.findByIdAndDeletedFalse(id).orElseThrow(()
                -> new NoSuchElementException(String
                .format("Can't find car with id: %s in DB", id)));
    }

    @Override
    public Car update(Long id, Car car) {
        Car carFromDb = findById(id);
        carFromDb.setCarType(car.getCarType());
        carFromDb.setBrand(car.getBrand());
        carFromDb.setInventory(car.getInventory());
        carFromDb.setDailyFee(car.getDailyFee());
        carFromDb.setModel(car.getModel());
        carFromDb.setDeleted(car.isDeleted());
        return carRepository.save(carFromDb);
    }

    @Override
    public void delete(Long id) {
        carRepository.safeDelete(id);
    }

    @Override
    public Car addCarToInventory(Long id) {
        Car car = findById(id);
        car.setInventory(car.getInventory() + 1);
        return carRepository.save(car);
    }

    @Override
    public Car removeCarFromInventory(Long id) {
        Car car = findById(id);
        if (car.getInventory() > 0) {
            car.setInventory(car.getInventory() - 1);
            return carRepository.save(car);
        }
        throw new RuntimeException(String.format("Can't take car with id: %s from inventory", id));
    }
}
