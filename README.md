# Coding challenge - Finmid

## Setup

You need to have a mysql database running with a database named: 'bank' 
In this case, you can use a docker container with the following command:

```bash
```shell script
$ docker run --rm --name test-mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=123456 -e MYSQL_DATABASE=bank -d mysql:latest
```

## Running the app

```shell script
$ gradle bootRun 
```

## Manual testing

```shell script
## Create an account
$ curl --location --request POST 'localhost:8080/account' \
--header 'Content-Type: application/json' \
--data-raw '{
	"balance": "200.00"
}'

## Get an account
$ curl --location --request GET 'localhost:8080/account/{id}'

## Update an account
$ curl --location --request PUT 'localhost:8080/account/{id}' \
--header 'Content-Type: application/json' \
--data-raw '{
	"balance": "200.00"
}'

## Make a transaction
$ curl --location --request POST 'localhost:8080/transaction' \
--header 'Content-Type: application/json' \
--data-raw '{
	"to": 1, 
	"from": 2, 
	"amount": 100.00
}'

```
