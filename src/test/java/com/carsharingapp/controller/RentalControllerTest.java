package com.carsharingapp.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carsharingapp.dto.car.CarFullResponseDto;
import com.carsharingapp.dto.car.CarShortResponseDto;
import com.carsharingapp.dto.rental.RentalFullResponseDto;
import com.carsharingapp.dto.rental.RentalRequestDto;
import com.carsharingapp.dto.rental.RentalResponseDto;
import com.carsharingapp.dto.rental.RentalSetActualReturnDateDto;
import com.carsharingapp.dto.user.UserResponseDto;
import com.carsharingapp.model.Car;
import com.carsharingapp.model.Rental;
import com.carsharingapp.model.Role;
import com.carsharingapp.model.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@Sql(scripts = {
        "classpath:database/car/add-cars-to-cars-table.sql",
        "classpath:database/user/add-users-to-users-table.sql",
        "classpath:database/user/add-roles-to-roles-table.sql",
        "classpath:database/user/add-users-roles.sql",
        "classpath:database/rental/add-rentals-to-rentals-table.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {
        "classpath:database/rental/remove-rentals.sql",
        "classpath:database/user/remove-users-roles.sql",
        "classpath:database/car/remove-cars-from-cars-table.sql",
        "classpath:database/user/remove-users.sql",
        "classpath:database/user/remove-roles.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RentalControllerTest {
    protected static MockMvc mockMvc;
    private static final Role USER_ROLE = new Role().setName(Role.RoleName.ROLE_USER);
    private static final User USER = new User()
            .setId(1L)
            .setFirstName("John")
            .setLastName("Doe")
            .setEmail("john.doe@example.com")
            .setPassword("password")
            .setRoles(new HashSet<>(Collections.singleton(USER_ROLE)));
    private static final UserResponseDto USER_RESPONSE_DTO = new UserResponseDto()
            .setFirstName(USER.getFirstName())
            .setLastName(USER.getLastName())
            .setEmail(USER.getEmail());
    private static final Car CAR = new Car()
            .setId(1L)
            .setModel("M5")
            .setBrand("BMW")
            .setCarType(Car.CarType.SEDAN)
            .setInventory(10)
            .setDailyFee(BigDecimal.valueOf(90));
    private static final CarFullResponseDto CAR_RESPONSE_DTO = new CarFullResponseDto()
            .setId(CAR.getId())
            .setModel(CAR.getModel())
            .setBrand(CAR.getBrand())
            .setCarType(CAR.getCarType().name())
            .setInventory(CAR.getInventory())
            .setDailyFee(CAR.getDailyFee());
    private static final Rental ACTIVE_RENTAL = new Rental()
            .setId(1L)
            .setUser(USER)
            .setCar(CAR)
            .setRentalDate(LocalDate.of(2024, 7, 20))
            .setReturnDate(LocalDate.of(2024, 7, 29));
    private static final RentalFullResponseDto ACTIVE_RENTAL_RESPONSE_DTO =
            new RentalFullResponseDto()
                    .setId(ACTIVE_RENTAL.getId())
                    .setCar(CAR_RESPONSE_DTO)
                    .setUser(USER_RESPONSE_DTO)
                    .setRentalDate(ACTIVE_RENTAL.getRentalDate())
                    .setReturnDate(ACTIVE_RENTAL.getReturnDate());
    private static final RentalResponseDto ACTIVE_RENTAL_SHORT_RESPONSE_DTO =
            new RentalResponseDto()
                    .setId(ACTIVE_RENTAL.getId())
                    .setCarId(CAR.getId())
                    .setUserId(USER.getId())
                    .setRentalDate(ACTIVE_RENTAL.getRentalDate())
                    .setReturnDate(ACTIVE_RENTAL.getReturnDate());
    private static final RentalRequestDto ACTIVE_RENTAL_REQUEST_DTO =
            new RentalRequestDto()
                    .setCarId(1L)
                    .setRentalDate(ACTIVE_RENTAL.getRentalDate())
                    .setReturnDate(ACTIVE_RENTAL.getReturnDate());
    private static final RentalSetActualReturnDateDto RETURN_DATE_DTO =
            new RentalSetActualReturnDateDto()
                    .setActualReturnDate(LocalDate.of(2024, 8, 1));
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @WithUserDetails("john.doe@example.com")
    @Test
    @DisplayName("Create a new rental")
    public void createRental_ValidRequest_WillReturnRentalDto() throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(ACTIVE_RENTAL_REQUEST_DTO);

        MvcResult result = mockMvc.perform(
                        post("/rentals")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        RentalFullResponseDto actualResponseDto = objectMapper
                .readValue(result.getResponse().getContentAsString(), RentalFullResponseDto.class);

        Assertions.assertNotNull(actualResponseDto);
        Assertions.assertNotNull(actualResponseDto.getId());
        EqualsBuilder.reflectionEquals(ACTIVE_RENTAL_RESPONSE_DTO, actualResponseDto, "id");
    }

    @WithUserDetails("john.doe@example.com")
    @Test
    @DisplayName("Get all rentals by active status")
    public void getAllRentalsByActiveStatus_ShouldReturnListOfRentals() throws Exception {
        List<RentalResponseDto> expectedList = Arrays.asList(ACTIVE_RENTAL_SHORT_RESPONSE_DTO);

        MvcResult result = mockMvc.perform(
                        get("/rentals")
                                .param("isActive", "true")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode contentNode = jsonNode.get("content");
        CarShortResponseDto[] actual =
                objectMapper.treeToValue(contentNode, CarShortResponseDto[].class);
        Assertions.assertEquals(expectedList.size(), actual.length);
    }

    @WithUserDetails("john.doe@example.com")
    @Test
    @DisplayName("Get rental by id")
    public void getRentalById_ValidId_ShouldReturnRentalDto() throws Exception {
        Long id = 1L;

        MvcResult result = mockMvc.perform(
                        get("/rentals/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        RentalFullResponseDto actualResponseDto = objectMapper
                .readValue(result.getResponse().getContentAsString(), RentalFullResponseDto.class);
        Assertions.assertNotNull(actualResponseDto);
        Assertions.assertEquals(ACTIVE_RENTAL_RESPONSE_DTO.getId(), actualResponseDto.getId());
    }

    @WithUserDetails("john.doe@example.com")
    @Test
    @DisplayName("Return rental by id")
    public void returnRental_ValidId_ShouldReturnRentalDto() throws Exception {
        Long id = 1L;

        String jsonRequest = objectMapper.writeValueAsString(RETURN_DATE_DTO);

        MvcResult result = mockMvc.perform(
                        post("/rentals/{id}/return", id)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        RentalResponseDto actualResponseDto = objectMapper
                .readValue(result.getResponse().getContentAsString(), RentalResponseDto.class);
        Assertions.assertNotNull(actualResponseDto);
        Assertions.assertEquals(ACTIVE_RENTAL_SHORT_RESPONSE_DTO.getId(),
                actualResponseDto.getId());
    }
}
