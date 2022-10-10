package com.endava.internship.mocking.repository;

import com.endava.internship.mocking.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemPaymentRepositoryTest {
    private InMemPaymentRepository inMemPaymentRepository;
    private final Payment payment = new Payment(1, 32.2, "message");
    private final Payment payment2 = new Payment(2, 10.5, "message2");
    private final Payment payment3 = new Payment(3, 17.7, "message3");


    @BeforeEach
    void setUp() {
        inMemPaymentRepository = new InMemPaymentRepository();
    }

    @Test
    void findById() {
        UUID uuid = payment.getPaymentId();
        inMemPaymentRepository.save(payment);

        assertEquals(inMemPaymentRepository.findById(uuid), Optional.of(payment));
    }

    @Test
    void findById_throwExceptionWhenNull() {
        assertThrows(IllegalArgumentException.class, () -> inMemPaymentRepository.findById(null),
                "Payment id must not be null");
    }

    @Test
    void findAll() {
        List<Payment> paymentList = asList(payment, payment2, payment3);
        inMemPaymentRepository.save(payment);
        inMemPaymentRepository.save(payment2);
        inMemPaymentRepository.save(payment3);

        assertThat(inMemPaymentRepository.findAll()).containsExactlyInAnyOrderElementsOf(paymentList);
    }

    @Test
    void save() {
        assertEquals(inMemPaymentRepository.save(payment), payment);
    }

    @Test
    void save_throwsExceptionWhenNull() {
        assertThrows(IllegalArgumentException.class, () -> inMemPaymentRepository.save(null),
                "Payment must not be null");
    }

    @Test
    void save_throwsExceptionWhenPaymentIsAlreadySaved() {
        inMemPaymentRepository.save(payment);

        assertTrue(inMemPaymentRepository.findById(payment.getPaymentId()).isPresent());
        assertThrows(IllegalArgumentException.class, () -> inMemPaymentRepository.save(payment));
    }

    @Test
    void editMessage() {
        inMemPaymentRepository.save(payment);
        String msg = "new message";
        inMemPaymentRepository.editMessage(payment.getPaymentId(), msg);

        assertEquals(inMemPaymentRepository.editMessage(payment.getPaymentId(), msg), payment);
    }

    @Test
    void editMessage_throwsExceptionWhenPaymentIsNull() {
        assertThrows(NoSuchElementException.class,
                () -> inMemPaymentRepository.editMessage(payment.getPaymentId(), "message"));
    }
}