package mate.capsharingapp.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import mate.capsharingapp.dto.car.CarFullResponseDto;
import mate.capsharingapp.dto.car.CarRequestDto;
import mate.capsharingapp.dto.car.CarShortResponseDto;
import mate.capsharingapp.dto.car.CarUpdateInventoryDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@Sql(scripts = {
        "classpath:database/car/add-cars-to-cars-table.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {
        "classpath:database/car/remove-cars-from-cars-table.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CarControllerTest {
    protected static MockMvc mockMvc;
    private static final CarFullResponseDto FIRST_CAR_FULL_RESPONSE_DTO =
            new CarFullResponseDto()
                    .setId(1L)
                    .setModel("M5")
                    .setBrand("BMW")
                    .setCarType("SEDAN")
                    .setInventory(10)
                    .setDailyFee(BigDecimal.valueOf(90).setScale(2));
    private static final CarFullResponseDto SECOND_CAR_FULL_RESPONSE_DTO =
            new CarFullResponseDto()
                    .setId(2L)
                    .setModel("A7")
                    .setBrand("AUDI")
                    .setCarType("SEDAN")
                    .setInventory(15)
                    .setDailyFee(BigDecimal.valueOf(80).setScale(2));
    private static final CarShortResponseDto FIRST_CAR_SHORT_RESPONSE_DTO =
            new CarShortResponseDto()
                    .setId(1L)
                    .setModel("M5")
                    .setCarType("SEDAN");
    private static final CarShortResponseDto SECOND_CAR_SHORT_RESPONSE_DTO =
            new CarShortResponseDto()
                    .setId(2L)
                    .setModel("A6")
                    .setCarType("SEDAN");
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext
    ) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    @DisplayName("Save a new car with valid data")
    public void saveCar_ValidRequest_ShouldReturnCarDto() throws Exception {
        CarRequestDto createCarRequestDto = new CarRequestDto()
                .setModel(FIRST_CAR_FULL_RESPONSE_DTO.getModel())
                .setBrand(FIRST_CAR_FULL_RESPONSE_DTO.getBrand())
                .setCarType(FIRST_CAR_FULL_RESPONSE_DTO.getCarType())
                .setInventory(FIRST_CAR_FULL_RESPONSE_DTO.getInventory())
                .setDailyFee(FIRST_CAR_FULL_RESPONSE_DTO.getDailyFee());
        CarFullResponseDto expectedCar = FIRST_CAR_FULL_RESPONSE_DTO;

        String jsonRequest = objectMapper.writeValueAsString(createCarRequestDto);

        MvcResult result = mockMvc.perform(
                        post("/cars")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        CarFullResponseDto actualCarDto = objectMapper.readValue(result.getResponse()
                .getContentAsString(), CarFullResponseDto.class);
        Assertions.assertNotNull(actualCarDto);
        Assertions.assertNotNull(actualCarDto.getId());
        EqualsBuilder.reflectionEquals(expectedCar, actualCarDto, "id");
    }

    @Test
    @DisplayName("Get all cars")
    void getAllCars_GivenCarsInCatalog_ShouldReturnAllCars() throws Exception {
        List<CarShortResponseDto> cars = new ArrayList<>();
        cars.add(FIRST_CAR_SHORT_RESPONSE_DTO);
        cars.add(SECOND_CAR_SHORT_RESPONSE_DTO);

        MvcResult result = mockMvc.perform(
                        get("/cars")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode contentNode = jsonNode.get("content");
        CarShortResponseDto[] actualCars =
                objectMapper.treeToValue(contentNode, CarShortResponseDto[].class);
        Assertions.assertEquals(cars.size(), actualCars.length);
    }

    @Test
    @DisplayName("Get car by id")
    void getCarById_ValidId_ShouldReturnValidCarDto() throws Exception {
        Long id = 2L;

        MvcResult result = mockMvc.perform(
                        get("/cars/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CarFullResponseDto actualCarDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), CarFullResponseDto.class
        );
        Assertions.assertNotNull(actualCarDto);
        EqualsBuilder.reflectionEquals(SECOND_CAR_FULL_RESPONSE_DTO, actualCarDto);
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    @DisplayName("Update car by id")
    void update_ValidRequest_ShouldReturnUpdatedCarDto() throws Exception {
        Long id = 2L;
        CarRequestDto updateCarRequestDto = new CarRequestDto()
                .setModel("updatedModel")
                .setBrand("updatedBrand")
                .setCarType("SUV")
                .setInventory(20)
                .setDailyFee(BigDecimal.valueOf(100).setScale(2));
        CarFullResponseDto expected = new CarFullResponseDto()
                .setModel(updateCarRequestDto.getModel())
                .setBrand(updateCarRequestDto.getBrand())
                .setCarType(updateCarRequestDto.getCarType())
                .setInventory(updateCarRequestDto.getInventory())
                .setDailyFee(updateCarRequestDto.getDailyFee());

        String jsonRequest = objectMapper.writeValueAsString(updateCarRequestDto);
        MvcResult result = mockMvc.perform(
                        put("/cars/{id}", id)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CarFullResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CarFullResponseDto.class
        );
        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    @DisplayName("Update car inventory by id")
    void updateCarInventory_ValidRequest_ShouldReturnUpdatedCarDto() throws Exception {
        Long id = 2L;
        CarUpdateInventoryDto updateCarRequestDto = new CarUpdateInventoryDto()
                .setInventory(20);
        CarFullResponseDto expected = SECOND_CAR_FULL_RESPONSE_DTO
                .setInventory(updateCarRequestDto.getInventory());

        String jsonRequest = objectMapper.writeValueAsString(updateCarRequestDto);
        MvcResult result = mockMvc.perform(
                        patch("/cars/{id}", id)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CarFullResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CarFullResponseDto.class
        );
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected.getInventory(), actual.getInventory());
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    @DisplayName("Delete car by id")
    void delete_ValidId_ShouldDeleteCorrectCar() throws Exception {
        Long id = 1L;

        mockMvc.perform(delete("/cars/{id}", id))
                .andExpect(status().isNoContent())
                .andReturn();
    }
}
