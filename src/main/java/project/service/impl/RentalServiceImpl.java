package project.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.model.Rental;
import project.model.car.Car;
import project.repository.RentalRepository;
import project.service.CarService;
import project.service.RentalService;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final CarService carService;

    @Override
    public Rental save(Rental rental) {
        Car carFromDb = carService.findById(rental.getCar().getId());
        if (carFromDb.getInventory() > 0) {
            carFromDb.setInventory(carFromDb.getInventory() - 1);
            carService.update(carFromDb.getId(), carFromDb);
            return rentalRepository.save(rental);
        }
        throw new RuntimeException("Can't save rental to db, cars in inventory is less than 0");
    }

    @Override
    public List<Rental> findByIdAndIsActive(Long userId, boolean isActive) {
        return rentalRepository.findByIdAndIsActive(userId, isActive);
    }

    @Override
    public Rental findById(Long id) {
        return rentalRepository.findById(id).orElseThrow(()
                -> new NoSuchElementException(String
                .format("Can't find rental by id: %s in DB", id)));
    }

    @Override
    public Rental returnRental(Long id) {
        Rental rental = findById(id);
        Car carToReturn = carService.findById(rental.getCar().getId());
        carToReturn.setInventory(carToReturn.getInventory() + 1);
        carService.update(carToReturn.getId(), carToReturn);
        rental.setActualDate(LocalDateTime.now());
        return update(id, rental);
    }

    @Override
    public List<Rental> findOverdueRental(LocalDateTime dateTime) {
        return rentalRepository.findOverdueRental(dateTime);
    }

    @Override
    public Rental update(Long id, Rental rental) {
        Rental rentalFromDb = rentalRepository.findById(id).orElseThrow(()
                -> new NoSuchElementException(String
                .format("Can't find rental by id: %s", id)));
        rentalFromDb.setCar(rental.getCar());
        rentalFromDb.setUser(rental.getUser());
        rentalFromDb.setRentalDate(rental.getRentalDate());
        rentalFromDb.setReturnDate(rental.getReturnDate());
        rentalFromDb.setActualDate(rental.getActualDate());
        return rentalRepository.save(rentalFromDb);
    }

    @Override
    public List<Rental> findAll() {
        return rentalRepository.findAll();
    }
}
