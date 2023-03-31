---
layout: post
title:  "HTTP Security Headers"
date:   2023-03-15 02:41:00 +0200
categories: Application security blogs
---


Here are some list of HTTP security headers which we need to consider while building a web application. 


![table]({{ "/assets/images/securityheaders/fig2-result.png" | relative_url }})


## X-Frame-Options ##

The X-Frame-Options HTTP response header can be used to indicate whether a browser should be allowed to render a 
page in a <frame>, <iframe>, <embed> or <object>.


**Syntax:**

    X-Frame-Options: DENY       - No rendering within a frame
    X-Frame-Options: SAMEORIGIN - No rendering if origin mismatch


**By-Pass:**

To bypass X-Frame-Options, attackers may try to use a framing technique known as "UI redress" or "clickjacking," where 
they trick the user into clicking on a button or link that is actually on a different page.


**Prevention :**

Prevents from Click-jacking attacks, as the header ensuring that their content is not embedded into other sites.



## X-XSS-Protection ##

[Deprecated]

The HTTP X-XSS-Protection response header is a feature of Internet Explorer, Chrome and Safari that stops pages from 
loading when they detect reflected cross-site scripting (XSS) attacks. Instead use Content-Security-Policy that disables
the use of inline JavaScript ('unsafe-inline').


**Syntax:**

    X-XSS-Protection: 0     - Filter disabled
    

**By-Pass:**

To bypass X-XSS-Protection, attackers may try to use obfuscation techniques or encode their payloads to avoid detection.


**Prevention :**

Prevents from Cross Site Scripting (XSS) attacks.


## Content-Type ##

Is used to indicate the original media type of the resource (prior to any content encoding applied for sending). If not
set correctly, the resource (e.g. an image) may be interpreted as HTML.


**Syntax:**

    Content-Type: text/html; charset=utf-8
    Content-Type: multipart/form-data; boundary=something


**By-Pass:**

This header's value may be ignored, for example when browsers perform MIME sniffing;


**Prevention :**

Set the X-Content-Type-Options header value to nosniff to prevent from MIME sniffing attacks.

## X-Content-Type-Options ##

Setting this header will prevent the browser from interpreting files as a different MIME type to what is specified in
the Content-Type HTTP header (e.g. treating text/plain as text/css).


**Syntax:**

    X-Content-Type-Options: nosniff     - prevents browser from MIME-sniffing a response away from the declared content-type.


**By-Pass:**

To bypass X-Content-Type-Options, attackers may try to use file extensions or MIME types that are not explicitly blocked
by the header.


**Prevention :**

Set the X-Content-Type-Options header value to nosniff to prevent this behavior.


## Strict-Transport-Security (HSTS) ##

The HTTP Strict-Transport-Security response header (often abbreviated as HSTS) informs browsers that the site should only
be accessed using HTTPS, and that any future attempts to access it using HTTP should automatically be converted to HTTPS.


**Syntax:**

    Strict-Transport-Security: max-age=31536000; includeSubDomains; preload

max-age : The time, in seconds, that the browser should remember that this site is only to be accessed using HTTPS.
includeSubDomains : If this optional parameter is specified, this rule applies to all the site’s subdomains as well.


**By-Pass:**

If a website accepts a connection through HTTP and redirects to HTTPS, visitors may initially communicate with the non-encrypted
version of the site before being redirected, for e.g. If the visitor enters http://www.foo.com/ or even just foo.com.
This creates an opportunity for a man-in-the-middle attack. The redirect could be exploited to direct visitors to a malicious
site instead of the secure version of the original site.


**Prevention :**

This header informs the browser that it should never load a site using HTTP and should automatically convert all attempts
to access the site using HTTP to HTTPS requests instead.

## Content Security Policy ##

The HTTP Content-Security-Policy response header allows web-site administrators to control resources the user agent is
allowed to load for a given page.


**Syntax:**

    Content-Security-Policy: default-src 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval'; style-src 'self' 
    Content-Security-Policy: …; report-uri https://endpoint.example.com; report-to groupname


**By-Pass:**

To bypass CSP, attackers may try to use data: URLs or inline styles/scripts, or try to exploit any flaws in your CSP policy.

**Prevention :**

It prevents from cross-site scripting (XSS) and other code injection attacks.


## Referrer-Policy ##


This header controls how much information the browser sends to the server when following a link.


**Syntax:**

    Referrer-Policy: no-referrer    - No referrer information is sent along the request.
    Referrer-Policy: origin         - Only send the origin of the document like http://example.com

**By-Pass:**

To bypass Referrer-Policy, attackers may try to use other attack vectors, such as CSRF or social engineering.


**Prevention :**


## Cache-Control ##


The Cache-Control HTTP header field holds directives (instructions) — in both requests and responses — that control caching
in browsers and shared caches (e.g. Proxies, CDNs).


**Syntax:**

    Cache-Control: no-store, max-age=0

no-store:   The response may not be stored in any cache.
max-age:    The maximum amount of time a resource is considered fresh.


**Prevention :**

Prevents from Web cache poisoning


## Access-Control-Allow-Origin ##

This response header indicates whether the response can be shared with requesting code from the given origin.

**Syntax:**

Access-Control-Allow-Origin: *          - It allows any origin to access the resource.
Access-Control-Allow-Origin: <origin>   - Only single origin can specify, so resources would be shared with specified origin only.
Access-Control-Allow-Origin: null       - It specified the null origin, it's not safe,so should be avoided.


## Permissions-Policy (formerly Feature-Policy) ##

This header provides a mechanism to allow and deny the use of browser features in a document or within any <iframe> 
elements in the document.

**Syntax:**
    
    Permissions-Policy: <directive> <allowlist>

## Set-cookie ## 

The Set-Cookie HTTP response header is used to send a cookie from the server to the user agent, so that the user agent
can send it back to the server later. To send multiple cookies, multiple Set-Cookie headers should be sent in the same response.


**Syntax:**

    Set-Cookie: <cookie-name>=<cookie-value>; Domain=<domain-value>; Secure; HttpOnly
    Set-Cookie: <cookie-name>=<cookie-value>; Path=<path-value>; Max-Age=<number>; SameSite=Lax

**Prevention :**

This tag would help in preventing from XSS, CSRF and accessing sensitive information if set properly.


## Clear-Site-Data ##

Remove locally stored data, after user signs out of the website or service. To do this, add the Clear-Site-Data header
to the page that confirms the logging out from the site has been accomplished successfully.

**Syntax:**
    
    Clear-Site-Data: "cache", "cookies", "storage", "executionContexts"


## Cross-Origin-Embedder-Policy ##

Configures embedding cross-origin resources into the document.

**Syntax:**

    Cross-Origin-Embedder-Policy: unsafe-none | require-corp | credentialless


## Cross-Origin-Opener-Policy ##

The HTTP Cross-Origin-Opener-Policy (COOP) response header allows you to ensure a top-level document does not share a
browsing context group with cross-origin documents.


**Syntax:**

    Cross-Origin-Opener-Policy: unsafe-none | same-origin-allow-popups | same-origin

## Cross-Origin-Resource-Policy ##

This header allows you to control the set of origins that are empowered to include a resource. CORP is an additional
layer of protection beyond the default same-origin policy. Cross-Origin Resource Policy complements Cross-Origin Read
Blocking (CORB), which is a mechanism to prevent some cross-origin reads by default.

**Syntax:**

    Cross-Origin-Resource-Policy: same-site | same-origin | cross-origin


![table]({{ "/assets/images/securityheaders/fig1-headers.png" | relative_url }})


## References ##

https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers

---