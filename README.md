# assignment-two-2023-mooreTomas
assignment-two-2023-mooreTomas created by GitHub Classroom


## Adding Python code here, currently have GET (get all, get one) and POST working with this

```sh
import requests
import json

# set the API endpoint URL
serverUrl = "http://localhost:8080"

controllerMethod = "/customers"
getAllCustomers = ""

getone = "/"



def getAll():
    response = requests.get(serverUrl + controllerMethod + getAllCustomers)
    customers = response.json()
    print(customers)


# define input parameter here

def getonecust():
    x = input("Enter the ID of the customer to retrieve: ")
    response = requests.get(serverUrl + controllerMethod + getone + str(x))
    customer = response.json()
    print(customer)


def postCustomer():
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
    response = requests.post(serverUrl + controllerMethod, headers=headers, json=data)
    print(response.status_code)


if __name__ == '__main__':
    getonecust()
    getAll()
    postCustomer()
    
    
    
    ```
