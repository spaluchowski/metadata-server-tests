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
