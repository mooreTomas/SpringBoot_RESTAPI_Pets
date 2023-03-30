package com.example.assignmenttwo_starter.config;

import com.example.assignmenttwo_starter.model.Customer;
import com.example.assignmenttwo_starter.model.Dog;
import com.example.assignmenttwo_starter.model.DogShowRegistration;
import com.example.assignmenttwo_starter.model.ImageData;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class PdfReportGenerator {

    public byte[] generateDogShowPdf(List<DogShowRegistration> registrations, LocalDate eventDate) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        // Add title at the top of the document
        try {
            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            Paragraph titleParagraph = new Paragraph("Participants in Upcoming DogShow")
                    .setFont(font)
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(titleParagraph);
            document.add(new Paragraph("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add event date to the top of the document
        Paragraph eventDateParagraph = new Paragraph("Dog Show Event Date: " + eventDate.toString());
        document.add(eventDateParagraph);

        // Iterate through the registrations and add their information to the document
        for (DogShowRegistration registration : registrations) {
            Dog dog = registration.getDog();
            Customer customer = registration.getCustomer();

            // Add customer and dog name
            document.add(new Paragraph("Customer: " + customer.getFirstName() + " " + customer.getLastName()));
            document.add(new Paragraph("Dog: " + dog.getName()));

            // Add dog image if it exists
            if (!dog.getImages().isEmpty()) {
                ImageData imageData = dog.getImages().get(0);
                Image img = new Image(ImageDataFactory.create(imageData.getImageData()));
                img.scaleToFit(100, 100); // Scale the image to fit within the specified dimensions
                document.add(img);
            }

            // Add a separator between registrations
            document.add(new Paragraph("\n----------------------------------------------\n"));
        }

        document.close();

        return outputStream.toByteArray();
    }
}
