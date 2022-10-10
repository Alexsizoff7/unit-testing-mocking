package com.endava.internship.mocking.service;


import com.endava.internship.mocking.model.Status;
import com.endava.internship.mocking.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BasicValidationServiceTest {
    private BasicValidationService basicValidationService;

    @BeforeEach
    void setUp() {
        basicValidationService = new BasicValidationService();
    }

    @Test
    void validateAmount() {
        assertDoesNotThrow(() -> basicValidationService.validateAmount(32.2));
    }

    @Test
    void validateAmount_throwsExceptionWhenNull() {
        assertThrows(IllegalArgumentException.class, () -> basicValidationService.validateAmount(null));
    }

    @Test
    void validateAmount_throwsExceptionWhenNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> basicValidationService.validateAmount(-32.2));
    }

    @Test
    void validatePaymentId() {
        assertDoesNotThrow(() -> basicValidationService.validatePaymentId(UUID.randomUUID()));
    }

    @Test
    void validatePaymentId_throwsExceptionWhenNull() {
        assertThrows(IllegalArgumentException.class, () -> basicValidationService.validatePaymentId(null));
    }

    @Test
    void validateUserId() {
        assertDoesNotThrow(() -> basicValidationService.validateUserId(1));
    }

    @Test
    void validateUserId_throwsExceptionWhenNull() {
        assertThrows(IllegalArgumentException.class, () -> basicValidationService.validateUserId(null));
    }

    @Test
    void validateUser() {
        User user = new User(1, "Alex", Status.ACTIVE);
        assertDoesNotThrow(() -> basicValidationService.validateUser(user));
    }

    @Test
    void validateUser_throwsExceptionWhenUserInactive() {
        User user2 = new User(2, "John Doe", Status.INACTIVE);
        assertThrows(IllegalArgumentException.class, () -> basicValidationService.validateUser(user2));
    }

    @Test
    void validateMessage() {
        assertDoesNotThrow(() -> basicValidationService.validateMessage("Payment from user Alex"));
    }

    @Test
    void validateMessage_throwsExceptionWhenNull() {
        assertThrows(IllegalArgumentException.class, () -> basicValidationService.validateMessage(null));
    }
}