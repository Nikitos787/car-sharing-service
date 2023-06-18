package project.service.mapper.impl;

import org.springframework.stereotype.Component;
import project.dto.request.RentalRequestDto;
import project.dto.response.RentalResponseDto;
import project.model.Rental;
import project.model.car.Car;
import project.service.mapper.RequestDtoMapper;
import project.service.mapper.ResponseDtoMapper;

@Component
public class RentalMapper implements RequestDtoMapper<RentalRequestDto, Rental>,
        ResponseDtoMapper<RentalResponseDto, Rental> {

    @Override
    public Rental mapToModel(RentalRequestDto dto) {
        Car car = new Car();
        car.setId(dto.getCarId());
        Rental rental = new Rental();
        rental.setCar(car);
        rental.setReturnDate(dto.getReturnDate());
        rental.setRentalDate(dto.getRentalDate());
        return rental;
    }

    @Override
    public RentalResponseDto mapToDto(Rental rental) {
        RentalResponseDto dto = new RentalResponseDto();
        dto.setId(rental.getId());
        dto.setRentalDate(rental.getRentalDate());
        dto.setActualDate(rental.getActualDate());
        dto.setReturnDate(rental.getReturnDate());
        dto.setCarId(rental.getCar().getId());
        dto.setUserId(rental.getUser().getId());
        return dto;
    }
}
