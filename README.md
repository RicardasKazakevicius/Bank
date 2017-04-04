# Bank web service

## Instalation

docker build -t superbank:v1 .
docker run -d -p 80:1234 superbank:v1

## Endpoints and methods
/accounts  GET,POST
/accounts/1  GET, UPDATE, PUT, DELETE

/transactions  GET, POST
/transactions/1  GET, UPDATE, PUT, DELETE

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
