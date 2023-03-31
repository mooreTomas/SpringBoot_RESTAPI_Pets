package com.example.assignmenttwo_starter.controller;

import com.example.assignmenttwo_starter.config.PdfReportGenerator;
import com.example.assignmenttwo_starter.model.Customer;
import com.example.assignmenttwo_starter.model.Dog;
import com.example.assignmenttwo_starter.model.DogShowRegistration;
import com.example.assignmenttwo_starter.service.CustomerService;
import com.example.assignmenttwo_starter.service.DogService;
import com.example.assignmenttwo_starter.service.DogShowRegistrationService;
import com.example.assignmenttwo_starter.service.EmailService;

// import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/dog-show")
public class DogShowController {

    @Autowired
    private DogShowRegistrationService dogShowRegistrationService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private DogService dogService;

    @Autowired
    private PdfReportGenerator pdfReportGenerator;

    @Autowired
    private EmailService emailService;

    @PostMapping("/register/{customerId}/{dogName}")
//    @ApiOperation(value = "Register dog for dog show", notes = "Registers a dog for a dog show event")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "Dog registered successfully"),
//            @ApiResponse(responseCode = "400", description = "Dog is not vaccinated"),
//            @ApiResponse(responseCode = "404", description = "Customer or dog not found")
//    })
    public ResponseEntity<?> registerDogForShow(@PathVariable Integer customerId, @PathVariable String dogName, @RequestParam("eventDate") String eventDateString, @RequestParam("email") String email) throws MessagingException {
        Optional<Customer> customerOptional = customerService.findOneCustomer(Long.valueOf(customerId));
        if (!customerOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found");
        }

        Optional<Dog> dogOptional = dogService.findDogByNameAndCustomerId(dogName, customerId);
        if (!dogOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Dog not found, try again!");
        }

        Dog dog = dogOptional.get();
        if (!dog.isVaccinated()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Your dog " + dog.getName() + " is not vaccinated, can't enter dog show");
        }

        LocalDate eventDate = LocalDate.parse(eventDateString);

        // Check if the dog is already registered for the event date
        Optional<DogShowRegistration> existingRegistration = dogShowRegistrationService.findRegistrationByDogAndEventDate(dog, eventDate);
        if (existingRegistration.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Dog is already registered for this event date");
        }

        DogShowRegistration registration = new DogShowRegistration(null, eventDate, customerOptional.get(), dog);
        dogShowRegistrationService.saveDogShowRegistration(registration);

        // Send confirmation email after successful registration
        String subject = "Dog Show Registration Confirmation";
        String body = "Congratulations! Your dog " + dog.getName() + " has been successfully registered for the dog show on " + eventDateString + ".";
        List<DogShowRegistration> registrations = dogShowRegistrationService.findByEventDate(eventDate);
        emailService.sendConfirmationEmailWithPdfAttachment(email, subject, body, registrations, eventDate);

        return ResponseEntity.status(HttpStatus.CREATED).body("Dog successfully registered for the show");
    }


    @GetMapping("/{registrationId}")
//    @ApiOperation(value = "Get dog show registration", notes = "Retrieves the dog show registration for the given registration ID")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "Registration found successfully"),
//            @ApiResponse(responseCode = "404", description = "Registration not found")
//    })
    public ResponseEntity<?> getRegistration(@PathVariable Long registrationId) {
        Optional<DogShowRegistration> registrationOptional = dogShowRegistrationService.findRegistrationById(registrationId);
        if (!registrationOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Registration not found");
        }

        return ResponseEntity.ok(registrationOptional.get());
    }

    @DeleteMapping(value = "/{registrationId}", produces = {MediaType.APPLICATION_PDF_VALUE, MediaType.TEXT_PLAIN_VALUE})
//    @ApiOperation(value = "Delete dog show registration", notes = "Deletes the specified dog show registration")
//    @ApiResponses({
//            @ApiResponse(responseCode = "204", description = "Registration deleted successfully"),
//            @ApiResponse(responseCode = "404", description = "Registration not found")
//    })
    public ResponseEntity<?> deleteRegistration(@PathVariable Long registrationId) {
        Optional<DogShowRegistration> registrationOptional = dogShowRegistrationService.findRegistrationById(registrationId);
        if (!registrationOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Registration not found");
        }

        dogShowRegistrationService.deleteRegistration(registrationId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/report")
//    @ApiOperation(value = "Generate dog show report", notes = "Generates a PDF report of all registered dogs for the specified event date")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "Dog show report generated successfully"),
//            @ApiResponse(responseCode = "404", description = "No registrations found for the specified event date")
//    })
    public ResponseEntity<byte[]> getDogShowReport(@RequestParam("eventDate") String eventDateString) {
        LocalDate eventDate = LocalDate.parse(eventDateString);

        List<DogShowRegistration> registrations = dogShowRegistrationService.findByEventDate(eventDate);

        if (registrations != null && !registrations.isEmpty()) {
            byte[] pdfBytes = pdfReportGenerator.generateDogShowPdf(registrations, eventDate);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.builder("attachment").filename("DogShowRegistrations_" + eventDateString + ".pdf").build());
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } else {
            return (ResponseEntity<byte[]>) ResponseEntity.status(HttpStatus.NOT_FOUND);
        }
    }
}

