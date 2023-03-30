package com.example.assignmenttwo_starter.service;

import com.example.assignmenttwo_starter.config.PdfReportGenerator;
import com.example.assignmenttwo_starter.model.DogShowRegistration;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.List;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private PdfReportGenerator pdfReportGenerator;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendConfirmationEmailWithPdfAttachment(String to, String subject, String body, List<DogShowRegistration> registrations, LocalDate eventDate) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body);

        // Generate the PDF report
        byte[] pdfBytes = pdfReportGenerator.generateDogShowPdf(registrations, eventDate);

        // Attach the PDF report to the email
        helper.addAttachment("dog_show_report.pdf", new ByteArrayResource(pdfBytes));

        javaMailSender.send(message);
    }
}
