# Bank web service

#### How to run

docker-compose up -d

#### Login as user
```javascript
/login
{
  "id": 1,
  "first_name": "Jonas",
  "password": "1234"
}
```

#### Register as user
```javascript
/register
{
"first_name": "Ramunas",
"last_name": "Ramusis",
"password": "passwordas"
}
```

#### Login as admin
```javascript
/login
{
  "first_name": "admin",
  "password": "password"
}
```

#### Register as admin
```javascript
/register
{
  "first_name": "admin",
  "password": "password"
}
```

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
