# Bank web service

## Instalation
Download docker-compose.yml and run: docker-compose up -d

docker pull ricardaskazakevicius/superbank:v11

docker run -d -p 50:1234 ricardaskazakevicius/superbank:v11

or

docker build -t superbank:v1 .

docker run -d -p 50:1234 superbank:v1

## Endpoints and methods
/accounts  GET,POST

/accounts/1  GET, PUT, DELETE

/accounts/1/sent  GET

/accounts/1/received  GET

/transactions  GET, POST

/transactions/1  GET, PUT, DELETE

## Data

#### Account
```javascript
{
  "id": 1,
  "name": "Jonas",
  "surname": "Jonaitis",
  "balance": 200.21
}
```

#### Transaction
```javascript
{
"senderId": 2,
"receiverId": 1,
"amount": 100
"bankName": "bankr"
}
```
