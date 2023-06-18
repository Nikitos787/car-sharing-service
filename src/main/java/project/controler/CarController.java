package project.controler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Operation(summary = "endpoint for creating car",
            description = "endpoint for create car in DB. Only manager has permitting")
    public CarResponseDto add(
            @Parameter(schema = @Schema(type = "String",
                    defaultValue = "{\n"
                            + "\"model\":\"car model\", \n"
                            + "\"brand\":\"car brand\",\n"
                            + "\"carType\":\"car type\", \n"
                            + "\"inventory\":6, \n"
                            + "\"dailyFee\":123.00 \n"
                            + "}"))
            @RequestBody CarRequestDto carRequestDto) {
        Car car = carService.save(requestDtoMapper.mapToModel(carRequestDto));
        notificationService.sendMessageToAdministrators("New car was created with id: "
                + car.getId());
        return responseDtoMapper.mapToDto(car);
    }

    @GetMapping
    @Operation(summary = "endpoint for get all car with pagination and sorting",
            description = "endpoint for getting all cars with pagination and sorting")
    public List<CarResponseDto> findAll(
            @Parameter(schema = @Schema(type = "Integer", description = "Car per page"))
            @RequestParam(defaultValue = "10") Integer count,
            @Parameter(schema = @Schema(type = "Integer", description = "Number of page"))
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(schema = @Schema(type = "String", description = "Sorting type (DESC or ASC"))
            @RequestParam(defaultValue = "id") String sortBy) {
        Sort sort = Sort.by(RequestParamParser.toSortOrders(sortBy));
        Pageable pageable = PageRequest.of(page, count, sort);
        return carService.findAll(pageable).stream()
                .map(responseDtoMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/by-params")
    @Operation(summary = "endpoint for getting car and filtering their characteristics",
            description = "endpoint for filtering car")
    public List<CarResponseDto> findAllByParams(
            @Parameter(schema = @Schema(type = "String",
                    description = "filtering by field. For example: modelIn=X5"))
            @RequestParam Map<String, String> params) {
        return carService.findAllByParams(params).stream()
                .map(responseDtoMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "endpoint for getting car by id",
            description = "endpoint for getting car by id")
    public CarResponseDto getById(
            @Parameter(schema = @Schema(type = "Integer", description = "Car id"))
            @PathVariable Long id) {
        return responseDtoMapper.mapToDto(carService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "endpoint for update car",
            description = "endpoint for updating car")
    public CarResponseDto updateInfo(
            @Parameter(schema = @Schema(type = "Integer", description = "Car id"))
            @PathVariable Long id,
            @Parameter(schema = @Schema(type = "String",
                    defaultValue = "{\n"
                            + "\"model\":\"X5\",\n"
                            + "\"brand\":\"BMW\",\n"
                            + "\"carType\":\"UNIVERSAL\",\n"
                            + "\"inventory\":5, \n"
                            + "\"dailyFee\":100.00 \n"
                            + "}"))

            @RequestBody CarRequestDto carRequestDto) {
        Car updatedCar = carService.update(id, requestDtoMapper.mapToModel(carRequestDto));
        notificationService
                .sendMessageToAdministrators(String
                        .format("Car with id: %s was updated", updatedCar.getId()));
        return responseDtoMapper.mapToDto(updatedCar);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "endpoint for deleting car",
            description = "endpoint for deleting car from DB")
    public void delete(
            @Parameter(schema = @Schema(type = "Integer", description = "Car id"))
            @PathVariable Long id) {
        notificationService
                .sendMessageToAdministrators(String
                        .format("Car with id: %s was deleted", id));
        carService.delete(id);
    }

    @PostMapping("/add/{id}")
    @Operation(summary = "endpoint for adding car",
            description = "endpoint for adding 1 car to inventory")
    public CarResponseDto addCarToInventory(
            @Parameter(schema = @Schema(type = "Integer", description = "Car id"))
            @PathVariable Long id) {
        notificationService
                .sendMessageToAdministrators(String
                        .format("Car by id: %s was add ot inventory", id));
        return responseDtoMapper.mapToDto(carService.addCarToInventory(id));
    }

    @DeleteMapping("/remove/{id}")
    @Operation(summary = "endpoint for remove car",
            description = "endpoint for removing 1 car from inventory")
    public CarResponseDto removeCarFromInventory(
            @Parameter(schema = @Schema(type = "Integer", description = "Car id"))
            @PathVariable Long id) {
        notificationService
                .sendMessageToAdministrators(String
                        .format("One car with id: %s was removed from inventory", id));
        return responseDtoMapper.mapToDto(carService.removeCarFromInventory(id));
    }
}
