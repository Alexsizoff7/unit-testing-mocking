package com.endava.internship.mocking.service;

import com.endava.internship.mocking.model.Payment;
import com.endava.internship.mocking.model.Status;
import com.endava.internship.mocking.model.User;
import com.endava.internship.mocking.repository.PaymentRepository;
import com.endava.internship.mocking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    final Integer userIdMock = 6;
    final Double amountMock = 32.2;
    final User user = new User(userIdMock, "Alex", Status.ACTIVE);
    final UUID uuid = UUID.randomUUID();
    final String msg = "Payment from user " + user.getName();


    @Mock
    private UserRepository userRepositoryMock;
    @Mock
    private PaymentRepository paymentRepositoryMock;
    @Mock
    private ValidationService validationServiceMock;
    @InjectMocks
    private PaymentService paymentServiceWithMocks;
    @Captor
    ArgumentCaptor<Payment> captor;

    @BeforeEach
    void setUp() {
    }

    @Test
    void createPayment() {
        when(userRepositoryMock.findById(anyInt())).thenReturn(Optional.of(user));
        when(paymentRepositoryMock.save(any())).thenReturn(new Payment(userIdMock, amountMock, msg));
        paymentServiceWithMocks.createPayment(userIdMock, amountMock);

        verify(userRepositoryMock).findById(userIdMock);
        verify(validationServiceMock, times(1)).validateUserId(userIdMock);
        verify(validationServiceMock, times(1)).validateAmount(amountMock);
        verify(validationServiceMock, times(1)).validateUser(user);
        verify(paymentRepositoryMock, times(1)).save(captor.capture());
        Payment paymentMock = captor.getValue();
        assertEquals(paymentMock.getUserId(), user.getId());
        assertEquals(paymentMock.getAmount(), amountMock);
        assertEquals(paymentMock.getMessage(), msg);
    }

    @Test
    void createPayment_throwsExceptionIfUserListIsEmpty() {
        when(userRepositoryMock.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> paymentServiceWithMocks.createPayment(7, 55.5));
    }

    @Test
    void createPayment_invalidUserId_ThrowsIllegalArgumentException() {
        doThrow(new IllegalArgumentException()).when(validationServiceMock).validateUserId(any());
        assertThrows(IllegalArgumentException.class, () -> paymentServiceWithMocks.createPayment(null, 55d));
    }

    @Test
    void editMessage() {
        when(paymentRepositoryMock.editMessage(any(), anyString())).thenReturn(new Payment(userIdMock, amountMock, msg));
        paymentServiceWithMocks.editPaymentMessage(uuid, msg);

        verify(validationServiceMock, atMostOnce()).validatePaymentId(uuid);
        verify(validationServiceMock, atMostOnce()).validateMessage(msg);
        verify(paymentRepositoryMock, atMostOnce()).editMessage(uuid, msg);
    }

    @Test
    void editMessage_throwsExceptionIfPaymentWithIdNotFound() {
        doThrow(new NoSuchElementException()).when(paymentRepositoryMock).editMessage(any(), anyString());

        assertThrows(NoSuchElementException.class,
                () -> paymentServiceWithMocks.editPaymentMessage(UUID.randomUUID(), msg));
    }

    @Test
    void getAllByAmountExceeding() {
        List<Payment> paymentList = asList(
                new Payment(8, 17.55, "message8"),
                new Payment(9, 40.9, "message9"),
                new Payment(10, 65.3, "message10"),
                new Payment(11, 38d, "message11"),
                new Payment(12, 32.2, "message12")
        );
        when(paymentRepositoryMock.findAll()).thenReturn(paymentList);

        assertTrue(paymentServiceWithMocks.getAllByAmountExceeding(amountMock)
                .stream().allMatch(payment -> payment.getAmount() > amountMock));
    }
}
