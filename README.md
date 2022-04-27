# Overview

Code for API testing of the https://github.com/piotr-iohk/metadata-server-mock service

# Defects

Defects located

All should contain:

- App version
- Severity

But shortened for readability

## Bug 1 - Server information disclosure

### Description

Server information leakage
Every response contains header:
Server: WEBrick/1.4.2 (Ruby/2.6.6/2020-03-31)

Needs review:
https://www.cvedetails.com/cve/CVE-2019-11879/
https://www.cvedetails.com/cve/CVE-2020-25613/

### Expected

- Do not disclose server information
- Upgrade

***

## Bug 2 - Wrong content type is parsed as JSON

### Description

Looks like content type header may be ignored.  
If request contains:

- "Content-Type=text/plain; charset=ISO-8859-1" in header
- valid JSON body

the document is parsed

### Steps

```
curl 'http://metadata-server-mock.herokuapp.com/metadata/query' --header 'Accept: */*' --header 'Content-Type: text/plain; charset=ISO-8859-1' --header 'Host: metadata-server-mock.herokuapp.com' --header 'Connection: Keep-Alive' --header 'User-Agent: Apache-HttpClient/4.5.13 (Java/17.0.2)' --data-binary '{"subjects":["919e8a1922aaa764b1d66407c6f62244e77081215f385b60a62091494861707079436f696e"]}' --compressed --insecure --verbose
```

### Actual outcome

```
HTTP/1.1 200 OK
Connection: keep-alive
Content-Type: application/json
Content-Length: 2878
X-Content-Type-Options: nosniff
Server: WEBrick/1.4.2 (Ruby/2.6.6/2020-03-31)
Date: Wed, 27 Apr 2022 15:14:12 GMT
Via: 1.1 vegur

{
"subjects": [
{
"subject": "919e8a1922aaa764b1d66407c6f62244e77081215f385b60a62091494861707079436f696e",
...
```

### Expected outcome

Response code 400

***

## Bug 3 - Empty Json root element in body results in Internal Server Error

### Description

Send request with empty root json element gives in response code 500
Same happens for invalid JSON

#### Steps

```
curl 'http://metadata-server-mock.herokuapp.com/metadata/query' --request POST --header 'Content-Type: application/json' --header 'Accept: */*' --header 'Content-Length: 0' --header 'Host: metadata-server-mock.herokuapp.com' --header 'Connection: Keep-Alive' --header 'User-Agent: Apache-HttpClient/4.5.13 (Java/17.0.2)' --compressed --insecure --verbose
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

***

## Bug 4 - [/metadata/query] No body element in request results in Internal Server Error

### Description

Send request with empty root json element gives response code 500

#### Steps

```
curl 'http://metadata-server-mock.herokuapp.com/metadata/query' --header 'Accept: */*' --header 'Content-Type: application/json' --header 'Host: metadata-server-mock.herokuapp.com' --header 'Connection: Keep-Alive' --header 'User-Agent: Apache-HttpClient/4.5.13 (Java/17.0.2)' --data-binary '{}' --compressed --insecure --verbose
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

***

## Bug 5 - [/metadata/{id}] Improper resource not found error response behaviour

### Description

Requesting not found subject gives inconsistent response:

- response code 200
- content-type: json
- plain text body

### Steps

```
 curl 'http://metadata-server-mock.herokuapp.com/metadata/nonExistingSubject' --header 'Accept: */*' --header 'Host: metadata-server-mock.herokuapp.com' --header 'Connection: Keep-Alive' --header 'User-Agent: Apache-HttpClient/4.5.13 (Java/17.0.2)' --compressed --insecure --verbose
```

### Actual response

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

Response code 404

***

## Bug 6 - X-Xss-Protection header should not be returned in response

### Description

Header "X-Xss-Protection: 1; mode=block" is returned in response

https://stackoverflow.com/questions/9090577/what-is-the-http-header-x-xss-protection#:~:text=X-XSS-Protection%3A%201%3B%20mode%3Dblock%20allows%20attacker%20to%20leak%20data,the%20behavior%20of%20the%20page%20as%20a%20side-channel

### Steps

```
curl 'http://metadata-server-mock.herokuapp.com/unknown' --header 'Accept: */*' --header 'Host: metadata-server-mock.herokuapp.com' --header 'Connection: Keep-Alive' --header 'User-Agent: Apache-HttpClient/4.5.13 (Java/17.0.2)' --compressed --insecure --verbose
```

### Actual outcome

Header "X-Xss-Protection: 1; mode=block" in response

### Expected outcome

No header X-Xss-Protection in response

***

## Bug 7 - [/metadata/{id}/properties/{pid}] Requesting policy and subject property gets invalid json in response

### Description

Requesting subject/policy properties gives invalid JSON response,
even though Content-Type indicates that it should be JSON

### Steps

```
curl 'http://metadata-server-mock.herokuapp.com/metadata/919e8a1922aaa764b1d66407c6f62244e77081215f385b60a62091494861707079436f696e/properties/subject' --header 'Accept: */*' --header 'Host: metadata-server-mock.herokuapp.com' --header 'Connection: Keep-Alive' --header 'User-Agent: Apache-HttpClient/4.5.13 (Java/17.0.2)' --compressed --insecure --verbose
```

or:

