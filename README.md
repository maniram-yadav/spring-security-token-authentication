# spring-security-token-authentication Application


**Project Desctiption**

This project is built on Spring security framework. It uses the custom implementation of spring security filter for handling the Authentication and Authorization process of users login based on generated token by the server.

When User login for the first time and provided credential is valid then user will get a authorization token in response header. That taken will be used with each request for calling the implemented API. On each request provided token in header will be validated if token is valid and still has not been expired the desired reponse will be provided from server otherwise it will give invalid token or token expired resonse error message.



**Token Invalidation Process**

Following are the cases when user already generated token will be invalidated even though it have not been expired.

* If User changes the Password
* If users roles are changed
* If admin invalidate all the generated tokens for every user.



**NOTE** - If user try to login multiple time on one system or on different system then the token which has been generated earlier will only be valid and apart from that all other token will be invalidated.
