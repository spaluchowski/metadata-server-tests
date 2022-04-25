# Overview

Code for API testing of the https://github.com/piotr-iohk/metadata-server-mock service

# Defects

Defects located

## Bug 1

Information disclosure
Server information leakage

Header:
Server: WEBrick/1.4.2 (Ruby/2.6.6/2020-03-31)

## Bug 2

Sending:
Content-Type=text/plain; charset=ISO-8859-1

Any CT is parsed as JSON?

## Bug 3

### Summary

Empty Json root element in body results in Internal Server Error

### App Version

x

### Severity

High

### Description

#### Steps

Send request with empty root json element

```
Request method:	POST
Request URI:	http://metadata-server-mock.herokuapp.com/metadata/query
Proxy:			<none>
Request params:	<none>
Query params:	<none>
Form params:	<none>
Path params:	<none>
Headers:		Accept=*/*
Content-Type=application/json
Cookies:		<none>
Multiparts:		<none>
Body:
{

}
```

#### Actual outcome

```
HTTP/1.1 500 Internal Server Error
Connection: keep-alive
Content-Type: text/html;charset=utf-8
Content-Length: 30
X-Xss-Protection: 1; mode=block
X-Content-Type-Options: nosniff
X-Frame-Options: SAMEORIGIN
Server: WEBrick/1.4.2 (Ruby/2.6.6/2020-03-31)
Date: Sat, 23 Apr 2022 10:19:02 GMT
Via: 1.1 vegur

<html>
  <body>
    <h1>Internal Server Error</h1>
  </body>
</html>
```

#### Expected outcome

400 Bad request response should be sent

## Bug 4

### Summary

No body element in request results in Internal Server Error

App Version: x
Severity: High

### Description

#### Steps

Send request with empty root json element

```
Request method:	POST
Request URI:	http://metadata-server-mock.herokuapp.com/metadata/query
Proxy:			<none>
Request params:	<none>
Query params:	<none>
Form params:	<none>
Path params:	<none>
Headers:		Accept=*/*
Content-Type=application/json
Cookies:		<none>
Multiparts:		<none>
Body:
{

}
```

#### Actual outcome

```
HTTP/1.1 500 Internal Server Error
Connection: keep-alive
Content-Type: text/html;charset=utf-8
Content-Length: 30
X-Xss-Protection: 1; mode=block
X-Content-Type-Options: nosniff
X-Frame-Options: SAMEORIGIN
Server: WEBrick/1.4.2 (Ruby/2.6.6/2020-03-31)
Date: Sat, 23 Apr 2022 10:19:02 GMT
Via: 1.1 vegur

<html>
  <body>
    <h1>Internal Server Error</h1>
  </body>
</html>
```

#### Expected outcome

400 Bad request response should be sent

## Bug 5

Request
```
Request method:	GET
Request URI:	http://metadata-server-mock.herokuapp.com/metadata/789ef8ae89617f34c07f7f6a12e4d65146f958c0bc15a97b4ff169f1
Proxy:			<none>
Request params:	<none>
Query params:	<none>
Form params:	<none>
Path params:	property=789ef8ae89617f34c07f7f6a12e4d65146f958c0bc15a97b4ff169f1
Headers:		Accept=*/*
Cookies:		<none>
Multiparts:		<none>
Body:			<none>
```
Actual response
```
HTTP/1.1 200 OK
Connection: keep-alive
X-Content-Type-Options: nosniff
Server: WEBrick/1.4.2 (Ruby/2.6.6/2020-03-31)
Date: Sat, 23 Apr 2022 11:25:14 GMT
Via: 1.1 vegur
Content-Type: application/json
Content-Length: 86

Requested subject '789ef8ae89617f34c07f7f6a12e4d65146f958c0bc15a97b4ff169f1' not found
```
#### Expected
To be discussed:

- Content Type: application/json - Where the body is text/plain
- Status code 200 - Where the item is not found

### Bug 6

X-Xss-Protection: 1; mode=block
https://stackoverflow.com/questions/9090577/what-is-the-http-header-x-xss-protection#:~:text=X-XSS-Protection%3A%201%3B%20mode%3Dblock%20allows%20attacker%20to%20leak%20data,the%20behavior%20of%20the%20page%20as%20a%20side-channel.

### Bug

Subjects undefined - Internal server error

### Bug
WEBrick/1.4.2  - Disputed path traversal - 
https://www.cvedetails.com/cve/CVE-2019-11879/

### Bug

decimals, policy - te property sa w subject 919e8a1922aaa764b1d66407c6f62244e77081215f385b60a62091494861707079436f696e ale nie sa w known properties!

### Bug

Requesting policy property gets invalid json in response

```
Request method:	GET
Request URI:	http://metadata-server-mock.herokuapp.com/metadata/919e8a1922aaa764b1d66407c6f62244e77081215f385b60a62091494861707079436f696e/properties/policy
Proxy:			<none>
Request params:	<none>
Query params:	<none>
Form params:	<none>
Path params:	subject=919e8a1922aaa764b1d66407c6f62244e77081215f385b60a62091494861707079436f696e
property=policy
Headers:		Accept=*/*
Cookies:		<none>
Multiparts:		<none>
Body:			<none>
```
```
HTTP/1.1 200 OK
Connection: keep-alive
Content-Type: application/json
Content-Length: 76
X-Content-Type-Options: nosniff
Server: WEBrick/1.4.2 (Ruby/2.6.6/2020-03-31)
Date: Sat, 23 Apr 2022 23:21:50 GMT
Via: 1.1 vegur

"82008201818200581c69303ce3536df260efddbc949ccb94e6993302b10b778d8b4d98bfb5"
```

### Bug

Needs to be double-checked with BA 

Known properties vs which one I can request

### Bug

Unknown property error in response body is not a JSON 

```
curl 'http://metadata-server-mock.herokuapp.com/metadata/919e8a1922aaa764b1d66407c6f62244e77081215f385b60a62091494861707079436f696e/properties/unit' --header 'Accept: */*' --header 'Host: metadata-server-mock.herokuapp.com' --header 'Connection: Keep-Alive' --header 'User-Agent: Apache-HttpClient/4.5.13 (Java/17.0.2)' --compressed --insecure --verbose
```
```
Request method:	GET
Request URI:	http://metadata-server-mock.herokuapp.com/metadata/919e8a1922aaa764b1d66407c6f62244e77081215f385b60a62091494861707079436f696e/properties/unit
Proxy:			<none>
Request params:	<none>
Query params:	<none>
Form params:	<none>
Path params:	subject=919e8a1922aaa764b1d66407c6f62244e77081215f385b60a62091494861707079436f696e
property=unit
Headers:		Accept=*/*
Cookies:		<none>
Multiparts:		<none>
Body:			<none>
```

```
HTTP/1.1 200 OK
Connection: keep-alive
Content-Type: application/json
Content-Length: 35
X-Content-Type-Options: nosniff
Server: WEBrick/1.4.2 (Ruby/2.6.6/2020-03-31)
Date: Mon, 25 Apr 2022 20:08:05 GMT
Via: 1.1 vegur

Requested property 'unit' not found
```