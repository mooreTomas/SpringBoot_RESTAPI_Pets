# Assignment 2

## Functionality listed below. After listing tasks, the Python script is also available.



.


## Task 1

- Fully implemented
- Basic functions implemented. GET, PUT, POST and DELETE
- GET single customer includes a link to all customers, as well as a to itself
- GET all customers has a paginated and non-paginated version
- Aforementioned Python script can consume requests from the API, namely, Get all, GET one and POST, with some appropriate validation
- POST Customer also enforces validation on email, phone, city etc with errors as appropriate
- Several controller methods are defined as Cacheable (particularly the GET all requests)
- Corresponding methods (such as delete, edit etc) have cacheevict where appropriate, so as not to return inaccurate cached requests



## Task 2
- Fully implemented
- Generates a PDF based on orderId. Contains all relevant information, including total cost, the products, the customer, date etc.
- Checks in place for order status and also a check for order items (existing orders without an orderItemCollection will not have invoices generated for them)
- invoice is fully internationalised for both Irish and English. New languages can easily be added modifying the properties file in the ResourceBundle
- return the JSON entity of orders associated with customers, with some links and their orderItemCollection etc.
- Minor DELETE, GET methods etc. for orders included as well
- 

## Task 3
- Several entities added:
    - ImageData, representing images added to the local database
    - Dog, representing a dog, which is associated with a customer
    - DogShowRegistration, representing registrations for various events
- A dog can be added to the Dog entity. Specify whether it is vaccinated or not (important for later), specify the owner id
- GET and DELETE methods here as well, won't be commonly used but nice to have
- Can upload an image for each dog (a customer can have more than 1 dog), enforces this in the controller method. Specifiy customer id and dogName in the URL mapping
- Image is uploaded locally, but a backup is also stored in the cloud; backup file is renamed so that it may be associated with customer later in case of data loss
- A customer can register any of their dogs to an event. Parameters include the date of the event (currently 1 hardcoded event in the DB, but more could be added in theory). Customer id and dogname specify the registration entry. Customer includes email as a paramter. They receive an email informing them of the successful registration, with attached PDF (more on this below)
- Some checks must be validated -> Dog must be vaccinated to enter, otehrwise an error is thrown; customer and dog name must be valid, and the dog must belong to that customer
- The email that is received to confirm the registration includes an attached PDF. The PDF is dynamically loaded with the current participants of the dog show. Customer and Dog name is included, and an iamge of each dog, if it exists on the database, is also included
- This PDF can also be generated as a direct API call, with the same functionality


## Adding Python code here, currently have GET (get all, get one) and POST working with this

```sh
import requests
import json

# set the API endpoint URL
SERVER_URL = "http://localhost:8080"

CONTROLLER_METHOD = "/customers"
GET_ALL_CUSTOMERS = ""

GET_ONE = "/"


def GetAll():
    response = requests.get(SERVER_URL + CONTROLLER_METHOD + GET_ALL_CUSTOMERS)
    customers = response.json()
    print(json.dumps(customers, indent=4))


# define input parameter here

def GetOneCust():
    """ Returns one customer from GET request """
    valid_input = False

    while not valid_input:
        x = input("Enter the ID of the customer to retrieve: ")
        valid_input = validateInput(x)

    response = requests.get(SERVER_URL + CONTROLLER_METHOD + GET_ONE + str(x))
    customer = response.json()
    print(json.dumps(customer, indent=4))


def validateInput(user_input):
    """
    Returns False for non-valid inputs.
    Returns user_input otherwise.
    """
    try:
        user_input = int(user_input)
        if user_input < 0:
            print(f"User input: {user_input} must be positive")
            return False
    except Exception as e:
        # Catch cases where integer conversion is invalid
        print('User input must be a valid integer')
        return False

    return user_input


def PostCustomer():
    headers = {"Content-Type": "application/json"}
    data = {"customerId": input("Enter customer ID: "), "firstName": input("Enter customer first name: "),
            "lastName": input("Enter customer last name: "), "email": input("Enter customer email: "),
            "phone": input("Enter customer phone: "), "address": input("Enter customer address: "),
            "city": input("Enter customer city: "), "country": input("Enter customer country: "),
            "postcode": input("Enter customer postcode: ")}

    response = requests.post(SERVER_URL + CONTROLLER_METHOD, headers=headers, json=data)
    print("POST method response: ", response.status_code)



if __name__ == '__main__':
    valid_input = False

    while not valid_input:
        user_input = input("Enter an integer value to specify which method to run: ")
        valid_input = validateInput(user_input)
        if not valid_input:
            print("Please try again. Not a valid input.")

    if user_input == "1":
        GetOneCust()
    elif user_input == "2":
        GetAll()
    elif user_input == "3":
        PostCustomer()



    
    
    
    ```
