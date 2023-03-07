# Spring Boot JWT Security example

This is Spring Boot Application for JWT security. 

## Complete Authorization flow ##

- User can register himself
- User can authenticate himself, which gives access token. For any further action user has to send access token in Authorization header as Bearer token.
- Only Admin cal delete any user
- Only Moderator can modify user
- Only Admin can access all users
- User can access its details by passing user id

![accesstokenflow](/springboot-jwt-example/assets/fig9-accesstokenflow.png)

Application support following rest endpoints.

Roles available: ADMIN/MODERATOR/USER

1. Register user: http:/localhost:8080/api/v1/auth/signup

![signup](/springboot-jwt-example/assets/fig1-signup.png)

![signuperesponse](/springboot-jwt-example/assets/fig1-signupresponse.png)

2. Authenticate user: http:/localhost:8080/api/v1/auth/signin

![authenticate](/springboot-jwt-example/assets/fig2-authenticate.png)
![authenticate](/springboot-jwt-example/assets/fig2-authenticateresponse.png)

3. Get all users: ttp:/localhost:8080/api/v1/users/all

![allusers](/springboot-jwt-example/assets/fig3-allusers.png)
![allusers](/springboot-jwt-example/assets/fig3-allusersresp.png)

4. Get user by id: http:/localhost:8080/api/v1/users/{id}

![accessuser](/springboot-jwt-example/assets/fig4-accessuser.png)

5. Delete user: http:/localhost:8080/api/v1/users/1

![deleteuser](/springboot-jwt-example/assets/fig12-deleteuser.png)

6. Update user: http:/localhost:8080/api/v1/users/mod/3

![updateuser](/springboot-jwt-example/assets/fig11-updateuser.png)
![updateuser](/springboot-jwt-example/assets/fig11-updateuserresp.png)
 
7. Token expired : http:/localhost:8080/api/v1/users/all

![tokenexpired](/springboot-jwt-example/assets/fig6-tokenexpired.png)
