package com.carsharingapp.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carsharingapp.dto.payment.PaymentFullResponseDto;
import com.carsharingapp.dto.payment.PaymentStatusResponseDto;
import com.carsharingapp.model.Car;
import com.carsharingapp.model.Payment;
import com.carsharingapp.model.Rental;
import com.carsharingapp.model.Role;
import com.carsharingapp.model.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@Sql(scripts = {
        "classpath:database/car/add-cars-to-cars-table.sql",
        "classpath:database/user/add-users-to-users-table.sql",
        "classpath:database/user/add-roles-to-roles-table.sql",
        "classpath:database/user/add-users-roles.sql",
        "classpath:database/rental/add-rentals-to-rentals-table.sql",
        "classpath:database/payment/add-payment-to-payments-table.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {
        "classpath:database/payment/remove-payments.sql",
        "classpath:database/rental/remove-rentals.sql",
        "classpath:database/user/remove-users-roles.sql",
        "classpath:database/user/remove-users.sql",
        "classpath:database/user/remove-roles.sql",
        "classpath:database/car/remove-cars-from-cars-table.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentControllerTest {
    protected static MockMvc mockMvc;
    private static final String PAYMENT_CANCELED_MESSAGE = "Payment session canceled";
    private static final Role USER_ROLE = new Role().setName(Role.RoleName.ROLE_USER);
    private static final Car FIRST_CAR = new Car()
            .setId(1L)
            .setModel("M5")
            .setBrand("BMW")
            .setCarType(Car.CarType.SEDAN)
            .setInventory(10)
            .setDailyFee(BigDecimal.valueOf(90));
    private static final User USER = new User()
            .setId(1L)
            .setFirstName("John")
            .setLastName("Doe")
            .setEmail("john.doe@example.com")
            .setPassword("password")
            .setRoles(new HashSet<>(Collections.singleton(USER_ROLE)));
    private static final Rental RENTAL = new Rental()
            .setId(1L)
            .setUser(USER)
            .setCar(FIRST_CAR)
            .setRentalDate(LocalDate.of(2024, 9, 10))
            .setReturnDate(LocalDate.of(2024, 9, 12))
            .setDeleted(false);
    private static final Payment PAYMENT = new Payment()
            .setId(1L)
            .setType(Payment.PaymentType.PAYMENT)
            .setStatus(Payment.PaymentStatus.PENDING)
            .setRental(RENTAL)
            .setAmountToPay(BigDecimal.valueOf(180).setScale(1))
            .setSessionId("sessionId")
            .setSessionUrl("sessionUrl");
    private static final PaymentFullResponseDto PAYMENT_FULL_RESPONSE_DTO =
            new PaymentFullResponseDto()
                    .setId(PAYMENT.getId())
                    .setType(PAYMENT.getType())
                    .setStatus(PAYMENT.getStatus())
                    .setRentalId(PAYMENT.getRental().getId())
                    .setSessionId(PAYMENT.getSessionId())
                    .setSessionUrl(PAYMENT.getSessionUrl())
                    .setAmountToPay(PAYMENT.getAmountToPay());
    private static final PaymentStatusResponseDto PAYMENT_STATUS_RESPONSE_DTO =
            new PaymentStatusResponseDto()
                    .setStatus(Payment.PaymentStatus.CANCELED)
                    .setSessionId(PAYMENT.getSessionId())
                    .setMessage(PAYMENT_CANCELED_MESSAGE);
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

    @WithUserDetails("admin@example.com")
    @Test
    @DisplayName("Get of payments of some user for admin")
    public void getAllPayments_withValidAdminData_shouldReturnPageOfPayments() throws Exception {
        List<PaymentFullResponseDto> expected = new ArrayList<>();
        expected.add(PAYMENT_FULL_RESPONSE_DTO);

        MvcResult result = mockMvc.perform(
                        get("/payments")
                                .param("userId", "1")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode contentNode = jsonNode.get("content");
        PaymentFullResponseDto[] actualPayments =
                objectMapper.treeToValue(contentNode, PaymentFullResponseDto[].class);
        Assertions.assertEquals(expected.size(), actualPayments.length);
        Assertions.assertEquals(expected.get(0), actualPayments[0]);
    }

    @WithUserDetails("john.doe@example.com")
    @Test
    @DisplayName("Get all user's payments")
    public void getAllPayments_withValidData_shouldReturnPageOfPayments() throws Exception {
        List<PaymentFullResponseDto> expected = new ArrayList<>();
        expected.add(PAYMENT_FULL_RESPONSE_DTO);

        MvcResult result = mockMvc.perform(
                        get("/payments")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode contentNode = jsonNode.get("content");
        PaymentFullResponseDto[] actualPayments =
                objectMapper.treeToValue(contentNode, PaymentFullResponseDto[].class);
        Assertions.assertEquals(expected.size(), actualPayments.length);
        Assertions.assertEquals(expected.get(0), actualPayments[0]);
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Handle cancel payment with valid session id")
    void handleCancel_withValidSessionId_ShouldReturnCancelMessage() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/payments/cancel/{sessionId}", "sessionId")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        PaymentStatusResponseDto actualPayment =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        PaymentStatusResponseDto.class);
        Assertions.assertNotNull(actualPayment);
        Assertions.assertEquals(PAYMENT_STATUS_RESPONSE_DTO, actualPayment);
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Handle cancel payment with invalid session id should throw exception")
    void handleCancel_withInvalidSessionId_ShouldThrowPaymentException() throws Exception {
        mockMvc.perform(
                        get("/payments/cancel/{sessionId}", "someId")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
