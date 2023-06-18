package project.controler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.dto.request.RentalRequestDto;
import project.dto.response.RentalResponseDto;
import project.model.Rental;
import project.model.user.User;
import project.service.NotificationService;
import project.service.RentalService;
import project.service.UserService;
import project.service.mapper.RequestDtoMapper;
import project.service.mapper.ResponseDtoMapper;

@RestController
@RequestMapping("/rentals")
@RequiredArgsConstructor
public class RentalController {
    private final RentalService rentalService;
    private final ResponseDtoMapper<RentalResponseDto, Rental> responseDtoMapper;
    private final RequestDtoMapper<RentalRequestDto, Rental> requestDtoMapper;
    private final NotificationService notificationService;
    private final UserService userService;

    @PostMapping
    @Operation(summary = "endpoint for add rental",
            description = "endpoint fpr creating rental")
    public RentalResponseDto add(Authentication authentication,
                                 @Parameter(schema = @Schema(type = "String",
                                         defaultValue = "{ \n"
                                                 + "\"rentalDate\":\"date when you rent car\", \n"
                                                 + "\"rentalReturn\":\"date for return\", \n"
                                                 + "\"carId\": 1 \n"
                                                 + "}"))
                                 @RequestBody RentalRequestDto rentalRequestDto) {
        Rental rentalForSave = requestDtoMapper.mapToModel(rentalRequestDto);
        User user = userService.findByEmail(authentication.getName()).orElseThrow(() ->
                new NoSuchElementException(String.format("User with email: %s doesn't exist in DB",
                        authentication.getName())));
        rentalForSave.setUser(user);
        Rental savedRental = rentalService.save(rentalForSave);
        notificationService.sendMessageAboutSuccessRent(savedRental);
        return responseDtoMapper.mapToDto(savedRental);
    }

    @PutMapping("/{id}/return")
    @Operation(summary = "endpoint for return car by rental",
            description = "endpoint for return car only for manager because based on actual "
                    + "return date will calculate price to pay")
    public RentalResponseDto returnRental(
            @Parameter(schema = @Schema(type = "Integer", description = "rental id"))
            @PathVariable Long id) {
        notificationService
                .sendMessageToAdministrators(String
                        .format("Car from rental with id: %s was returned", id));
        return responseDtoMapper.mapToDto(rentalService.returnRental(id));
    }

    @GetMapping
    @Operation(summary = "endpoint for finding rental by user id and status",
            description = "endpoint for getting rental by user id and status(returned or not")
    public List<RentalResponseDto> findAllByUserIdAndIsAlive(
            @Parameter(schema = @Schema(type = "Integer", description = "user id"))
            @RequestParam Long userId,
            @Parameter(schema = @Schema(type = "boolean"), description = "true or false")
            @RequestParam boolean status) {
        return rentalService.findByIdAndIsActive(userId, status).stream()
                .map(responseDtoMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "endpoint for finding rental by id",
            description = "endpoint for getting rental by id")
    public RentalResponseDto findById(
            @Parameter(schema = @Schema(type = "Integer", description = "rental id"))
            @PathVariable Long id) {
        return responseDtoMapper.mapToDto(rentalService.findById(id));
    }

    @GetMapping("/my-rentals")
    @Operation(summary = "endpoint for getting all own rentals",
            description = "endpoint for getting your own rentals")
    public List<RentalResponseDto> findAllMyRentals(Authentication authentication) {
        return rentalService.findAll().stream()
                .filter(r -> r.getUser().getEmail().equals(authentication.getName()))
                .map(responseDtoMapper::mapToDto)
                .collect(Collectors.toList());
    }
}
