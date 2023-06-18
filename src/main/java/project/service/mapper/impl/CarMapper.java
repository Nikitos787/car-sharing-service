package project.service.mapper.impl;

import org.springframework.stereotype.Component;
import project.dto.request.CarRequestDto;
import project.dto.response.CarResponseDto;
import project.model.car.Car;
import project.model.car.CarType;
import project.service.mapper.RequestDtoMapper;
import project.service.mapper.ResponseDtoMapper;

@Component
public class CarMapper implements RequestDtoMapper<CarRequestDto, Car>,
        ResponseDtoMapper<CarResponseDto, Car> {
    @Override
    public Car mapToModel(CarRequestDto dto) {
        Car car = new Car();
        car.setModel(dto.getModel());
        car.setCarType(CarType.valueOf(dto.getCarType().name().toUpperCase()));
        car.setInventory(dto.getInventory());
        car.setBrand(dto.getBrand());
        car.setDailyFee(dto.getDailyFee());
        return car;
    }

    @Override
    public CarResponseDto mapToDto(Car car) {
        CarResponseDto dto = new CarResponseDto();
        dto.setId(car.getId());
        dto.setDeleted(car.isDeleted());
        dto.setInventory(car.getInventory());
        dto.setBrand(car.getBrand());
        dto.setModel(car.getModel());
        dto.setCarType(car.getCarType());
        dto.setDailyFee(car.getDailyFee());
        return dto;
    }
}
