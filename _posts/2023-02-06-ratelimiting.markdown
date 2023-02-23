---
layout: post
title: Rate Limiting API endpoints with Bucket4j
date:   2023-01-31 01:23:45 +0800
description: Token-Buket rate limiting algorithm Implementation
---

## What is Rate Limiting ?


**Rate limiting** is a strategy for limiting network traffic. It restricts client for making number of API calls within a certain time frame. This is required to prevent Brute force, DoS or DDos, web scraping attacks.

Rate limits can be applied based on tracking IP address, API keys or access tokens to an API. If we apply rate limit based on IP Addresses, A rate limiting solution measures the amount of time between each request from each IP address,
and also measures the number of requests within a specified timeframe. If there are too many requests from a single IP within the given timeframe, the rate limiting solution will not fulfil the IP address's requests for a certain amount
of time.

So what happens to the requests if it reaches the limit.

- They may be dropped/rejected for sending Too many requests - HTTP 429.
- They may be queued until the remaining time period is elapsed.
- They may be transmitted by charging for an extra requests.


## Why do we need API rate Limiting?


We need API rate limiting to address few of the problems listed below. Although it's not the comprehensive list.

- It can prevent from Denial of service attack, which improves users experience.
- It can prevent from resource starvation which improve the availability of the API based services.
- It can prevent malicious user from brute forcing logging APIs.
- It helps in scaling APIs during the unexpected spikes of traffic by controlling the flow of data.


## Types of Rate limiter


1. User rate limits

   It is the most common and popular method of rate limiting. In this, a limit is applied on the no of requests a user can make in a given period of time. If any request's comes from the users after the specified time elapsed would simply reject's the request until the rate-limiting timeframe resets.

2. Geographic rate limits

   The rate limit is specified based on the geographic location for a specified timeframe.For e.g. if we know that the users from the specific regions are not active during certain timeframe [12am:6am] then we can lower the rate limit for this timeframe. This way we can prevent suspicious traffic and further reduces the risk of an attack.

3. Server rate limits

   This approach provides more flexibility, as to increase the rate limit on mostly used servers while decreasing the traffic limit on less active servers.


## What are the Rate limiting Algorithms?


There are several types of rate-limiting algorithms.

### Token Bucket

Consider there is a bucket which can hold n number of tokens, whose capacity is predefined. So, whenever any client request an API endpoint he needs to get a token from the bucket so that the request is successful.
While making this request, the tokens are being consumed by it. Simultaneously the bucket is re-filling the tokens at fixed rate making sure the capacity of the bucket won’t exceed at any given point.
If there is no token left in the bucket, then the client’s request will not be process.

### Leaky Bucket

It usea a fixed size bucket with a hole at the bottom. Requests are pouring into the bucket at different rate but it's removed from the bucket at constant rate. So, whenever the bucket is full with requests, all extra requestes will be discarded.
It helps in smoothing outbursts of traffic as requests are served at constant rate.

### Fixed-window

Its restrict the number of requests allowed during a given timeframe.
For e.g. an API accepts up to 10 requests/minute. So any extra requests coming during that time frame will be discarded until the window reset at the next minute.
e.g. Timeframe : 10:00:00 - 10:01:00 --> max 10 requests would be accepted. Any extra request would be discarded.
10:01:00 - 10:02:00 --> max 10 requests would be accepted. Any extra request would be discarded.

### Sliding-window
It's moreover similar to fixed window algorithm except for the starting point of each time window. The timeframe starts when the new request arrives, not at a predetermined time.
For e.g. an API accepts up to 10 requests/minute.If first request arrives at 10:00:01, API would accept max 10 request until 10:01:01.

### Sliding Log
It maintains time-stamped log of each request for a user. The system stores these logs in a time-sorted hash set or table. Logs with timestamps that exceed the rate limit are discarded. When a new request comes in, the sum of the logs are calculated to determine the request rate. If the request exceeds the limit threshold, they are simply queued.


## Token bucket Algorithm Implementation

### Terminologies User in Algorithm:

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

### Example:

It’s a simple Spring boot application which has UserController with three endpoints createUser, getUser and get all users.
Rate limit is applied to all three endpoints and will see it in action.  
We are using Bucket4J, it's a java library to implement the rate limiting which is based of token-bucket algorithm.
Provide following dependency with other required dependencies for spring boot rest API.

Let's take an example, if rate limit is set to 10 requests per minute for an API. That means the capacity of the bucket
can set to 10, and we can refill it with 10 tokens per minute.

If we receive 8 request for first minute, which is less than the bucket capacity for a minute. Then the remaining 2
tokens will be carry forward to next minute in addition to 8 new tokens to full-fill the capacity of bucket to 10 tokens.

If we receive 10 requests in first 45 seconds itself, then we would need to wait another 15 seconds to serve the requests.


![Dependency]({{ "/assets/images/ratelimit/dependency.png" | relative_url }})


The Bucket is built with rate limit of 10 requests per minute, So the API will reject the requests if its already received
10 requests in a time frame of 1 minute.

![Bucket_greedy]({{ "/assets/images/ratelimit/bucket_greedy.png" | relative_url }})


But what if all the 10 requests came in first 5 seconds which consumed all the tokens leads to spike in the application
suddenly.We can control this limit by applying multiple limits to the bucket. The bucket would serve 10 requests per
minute but serve 5 requests in 20 seconds time window.

![Bucket_inter]({{ "/assets/images/ratelimit/bucket_inter.png" | relative_url }})


## Implementation:


tryConsume will Tries to consume specified number of tokens from the bucket during the specified timeframe.
When it exceeds rate limit then it will reject the request with status code as HTTP - 429 TooManyRequests.

![Api_ratelimits]({{ "/assets/images/ratelimit/api_ratelimits.png" | relative_url }})


## Result:

All the requests after 5 seconds got rejected.


![Result]({{ "/assets/images/ratelimit/result.png" | relative_url }})

Github repo: https://github.com/sbadki/applicationsecurity/tree/main

---