package com._pearls.cms.service;

import com._pearls.cms.service.impl.EmailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "fromEmail", "noreply@contactmanagement.com");
    }

    @Test
    @DisplayName("sendOtp should build and send email message correctly")
    void testSendOtpSuccess() {
        String to = "user@example.com";
        String otp = "123456";

        emailService.sendOtp(to, otp);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertNotNull(sentMessage.getTo());
        assertEquals(to, sentMessage.getTo()[0]);
        assertEquals("noreply@contactmanagement.com", sentMessage.getFrom());
        assertEquals("Account Verification OTP", sentMessage.getSubject());
        assertNotNull(sentMessage.getText());
        assertTrue(sentMessage.getText().contains(otp));
    }

    @Test
    @DisplayName("sendOtp should throw ApiException on mail sending failure")
    void testSendOtpExceptionHandling() {
        doThrow(new MailSendException("SMTP connection failed")).when(mailSender).send(any(SimpleMailMessage.class));

        com._pearls.cms.common.exception.ApiException exception = org.junit.jupiter.api.Assertions.assertThrows(
            com._pearls.cms.common.exception.ApiException.class, 
            () -> emailService.sendOtp("user@example.com", "123456")
        );
        assertEquals(com._pearls.cms.common.exception.ErrorCode.EMAIL_DELIVERY_FAILED, exception.getErrorCode());
    }
}
