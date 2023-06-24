package project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.dto.request.CarRequestDto;
import project.dto.response.CarResponseDto;
import project.model.car.Car;
import project.service.CarService;
import project.service.NotificationService;
import project.service.mapper.RequestDtoMapper;
import project.service.mapper.ResponseDtoMapper;
import project.util.RequestParamParser;

@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController {
    private final CarService carService;
    private final ResponseDtoMapper<CarResponseDto, Car> responseDtoMapper;
    private final RequestDtoMapper<CarRequestDto, Car> requestDtoMapper;
    private final NotificationService notificationService;

    @PostMapping
    @Operation(summary = "Endpoint for creating a car",
            description = "Endpoint to create a car in the database. Only managers are permitted.")
    public CarResponseDto add(
            @Parameter(schema = @Schema(implementation = CarRequestDto.class))
            @RequestBody CarRequestDto carRequestDto) {
        Car car = carService.save(requestDtoMapper.mapToModel(carRequestDto));
        notificationService.sendMessageToAdministrators(String
                .format("New car was created with id: %s", car.getId()));
        return responseDtoMapper.mapToDto(car);
    }

    @GetMapping
    @Operation(summary = "Endpoint to get all cars with pagination and sorting",
            description = "Endpoint to get all cars with pagination and sorting")
    public List<CarResponseDto> findAll(
            @Parameter(description = "Number of cars per page")
            @RequestParam(defaultValue = "10") Integer count,
            @Parameter(description = "Page number")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Sorting type (ASC or DESC)")
            @RequestParam(defaultValue = "id") String sortBy) {
        Sort sort = Sort.by(RequestParamParser.toSortOrders(sortBy));
        Pageable pageable = PageRequest.of(page, count, sort);
        return carService.findAll(pageable).stream()
                .map(responseDtoMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/by-params")
    @Operation(summary = "Endpoint to get cars by filtering their characteristics",
            description = "Endpoint to filter cars")
    public List<CarResponseDto> findAllByParams(
            @Parameter(description = "Filtering by field. For example: modelIn=X5")
            @RequestParam Map<String, String> params) {
        return carService.findAllByParams(params).stream()
                .map(responseDtoMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Endpoint to get a car by ID",
            description = "Endpoint to get a car by its ID")
    public CarResponseDto getById(
            @Parameter(description = "Car ID")
            @PathVariable Long id) {
        return responseDtoMapper.mapToDto(carService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Endpoint to update a car",
            description = "Endpoint to update a car")
    public CarResponseDto updateInfo(
            @Parameter(description = "Car ID")
            @PathVariable Long id,
            @Parameter(schema = @Schema(implementation = CarRequestDto.class))
            @Valid @RequestBody CarRequestDto carRequestDto) {
        Car updatedCar = carService.update(id, requestDtoMapper.mapToModel(carRequestDto));
        notificationService
                .sendMessageToAdministrators(String
                        .format("Car with id: %s was updated", updatedCar.getId()));
        return responseDtoMapper.mapToDto(updatedCar);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Endpoint to delete a car",
            description = "Endpoint to delete a car from the database")
    public void delete(
            @Parameter(description = "Car ID")
            @PathVariable Long id) {
        notificationService
                .sendMessageToAdministrators(String
                        .format("Car with id: %s was deleted", id));
        carService.delete(id);
    }

    @PostMapping("/add/{id}")
    @Operation(summary = "Endpoint to add a car to the inventory",
            description = "Endpoint to add one car to the inventory")
    public CarResponseDto addCarToInventory(
            @Parameter(description = "Car ID")
            @PathVariable Long id) {
        notificationService
                .sendMessageToAdministrators(String
                        .format("Car by id: %s was add to inventory", id));
        return responseDtoMapper.mapToDto(carService.addCarToInventory(id));
    }

    @DeleteMapping("/remove/{id}")
    @Operation(summary = "Endpoint to remove a car from the inventory",
            description = "Endpoint to remove one car from the inventory")
    public CarResponseDto removeCarFromInventory(
            @Parameter(description = "Car ID")
            @PathVariable Long id) {
        notificationService
                .sendMessageToAdministrators(String
                        .format("One car with id: %s was removed from inventory", id));
        return responseDtoMapper.mapToDto(carService.removeCarFromInventory(id));
    }
}
