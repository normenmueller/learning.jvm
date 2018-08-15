# OAUTH2 Microservice

### /api/oauth2/register

This method is used to register a new application/service with the OAUTH server.

* **Method:** POST
* **Request-Payload:**
  
  ```
    {
        type: "pull",
        client_name: "test-app",
        client_url: "localhost:9000",
        client_description: "my test application",
        redirect_url: "localhost:8080/myService/redirectUrlAfterLogin"
    }
  ```
  
* **Response:**

  ```
    {
        client_id: "123456789",
        client_secret: "1a2b3c4d5e",
        issued_at: "UNIX_TIMESTAMP",
        expires_in: "UNIX_TIMESTAMP"
    }
  ```
  
### /api/oauth2/auth

This method is used to get authorisation for the user. Users provide their `client_id` and the server will verify if they're allowed to access this client (i.e. through a server-to-server request to the authentication microservice). Upon successful authorisation, they will be redirected to the `redirect_url`.

* **Method:** GET
* **Query-Parameters:**

  ```
  client_id: 123456789
  redirect_uri: http://localhost:9000/endpoint
  response_type: code
  ```
* **Response:**

  ```
  http://localhost:9000/endpoint?code=123456789
  ```
  
### /api/oauth2/token

The authorisation code afterwards needs to get exchanged for an access token. The token will be used to access protected api calls.

* **Method:** POST
* **Request-Payload:**

  
  ```
    {
        grant_type: "authorization_code",
        client_id: "123456789",
        redirect_uri: "http://localhost:9000/endpoint",
        client_secret: "1a2b3c4d5e",
        code: "123456789"
    }
  ```
  
* **Response:**

  ```
    {
        "token_type": "Bearer",
        "access_token": "1a2b3c4d5f6g7h",
        "expires_in": "UNIX_TIMESTAP",
        "user_id": "12345"
    }
  ```
  
### /api/oauth2/refresh_token

Used to change the current token for a new one. This has to be done after the token expired.

* **Method:** GET
* **Query-Parameters:**

  ```
  access_token: 1a2b3c4d5f6g7h
  ```

* **Response:**

  ```
    {
        "token_type": "Bearer",
        "access_token": "1a2b3c4d5f6g7h",
        "expires_in": "UNIX_TIMESTAP",
        "user_id": "12345"
    }
  ```  

### /api/oauth2/verify\_token\_validity

Verifies if a token is valid and not expired.

* **Method:** GET
* **Query-Parameters:**

  ```
  access_token: 1a2b3c4d5f6g7h
  ```
* **Response:**
  
   ```
    {
        valid: "true"
    }
  ```
  
### /api/oauth2/query\_endpoint

Queries whether or not the user may access the provided endpoint.

* **Method:** GET
* **Query-Parameters:**

  ```
  access_token: 1a2b3c4d5f6g7h
  endpoint: /api/service1/get_alarm_data
  ```
* **Response:**
  
   ```
    {
        granted: true
    }
  ```