# Spring Boot RESTful API

This project makes use of a H2, in-memory database, and is a fully fledged API for navigating a pet services type website.

- Contains a broad suite of POST, GET, DELETE, PUT API functions
- Caching where appropriate
- Pagination where appropriate
- Consumes and produces JSONS, XMLs and text files
- Basic validation when adding to database
- Generate PDF functionality, HATEOAS principles - generate invoices (which is internationalised depending on user locale) 
- Can upload images to in memory database, also a functionality in place to store same image in an AWS bucket online (as a backup/archive/redundancy)
- JavaMailSender functionality incorporated, sends email with attached PDF, images etc.
