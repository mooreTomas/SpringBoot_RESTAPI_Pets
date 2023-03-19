# assignment-two-2023-mooreTomas
assignment-two-2023-mooreTomas created by GitHub Classroom


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
    data = {
        "customerId": 999999,
        "firstName": "Mary",
        "lastName": "aaaaa",
        "email": "mking@hotmail.com",
        "phone": "0869876543",
        "address": "456 High Street",
        "city": "London",
        "country": "United Kingdom",
        "postcode": "W1B 2ES"
    }
    response = requests.post(SERVER_URL + CONTROLLER_METHOD, headers=headers, json=data)
    print("POST method response: ", response.status_code)


if __name__ == '__main__':
    GetOneCust()
    GetAll()
    PostCustomer()

    
    
    
    ```
