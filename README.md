

**Exchange Service**

all endpoints are secured with basicAuth *userName=admin* and *password=admin*

available endpoints: 
* reference exchange rates against EUR : http://localhost:8080//exchange/ref/?currencyPair=EUR/USD 
* reference exchange for different pairs:  http://localhost:8080//exchange/ref/?currencyPair=HUF/USD
* assumes an EUR based amount that need to be converted into the target curr:  http://localhost:8080/exchange/convert/?euroAmount=15&targetCurrency=USD
* retrieve a map of supported currencies and how many each one them was requested before:  http://localhost:8080/exchange/supported
* retrieve a Link for an interactive chart for a given currency measured against EUR:  http://localhost:8080/exchange/interactiveChart?targetCurrency=USD

**Remarks**
* there are so many points that I think needs refactoring and maybe a better approach but did not get to, we can discuss 
them if we got to the review.
  

**Running and Testing**
* the application is containerized in a very simple docker Image,  to build it run the following:

`docker build -t exhange-service .
  `

to run it run the following: 

`
docker run -p 8080:8080 exchange-service
`

the app should be available on localhost:8080 after that.
