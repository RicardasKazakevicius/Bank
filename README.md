# Bank web service

## Instalation
docker pull ricardaskazakevicius/superbank:v10

docker run -d -p 50:1234 ricardaskazakevicius/superbank:v10

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
"id": 1,
"senderId": 3,
"receiverId": 2,
"amount": 19.50
}
```
