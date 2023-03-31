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



Markdown is a lightweight markup language based on the formatting conventions
that people naturally use in email.
As [John Gruber] writes on the [Markdown site][df1]

> The overriding design goal for Markdown's
> formatting syntax is to make it as readable
> as possible. The idea is that a
> Markdown-formatted document should be
> publishable as-is, as plain text, without
> looking like it's been marked up with tags
> or formatting instructions.

This text you see here is *actually- written in Markdown! To get a feel
for Markdown's syntax, type some text into the left window and
watch the results in the right.

## Tech

Dillinger uses a number of open source projects to work properly:

- [AngularJS] - HTML enhanced for web apps!
- [Ace Editor] - awesome web-based text editor
- [markdown-it] - Markdown parser done right. Fast and easy to extend.
- [Twitter Bootstrap] - great UI boilerplate for modern web apps
- [node.js] - evented I/O for the backend
- [Express] - fast node.js network app framework [@tjholowaychuk]
- [Gulp] - the streaming build system
- [Breakdance](https://breakdance.github.io/breakdance/) - HTML
to Markdown converter
- [jQuery] - duh

And of course Dillinger itself is open source with a [public repository][dill]
 on GitHub.

## Installation

Dillinger requires [Node.js](https://nodejs.org/) v10+ to run.

Install the dependencies and devDependencies and start the server.

```sh
cd dillinger
npm i
node app
```

For production environments...

```sh
npm install --production
NODE_ENV=production node app
```

## Plugins

Dillinger is currently extended with the following plugins.
Instructions on how to use them in your own application are linked below.

| Plugin | README |
| ------ | ------ |
| Dropbox | [plugins/dropbox/README.md][PlDb] |
| GitHub | [plugins/github/README.md][PlGh] |
| Google Drive | [plugins/googledrive/README.md][PlGd] |
| OneDrive | [plugins/onedrive/README.md][PlOd] |
| Medium | [plugins/medium/README.md][PlMe] |
| Google Analytics | [plugins/googleanalytics/README.md][PlGa] |

## Development

Want to contribute? Great!

Dillinger uses Gulp + Webpack for fast developing.
Make a change in your file and instantaneously see your updates!

Open your favorite Terminal and run these commands.

First Tab:

```sh
node app
```

Second Tab:

```sh
gulp watch
```

(optional) Third:

```sh
karma test
```

#### Building for source

For production release:

```sh
gulp build --prod
```

Generating pre-built zip archives for distribution:

```sh
gulp build dist --prod
```

## Docker

Dillinger is very easy to install and deploy in a Docker container.

By default, the Docker will expose port 8080, so change this within the
Dockerfile if necessary. When ready, simply use the Dockerfile to
build the image.

```sh
cd dillinger
docker build -t <youruser>/dillinger:${package.json.version} .
```

This will create the dillinger image and pull in the necessary dependencies.
Be sure to swap out `${package.json.version}` with the actual
version of Dillinger.

Once done, run the Docker image and map the port to whatever you wish on
your host. In this example, we simply map port 8000 of the host to
port 8080 of the Docker (or whatever port was exposed in the Dockerfile):

```sh
docker run -d -p 8000:8080 --restart=always --cap-add=SYS_ADMIN --name=dillinger <youruser>/dillinger:${package.json.version}
```

> Note: `--capt-add=SYS-ADMIN` is required for PDF rendering.

Verify the deployment by navigating to your server address in
your preferred browser.

```sh
127.0.0.1:8000
```

## License

MIT

**Free Software, Hell Yeah!**

[//]: # (These are reference links used in the body of this note and get stripped out when the markdown processor does its job. There is no need to format nicely because it shouldn't be seen. Thanks SO - http://stackoverflow.com/questions/4823468/store-comments-in-markdown-syntax)

   [dill]: <https://github.com/joemccann/dillinger>
   [git-repo-url]: <https://github.com/joemccann/dillinger.git>
   [john gruber]: <http://daringfireball.net>
   [df1]: <http://daringfireball.net/projects/markdown/>
   [markdown-it]: <https://github.com/markdown-it/markdown-it>
   [Ace Editor]: <http://ace.ajax.org>
   [node.js]: <http://nodejs.org>
   [Twitter Bootstrap]: <http://twitter.github.com/bootstrap/>
   [jQuery]: <http://jquery.com>
   [@tjholowaychuk]: <http://twitter.com/tjholowaychuk>
   [express]: <http://expressjs.com>
   [AngularJS]: <http://angularjs.org>
   [Gulp]: <http://gulpjs.com>

   [PlDb]: <https://github.com/joemccann/dillinger/tree/master/plugins/dropbox/README.md>
   [PlGh]: <https://github.com/joemccann/dillinger/tree/master/plugins/github/README.md>
   [PlGd]: <https://github.com/joemccann/dillinger/tree/master/plugins/googledrive/README.md>
   [PlOd]: <https://github.com/joemccann/dillinger/tree/master/plugins/onedrive/README.md>
   [PlMe]: <https://github.com/joemccann/dillinger/tree/master/plugins/medium/README.md>
   [PlGa]: <https://github.com/RahulHP/dillinger/blob/master/plugins/googleanalytics/README.md>
