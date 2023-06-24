package project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
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
    @Operation(summary = "Endpoint to add a rental",
            description = "Endpoint to create a rental")
    public RentalResponseDto add(Authentication authentication,
                                 @Parameter(schema = @Schema(implementation =
                                         RentalRequestDto.class))
                                 @RequestBody RentalRequestDto rentalRequestDto) {
        Rental rentalForSave = requestDtoMapper.mapToModel(rentalRequestDto);
        User user = userService.findByEmail(authentication.getName());
        rentalForSave.setUser(user);
        Rental savedRental = rentalService.save(rentalForSave);
        notificationService.sendMessageAboutSuccessRent(savedRental);
        return responseDtoMapper.mapToDto(savedRental);
    }

    @PutMapping("/{id}/return")
    @Operation(summary = "Endpoint to return a car for a rental",
            description = "Endpoint to return a car for a rental. Only managers are permitted, "
                   + "as the actual return date will be used to calculate the price to pay")
    public RentalResponseDto returnRental(
            @Parameter(description = "Rental ID")
            @PathVariable Long id) {
        notificationService
                .sendMessageToAdministrators(String
                        .format("Car from rental with id: %s was returned", id));
        return responseDtoMapper.mapToDto(rentalService.returnRental(id));
    }

    @GetMapping
    @Operation(summary = "Endpoint to find rentals by user ID and status",
            description = "Endpoint to get rentals by user ID and status (returned or not)")
    public List<RentalResponseDto> findAllByUserIdAndIsAlive(
            @Parameter(description = "User ID")
            @RequestParam Long userId,
            @Parameter(description = "Status (true or false)")
            @RequestParam boolean status) {
        return rentalService.findByIdAndIsActive(userId, status).stream()
                .map(responseDtoMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Endpoint to find a rental by ID",
            description = "Endpoint to get a rental by ID")
    public RentalResponseDto findById(
            @Parameter(description = "Rental ID")
            @PathVariable Long id) {
        return responseDtoMapper.mapToDto(rentalService.findById(id));
    }

    @GetMapping("/my-rentals")
    @Operation(summary = "Endpoint to get all own rentals",
            description = "Endpoint to get all rentals owned by the authenticated user")
    public List<RentalResponseDto> findAllMyRentals(Authentication authentication) {
        return rentalService.findAll().stream()
                .filter(r -> r.getUser().getEmail().equals(authentication.getName()))
                .map(responseDtoMapper::mapToDto)
                .collect(Collectors.toList());
    }
}
