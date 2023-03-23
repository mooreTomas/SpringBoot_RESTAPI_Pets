# assignment-two-2023-mooreTomas
assignment-two-2023-mooreTomas created by GitHub Classroom


## Adding Python code here, currently have GET (get all, get one) and POST working with this
## Python code now allows user to select which method to run. POST method now allows user-specified input

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
