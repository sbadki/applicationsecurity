---
layout: post
title:  "SQL Injection"
date:   2023-03-15 02:41:00 +0200
categories: Application security blogs
---

SQL is a structural query language, which is used to preform CRUD(fetch, delete, modify, insert) operation on the database.

SQLi is an injection technique which allows attacker to inject malicious code via sql query input from the client to the application. This is possible when 
unfiltered data is passed from the user input fields to the SQL interpreter without sanitization.
With SQLi attacker can update, delete, modify or insert actions on the database. If he is able to elevate his permission to administrator account then he can be in 
complete control of an application.Which is a serious threat to the application.

	Example: SELECT * FROM USERS WHERE USERNAME = '" +emp_name + "' AND PASSWORD = '" + password+ "'

Attacker can pass the USERNAME as administrator'-- in the input field, considering administrator exists in USERS table,then this query would be treated as 
select * from users where username='administrator'-- by the SQL interpreter and as its ends with -- which is comment in oracle db so,remaining query would be ignored.


## What are SQLi Types? ##


There are various types of SQL injection.

### In-Band Injection ###

Attacker inject the query and see the results on the page. This type of injection are easier to exploit as the results are shown on to the page.

#### Error based Injection ####

Attacker injects a query to database which produces erroneous result. Attacker then uses that erroneous data to gather information about the structure of the database.

#### Union based Injection ####


It produces result by combining two or more SELECT statement with UNION operator and retrieving data from other tables within database.
- In this type of injection the no. of column requested in the query should match in both the select statement.
- Datatype of 1st columns in first select statement should match with datatype of 1st column in second select statement and so on.


### Blind Injection ###


In this type of attack, it asks the database true or false questions and determines the answers based on the response.No data is shown to the attacker whether the attack is 
success or failure or any type of application error. Techniques used to perform such type of attacks are little difficult.

#### Boolean Based Injection ####

Attacker prepare payload and injects into the query which results into true or false without showing query result to the user. Attacker infer the result based on the boolean 
condition passed into the query.

#### Time Based Injection ####

This type of injection forces database to wait for a specified amount of time before showing the result based of the condition.


### Out-of-Band Injection ###


This type of injection is very powerful and difficult to perform. Attacker uses this technique only when he can't use the same channel to launch and gather information.
This technique would solely rely on to the database server's ability to make HTTP or DNS request to deliver data to an attacker. It uses out-of-bound channel to perform 
the attack.
	

### Second order SQL injection ###


In this type of attack the malicious payload is injected into the application and stored into the database and at later point retrieved, rendered and executed. At first 
when the malicious payload is stored into the database the attack never happens but when that stored payload is accessed and used in query that time the attack happens.


## What are its impact? ##


1. Attacker can by-pass authentication logic of the web application.
2. Can retrieve sensitive information like social security number, credit card information and passwords.
3. Can access or modify file on to the server directly.
4. Can place backdoor on to the server and then can take over complete application.
5. Can issue commands to the operating system.


## What are the exploits? ##


Here will take an examples from WebGoat to show some exploits.

**In-band injection:**
Retrieve all the data from the users table.

    Query: "SELECT * FROM user_data WHERE LOGIN_COUNT = "' +login_count+'" AND user_iud = "+ user_id+"

1. Check which field is susceptible to sql injection. Try to put ' in the Login count and user_id input field and observer the output. Second field is injectable.  
2. Put 0 in login count and "0 or 1=1--" in the userid field 1=1 will always give true result and -- will comment remaining part of the query.
3. We are able to retrieve all the user data.


![errorexploit]({{ "/assets/images/sqlinjection/fig1-errorexploit.png" | relative_url }})

Result:
![errorresult]({{ "/assets/images/sqlinjection/fig2-errorresult.png" | relative_url }})


**Union based injection:** 
Retrieve all the data from user_system_data table.

As shown in below figure, we have two tables named user_data and user_system_data, we have to get all user details from the user_system_data 
table.Userid is the common fields between both the table. Here will combine two selects statements with UNION and produce one result.

    Query: "SELECT * FROM user_data WHERE last_name= "'+last_name''

1. In this example we already know how many columns are there in the table, but if we don't know the table then we start with first identifying the no of columns 
   in the table. This is achieved by trying multiple inputs like ' ORDER BY 1--' OR ' ORDER BY 2--' until you get false result.
2. Identify the datatype of the columns by like ' UNION SELECT NULL, NULL , NULL --' if data type is String OR ' UNION SELECT 1,2,3 --' if datatype is number

Inject: ' UNION SELECT userid as id,user_name,password,cookie,null,null,null from user_system_data;-- 

Result:

![unionexploit]({{ "/assets/images/sqlinjection/fig3-unionexploit.png" | relative_url }})

**Boolean based injection:** 
Retrieve all the users from the users table.
        
    Query: SELECT * FROM user_data WHERE first_name='"+fist_name+"' and last_name = "'+ last_name+'";

As shown in the figure, we try to inject last_name field with the input as 'Smith or 1=1' which results always to true and resulted into showing all the users.

![booleanexploit]({{ "/assets/images/sqlinjection/fig4-booleanexploit.png" | relative_url }})


**Time bases injection:** 
Retrieve password of the administrator user.Here used example from PortSwigger to explain how to exploit time based injection.

Injected TrackingId('xyz') parameter in Cookie to find out the password for administrator user.

1. Check after injecting the field page resul shows after 10 seconds or immediately. And it does.
xyz';select case when(1=1) then pg_sleep(10) else pg_sleep(0) end--

2. To check whether user administrator exists
xyz';select case when(username='administrator') then pg_sleep(10) end from users--

3. To check the length of the password, try until you find the exact length of the password. After tyring this step for multiple values, it looks password is of 20 characters.
xyz';select case when(username='administrator' and length(password) > 19) then pg_sleep(10) else pg_sleep(0) end from users--

4. To check whether the first char of the password is 'a' or not?
xyz';select case when(username='administrator' and substring(password,1,1)='a') then pg_sleep(10) else pg_sleep(0) end from users--

5. Likewise, we need to try for all the combinations from first char to 20th char until we identify the administrator password. We can use BurpSuit's advance technique 
to identify password using different payload.


## What are mitigation of SQLi? ##


1. Do not concat use user input directly into the queries, instead use Parameterized queries. Parameterized queries are compiled once and can be used with multiple inputs.
Make sure to use the prepared statement correctly, for setting parameters use ps.set..().

2. Validate user input, though it's not the full proof solution, but it definitely prevents user to put anything into the input field. This should be combined with other 
mitigation provided here.

3. Make use of Stored procedures. Only if it does not generate dynamic query.

4. Make sure to have proper management of user privileges. User should not have more privileges than what he is entitled to.


## References: ##


https://portswigger.net/web-security/jwt

https://github.com/WebGoat/WebGoat/releases












