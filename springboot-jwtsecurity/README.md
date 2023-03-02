# Spring Boot JWT Security example

This is Spring Boot Application for JWT security. 

## Complete Authorization flow ##

- User can register himself
- User can authenticate himself, which gives access token and refresh token. For any further action user has to send access token in Authorization header as Bearer token.
- Only Admin cal delete any user
- Only Moderator can modify user
- Only Admin can access all users
- User can access its details by passing user id

![accesstokenflow](/assets/fig9-accesstokenflow.png)

- If the access token is expired, user can request for refresh token.
- Once user get refresh token user can continue accessing other resources bases on roles he is assigned to. 

## Refresh token flow ##

![refreshtokenflow](/assets/fig10-refreshtokenflow.png)


Application support following rest endpoints.

Roles available: ADMIN/MODERATOR/USER

1. Register user: http://localhost:8080/api/v1/auth/signup

![signup](/assets/fig1-signup.png)

2. Authenticate user: http://localhost:8080/api/v1/auth/signin

![authenticate](/assets/fig2-authenticate.png)

3. Get all users: ttp://localhost:8080/api/v1/users/all

![allusers](/assets/fig3-allusers.png)

4. Get user by id: http://localhost:8080/api/v1/users/{id}

![accessuser](/assets/fig4-accessuser.png)

5. Delete user: http://localhost:8080/api/v1/users/1

![deleteuser](/assets/fig12-deleteuser.png)

6. Update user: http://localhost:8080/api/v1/users/mod/3

![updateuser](/assets/fig11-updateuser.png)
 
7. Token expired : http://localhost:8080/api/v1/users/all

![tokenexpired](/assets/fig6-tokenexpired.png)

8. Refresh token:http://localhost:8080/api/v1/auth/refreshtoken

![refreshtoken](/assets/fig7-refreshtoken.png)