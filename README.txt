Video here
https://www.youtube.com/watch?v=KxqlJblhzfI

DOESN'T INCLUDE THE SECRET KEY

JWT Authentication/Authorization With Spring Boot 3/Spring Security 6
Postgres database to store credentials
JWT (JSON web tokens) are useful to secure applications and protect endpoints
Features a simple API and uses Postman to send authenticated requests

A JSON Web Token (JWT) represents claims being transferred between two parties,
they are encoded as JSON objects andare digitally signed using a JSON web signature
Consists of 3 parts:
- Header
    Contains a "signing algorithm" and a type (JWT)
- Payload
    Contains the claims, statements about an entity (e.g. user) and additional data
- Signature
    Verifies the sender of the token is who they claim to be
    Also ensures the claims haven't been altered

Spring Security Architecture breakdown
- Starts with the user's HTTP request (frontend to backend through API)
- First thing to be executed is the filter (once per request)
    Validates the JWT token, which are signed to ensure security
- The filter checks if there is a JWT token,
   return 403 response if not (refused authorization)
- Validation process begins after filtering
- The filter makes a call to the UserDetailsService to fetch user info from the DB
    Filter extracts the "subject" (username/email) from token for UDS
    User may or may not exist, if they don't returns a 403 response
- If the user exists in our database we can begin the process of validating the token
    Token is generated for a particular user, so we validate based on that user
- User validation calls a JWT service that takes the user and token as a parameter
    Two scenarios: Invalid token (expired or for the wrong user) -> Send a 403
        If the token is valid -> Update the SecurityContextHolder and set to user
         this tells the rest of the filter chain that the user is authenticated
- After updating the SCH, send the request to the dispatcher servlet
    then forwarded to the controller to handle execution of the request
- After handling the request returns a 200 (or 2XX) response with the requested data