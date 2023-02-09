---
layout: post
title: Rate Limiting API endpoints with Bucket4j
date:   2023-01-31 01:23:45 +0800
description: Token-Buket rate limiting algorithm Implementation
---

## Rate Limiting 

**Rate limiting** is a strategy for limiting network traffic. It restricts client for making number of API calls within 
a certain time frame. This is required to prevent Brute force, DoS or DDos, web scraping attacks.

Rate limits can be applied based on tracking IP address, API keys or access tokens to an API. If we apply rate limit
based on IP Addresses, A rate limiting solution measures the amount of time between each request from each IP address,
and also measures the number of requests within a specified timeframe. If there are too many requests from a single IP
within the given timeframe, the rate limiting solution will not fulfil the IP address's requests for a certain amount
of time. 

So what happens to the requests if it reaches the limit.

- They may be dropped/rejected for sending Too many requests - HTTP 429.
- They may be queued until the remaining time period is elapsed.
- They may be transmitted by charging for an extra requests. 

## Token bucket Algorithm

Consider there is a bucket which can hold n number of tokens, whose capacity is predefined. So, whenever any client 
request an API endpoint he needs to get a token from the bucket so that the request is successful. What if there is no 
token available in the bucket then the client's request will be rejected to progress further.

While making this request, the tokens are being consumed by it. Simultaneously the bucket is re-filling the tokens at 
fixed rate making sure the capacity of the bucket won't exceed at any given point of time.

Before implementing token-bucket algorithm, will understand some terminologies used in algorithm.

## Terminologies User in Algorithm: 

**Bucket** - consider it is a container

**Limitations that are used by bucket can be denoted in terms of bandwidths. Bandwidth is denoted by the following terms:**

**Capacity** - specifies how many tokens your bucket has.

**Refill** - specifies how fast tokens can be refilled after it was consumed from a bucket.

	- Greedy - This type of refill greedily regenerates tokens manner, it tries to add the tokens to the bucket as soon as possible.
    Refill.greedy(10, Duration.ofSeconds(1));
	
	- Interval - This type of refill regenerates tokens in an interval manner. "Interval" in opposite to "greedy" will wait until the whole period will be elapsed before regenerating the whole amount of tokens.
    Refill.intervally(100, Duration.ofMinutes(1));
	
	- IntervallyAligned - In addition to Interval it is possible to specify the time when the first refill should happen. This type can be used to configure clear interval boundary i.e. start of the second, minute, hour, day. 
	
    InstantfirstRefillTime=ZonedDateTime.now()
			.truncatedTo(ChronoUnit.HOURS)
			.plus(1,ChronoUnit.HOURS)
			.toInstant();
		
	Bandwidth.classic(400,Refill.intervallyAligned(400,Duration.ofHours(1),firstRefillTime,true));
	
**Initial tokens** - Bucket4j extends the token-bucket algorithm by allowing to specify the initial amount of tokens for 
each bandwidth. By default, an initial amount of tokens equals to capacity and can be changed by withInitialTokens method. 

## Example:

It’s a simple Spring boot application which has UserController with three endpoints createUser, getUser and get all users. 
Rate limit is applied to all three endpoints and will see it in action.  The code can be referred from the 
Github repo: https://github.com/sbadki/appsec/tree/main

We are using Bucket4J, it's a java library to implement the rate limiting which is based of token-bucket algorithm.
Provide following dependency with other required dependencies for spring boot rest API.

Let's take an example, if rate limit is set to 10 requests per minute for an API. That means the capacity of the bucket
can set to 10, and we can refill it with 10 tokens per minute.

If we receive 8 request for first minute, which is less than the bucket capacity for a minute. Then the remaining 2
tokens will be carry forward to next minute in addition to 8 new tokens to full-fill the capacity of bucket to 10 tokens.

If we receive 10 requests in first 45 seconds itself, then we would need to wait another 15 seconds to serve the requests.
 

![dependency.JPG](https://sbadki.github.io/appsec/assets/images/ratelimit/dependency.JPG)


The Bucket is built with rate limit of 10 requests per minute, So the API will reject the requests if its already received 
10 requests in a time frame of 1 minute.

![bucket_greedy.JPG](https://sbadki.github.io/appsec/assets/images/ratelimit/bucket_greedy.JPG)


But what if all the 10 requests came in first 5 seconds which consumed all the tokens leads to spike in the application
suddenly.We can control this limit by applying multiple limits to the bucket. The bucket would serve 10 requests per 
minute but serve 5 requests in 20 seconds time window.

![bucket_inter.JPG](https://sbadki.github.io/appsec/assets/images/ratelimit/bucket_inter.JPG)


## Implementation:

tryConsume will Tries to consume specified number of tokens from the bucket during the specified timeframe.
When it exceeds rate limit then it will reject the request with status code as HTTP - 429 TooManyRequests. 

![api_ratelimits.JPG](https://sbadki.github.io/appsec/assets/images/ratelimit/api_ratelimits.JPG)


## Result:

All the requests after 5 seconds got rejected.


![result.JPG](https://sbadki.github.io/appsec/assets/images/ratelimit/result.JPG)