```
curl 'http://metadata-server-mock.herokuapp.com/metadata/919e8a1922aaa764b1d66407c6f62244e77081215f385b60a62091494861707079436f696e/properties/policy' --header 'Accept: */*' --header 'Host: metadata-server-mock.herokuapp.com' --header 'Connection: Keep-Alive' --header 'User-Agent: Apache-HttpClient/4.5.13 (Java/17.0.2)' --compressed --insecure --verbose
```

### Actual outcome

For subject:

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

### Expected

JSON body

***

## Bug 8 - [/metadata/{id}/properties/{pid}] Requesting known property for subject in which it does not exist

### Description

Preconditions:

1. Metadata for subject 919e8a1922aaa764b1d66407c6f62244e77081215f385b60a62091494861707079436f696e exists
2. Metadata does not contain "unit" element

When requesting "unit" property inconsistent response is sent:

- plain text message
- application/json content type
- response code 200

### Steps

```
curl 'http://metadata-server-mock.herokuapp.com/metadata/919e8a1922aaa764b1d66407c6f62244e77081215f385b60a62091494861707079436f696e/properties/unit' --header 'Accept: */*' --header 'Host: metadata-server-mock.herokuapp.com' --header 'Connection: Keep-Alive' --header 'User-Agent: Apache-HttpClient/4.5.13 (Java/17.0.2)' --compressed --insecure --verbose
```

### Actual outcome

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

### Expected outcome

Response code 404

## Bug 9 - [/metadata/query] Should fail request with known subject and unknown property

### Description

Requesting unknown property gives built in (subject/decimals/policy) metadata information

### Steps

```
curl 'http://metadata-server-mock.herokuapp.com/metadata/query' --header 'Accept: */*' --header 'Content-Type: application/json' --header 'Host: metadata-server-mock.herokuapp.com' --header 'Connection: Keep-Alive' --header 'User-Agent: Apache-HttpClient/4.5.13 (Java/17.0.2)' --data-binary '{"subjects":["919e8a1922aaa764b1d66407c6f62244e77081215f385b60a62091494861707079436f696e"],"properties":["unknown"]}' --compressed --insecure --verbose
```

### Actual

```
HTTP/1.1 200 OK
Connection: keep-alive
Content-Type: application/json
Content-Length: 470
X-Content-Type-Options: nosniff
Server: WEBrick/1.4.2 (Ruby/2.6.6/2020-03-31)
Date: Wed, 27 Apr 2022 19:28:12 GMT
Via: 1.1 vegur

{
    "subjects": [
        {
            "subject": "919e8a1922aaa764b1d66407c6f62244e77081215f385b60a62091494861707079436f696e",
            "decimals": {
...
```

### Expected

Response code 422

## Bug 10 - [/metadata/query] Should fail request with unknown subject

### Description

Requesting unknown subject inconsistent response

- plain text message
- application/json content type
- response code 200

### Steps

```
curl 'http://metadata-server-mock.herokuapp.com/metadata/nonExistingSubject/properties/description' --header 'Accept: */*' --header 'Host: metadata-server-mock.herokuapp.com' --header 'Connection: Keep-Alive' --header 'User-Agent: Apache-HttpClient/4.5.13 (Java/17.0.2)' --compressed --insecure --verbose
```

### Actual

```
HTTP/1.1 200 OK
Connection: keep-alive
Content-Type: application/json
Content-Length: 48
X-Content-Type-Options: nosniff
Server: WEBrick/1.4.2 (Ruby/2.6.6/2020-03-31)
Date: Wed, 27 Apr 2022 19:37:40 GMT
Via: 1.1 vegur

Requested subject 'nonExistingSubject' not found
```

### Expected

Response code 422

## Bug 11 - Unknown method response is invalid

### Description

Requesting unsupported method gives 404 response

### Steps

```
curl 'http://metadata-server-mock.herokuapp.com/metadata/query' --request PUT --header 'Accept: */*' --header 'Content-Type: text/plain; charset=ISO-8859-1' --header 'Host: metadata-server-mock.herokuapp.com' --header 'Connection: Keep-Alive' --header 'User-Agent: Apache-HttpClient/4.5.13 (Java/17.0.2)' --data-binary '{"subjects":["919e8a1922aaa764b1d66407c6f62244e77081215f385b60a62091494861707079436f696e"]}' --compressed --insecure --verbose
```

### Actual

```
HTTP/1.1 404 Not Found
Connection: keep-alive
X-Cascade: pass
Content-Type: text/html;charset=utf-8
Content-Length: 19
X-Xss-Protection: 1; mode=block
X-Content-Type-Options: nosniff
X-Frame-Options: SAMEORIGIN
Server: WEBrick/1.4.2 (Ruby/2.6.6/2020-03-31)
Date: Wed, 27 Apr 2022 20:21:16 GMT
Via: 1.1 vegur

<html>
  <body>PUT /metadata/query</body>
</html>
```

### Expected

Response code 405

***

To discuss :
- Which properties could be requested ? 
- Are some properties built-in/mandatory? Or this is a bug ?
- Logos are the same for both coins (base64 encoded)
- Should every property have the same SequenceNumber (they are not) ?
- Documentation is inconsistent with the API (i.e. signature)
- Need to verify signatures - which algorithm is used? Ed25519? As of now verification is not passing