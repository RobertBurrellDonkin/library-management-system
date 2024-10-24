# A Library Management System with A RESTful API

## Overview

A Spring Boot 3 microservice with both bonuses -

* Basic rate limiter based on concurrent request
* Simple JWT authentication based on Spring Security supporting tokens signed using asymmetric cryptography

Assuming that the **Java 8+** specified includes later version and noting that

* Java 8 is now end of life,
* has known security vulnerabilities,
* and is not supported by SpringBoot 3

this solution requires Java 17. It is expected to run on later versions but has only been tested on Java 17.

This document tries to keep close to the structure outlined in the submission instructions.

## How To Run

The solution is a conventional Spring Boot 3 microservice and can be run in the usual way. Profiles are used to allow
authentication variations.

By default, the microservice will start on port 8080 but this can be changed using the usual mechanisms

### Without JWT Authorization

The ``insecure`` profile permits all requests.

```
$ mvn spring-boot:run -Dspring-boot.run.profiles=insecure
```

Running this profile is the recommended starting point.

### With JWT Authorization

The ``secure`` profile includes a sample public key which private key is used in integration tests.

```
$ mvn spring-boot:run -Dspring-boot.run.profiles=secure
```

The following Authorization HTTP Header with a JWT bearer token allows authorized access when running this profile.

```
Authorization: Bearer eyJhbGciOiJSUzUxMiJ9.eyJzdWIiOiJhZG1pbiJ9.J7apSRgAlXDZjZ1kY1K4GRhjn1ikq3qSQrGJy7P6d5wSxBcs27CbWfF8alJmffpXUsuM2WQbAHsCbzG6Httd1K8sYBE1TCakOPT84fg-BzAIPMVxtpGc9Mtv_tSj_QYb96atZJjjtkobKlyGz4t2LIU10ZyN55QQIN2pHKuMkpnKGEKTlCYAzBFFC7NzAPBtPyJXt7Thl1oC-ChHBBeKEWE--cifLRyFDru3G6o-r5ud5hpgM7rCV3AGICRUppvZoZxdMRb94ECuhCzveEbY9fEMKCeWQKlIC4DNppxzVkXQ6NYjZnhx-S9FuVaTtJgotZPdy3yxLru9LGRUIJk1TTOF08zP1dbvKX4QDac7_3YqLkCCLMy_gRRI71rqypnbZkh5R5r1_S-Rv9m5B9IA7c-kBc3NnAZ_oAjF2YGlL1QotbkF1lr7XBkFT4OHgKqRukiwiWjUMURyexxel_y8iL06_GB9LgyvWiYpckdfWJK9_yXE_q4sJXuMTAODND5FPX7KfqYJjg3Ok5uqGkor3ZEDDj9Bbm2tx-uLO4SC56HxSwcuEVwBxdHqYrEKL-99EcgIKaZrrXLjtBgtSP2PuhJxFpzEzu7aEro534p4VO3ySfjG8s-2GMEzbUirx15MSlaiizi7mdPqbWvBNS2rGzXmUELjsjt_z9ve5KiILW4
```

Alternatively, JwtTokenBuilder generates suitable JWT tokens for the integration tests, and could be used to generate
tokens with other claims.

Note that this solution authenticates using sighed JWT bearer tokens but does not include authorization.
Full access is permitted to all subjects.

## RESTful API

The library books API endpoints are based at http://localhost:8080/api/books

### Book Data Structure

Books are modelled as JSON objects:

* **Structure**

| Attribute         | Type   | Required | Constraints | 
|-------------------|--------|----------|-------------|
| `isbn`            | string | Yes      |             |
| `title`           | string | Yes      |             |
| `author`          | string | Yes      |             |
| `publicationYear` | number | Yes      | Positive    |
| `availableCopies` | number | Yes      | Positive    |

* **Example**

```json
{
  "isbn": "some-isbn",
  "title": "some-title",
  "author": "some-author",
  "publicationYear": 2001,
  "availableCopies": 4
}
```

### AddBook

Adds a new book to the library.

```plaintext
POST /api/books
```

* **Accepts** JSON
* **Payload** `Book` object
    * **Example**

```json
{
  "isbn": "some-isbn",
  "title": "some-title",
  "author": "some-author",
  "publicationYear": 2001,
  "availableCopies": 4
}
``` 

#### Success Response

* **Status** `201 CREATED`
* **Body** (empty)

#### Error Responses

##### Book Is Already In Library

* **Status** `409 CONFLICT`
* **Body** (empty)

##### Invalid Book

* **Status** `400 BAD REQUEST`
* **Body** JSON error messages

| Attribute         | Type   | Required | Description                                                       | 
|-------------------|--------|----------|-------------------------------------------------------------------|
| `isbn`            | string | No       | Any validation errors associated with the `isbn` field            |
| `title`           | string | No       | Any validation errors associated with the `title` field           |
| `author`          | string | No       | Any validation errors associated with the `author` field          |
| `publicationYear` | string | No       | Any validation errors associated with the `publicationYear` field |
| `availableCopies` | string | No       | Any validation errors associated with the `availableCopies` field |

* **Example**

```json
{
  "author": "author is mandatory",
  "availableCopies": "availableCopies must be positive",
  "isbn": "isbn is mandatory",
  "publicationYear": "publicationYear must be positive",
  "title": "title is mandatory"
}
```

### RemoveBook

Removes a book from the library by ISBN.

```plaintext
DELETE /api/books/{isbn}
```

where

* `{isbn}` is the ISBN of the book to be removed

#### Success Response

* **Status** `200 OK`
* **Body** (empty)

#### Error Responses

##### Book Is Not In Library

* **Status** `404 NOT FOUND`
* **Body** (empty)

### FindBookByISBN

Returns a book by its ISBN.

```plaintext
GET /api/books/{isbn}
```

where

* `{isbn}` is the ISBN of the book to be found

#### Success Response

* **Status** `200 OK`
* **Payload** `Book` object
    * **Example**

```json
{
  "isbn": "some-isbn",
  "title": "some-title",
  "author": "some-author",
  "publicationYear": 2001,
  "availableCopies": 4
}
```

#### Error Responses

##### Book Is Not In Library

* **Status** `404 NOT FOUND`
* **Body** (empty)

### FindBooksByAuthor

Returns a list of books by a given author

```plaintext
GET /api/books?author={author}
```

where

* `{author}` is the name of the author

#### Success Response

* **Status** `200 OK`
* **Payload** Array of `Book` object, empty when no books in the library were written by the given author
    * **Example**

```json
[
  {
    "isbn": "some-isbn",
    "title": "some-title",
    "author": "some-author",
    "publicationYear": 2001,
    "availableCopies": 4
  }
]
```

### BorrowBook

Decreases the available copies of a book by 1.

```plaintext
POST /api/books/{isbn}/borrow
```

where

* `{isbn}` is the ISBN of the book to be borrowed

#### Success Response

* **Status** `200 OK`
* **Body** (empty)

#### Error Responses

##### Book Is Not In Library

* **Status** `404 NOT FOUND`
* **Body** (empty)
*

##### No Available Copies

* **Status** `409 CONFLICT`
* **Body** JSON error message

| Attribute      | Type   | Required | Description                | 
|----------------|--------|----------|----------------------------|
| `errorMessage` | string | Yes      | The nature of the conflict |

* **Example**

```json
{
  "errorMessage": "No copies available"
}
```

### ReturnBook

Increased the available copies of a book by 1.

```plaintext
POST /api/books/{isbn}/return
```

where

* `{isbn}` is the ISBN of the book to be borrowed

#### Success Response

* **Status** `200 OK`
* **Body** (empty)

#### Error Responses

##### Book Is Not In Library

* **Status** `404 NOT FOUND`
* **Body** (empty)

# Assumptions And Design Decisions

## System Requirements
### Java 
  * is now end of life (as is Spring Boot 2), 
  * has known security vulnerabilities,
  * and is not supported by SpringBoot 3. 
* The language the spec uses is consistently **Java 8+** (rather than **Java 8**). 
    * **Let's assume Java 17 is acceptable.**

## Organisation
### Project Layout
* Let's assume that this solution is intended to be deployed as a component within 
a system of **collaborating microservices** perhaps orchestrated by Kubernetes on 
a service mesh such as Istio. 
  * In this sort of **microservices architecture**, domain logic and functionality will
  be reused at the microservices level (rather than the library level). In this case,
  by calling the RESTful endpoints, as opposed to import library code. 
* A multi-module project isn't needed for a thin microservice such as this,
  and we do not expect other projects to depend on code from this project. 
* We'll adopt a single module project design.
* Consistency is important when maintaining projects within a finely
grained microservice architecture. Developers without recent familiarity
with a particular project need to be able to get up to speed quickly. 
Consistency in packaging naming conventions facilities this process.
  * Let's assume that these standard are reasonably close to common Spring Boot conventions
  with a slight bias towards a flatter structure allowing additional top level packages.
  * I lead towards a finely grained microservice architecture. Overly deep package structures
  indicating overly complex microservice may be a *code smell* highlighting
  a microservice which has drift from the path of single responsibility and
  a system which is failing to appropriately separate concerns.

## The Domain 
* Microservice domain models tend to lean towards one of two patterns
  * Rich POJOs encapsulating domain logic, or
  * Simple immutable data records
* **Immutability** reduces the difficulty of maintaining and comprehending concurrent solutions.
  * An internal mutable representation unable to **escape** is a common pattern for concurrent 
  in-memory stores.
* This solution ended up modelling **Book** as a simple immutable *Record* in the domain.
  * This design trades off the creation of additional Book objects for reduced contention and the
  use of non-blocking algorithms in the store.
    * The microservices deployment should be tuned to have additional memory, spare CPUs and
    an appropriate parallel GC algorithm to allow these objects to be efficiently garbage 
    collected.
    * A low latency solution might have traded otherwise.

## Library Design
* As the CAP Theorem neatly illustrates, the design of a concurrent solution requires trading off some qualities for
others. 
  * Designs within the context of cloud based architectures also need to factor in concerns
  around **elasticity** (the ability to scale up and down to meet demand efficiently) and about the costs of **horizontal** (more instances) verses **vertical**
    (more powerful instances).
  * An appropriate design would trade **less valuable** qualities for **more valuable** ones.
    * For example, a high throughput concurrent solution might trade off addition costs per instance
      (more memory) and less elasticity (increased instance boot costs) for more maintainable and readable concurrency approaches 
      that are less heavily optimised.
  * For the purpose of this exercise, we'll need to make some assumptions to make progress.
* For a small library with a few books then a set or list. Let's assume that the library
  management system should support large numbers of books whilst providing an efficient
  find by unique attribute (ISBN). Based on a Map.
    * The simplest thread safe design would have been to use synchronisation on the public methods.
      Effectively serializing access to the service. This would have had the advantage of simplicity and
      maintainability.
      For a small library, this would have been an excellent option.
    * Let's assume the need to support concurrent access.
    * Java has a rich range of options. For a high throughput, low latency application,  
      a bespoke solution may be the best option.
    * Let's assume that we are looking for reasonable concurrent efficient whilst maintaining
      readability. ConcurrentSkipList TODO weaknesses, strengths
* There is an API design decision around how to support addBook when adding a book with the same
  ISBN twice to the same library. Though it might be reasonable to merge the availabilityCount,
  there seems no natural way to merge different titles, authors and publication years. Until
  business logic TODO let's assume that the library should just reject.
    * We could throw an exception or use a return value. The principle of least surprise
      leans towards throwing an explicit exception.
    * Java Set return a value from add, whilst throwing an IllegalArgumentException in the case
      of validation. Consistency with Java collection leans towards returning a value.
    * Let's go with consistency for the moment.
* For consistency with Java collections, removeBook will return a boolean
* Let's assume that the efficiency of searches like findBooksByAuthor are less important
  than simplicity and maintainability. We have the option of adding a front side cache to improve
  search performance later. We'll just use a filter.
* Let's opt to keep the library code more readable and maintainable by making the Book domain
  object thread safe. Book will need to be final to indicate that subclassing is not recommended.
  We could have opted for value objects and an internal implement but that would have added
  a lot of complexity.
    * Due to differences across hardware, not practical to create a unit test that reliable demonstrates
      concurrency issues. We would need to augment the unit and integrate tests with end to end testing
      on a like-live system.
    * For consistency with AtomicInteger, book will return the number of available copies
* There is the question of the case of decrementing a book when it has no copies. Let's assume
  that we should apply business logic to prevent borrowing books when there are no copies in the
  library.
    * And domain logic that the number of available copies must be a positive integer or zero.
* Borrow book will return a boolean for consistency. Need to think about behaviour when there are no copies.
  Let's push on for now.
* If we want to be able to apply business logic around borrowing, we'll need to stop the internal
  representation escaping. So we'll need to switch a record and an internal representation.
* Before pushing on to create a cache, let's extract an interface.

# RESTful API
* REST design TODO (see notes)
* Considered a reactive design but reject
* POST -> created
    * There isn't a clear consensus around the right HTTP status code to return when a resource already exist,
      Let's return 409.
* DELETE -> ok or not found
* Let's consider find by author to be a search for a resource uniquely indexed by ISBN. This will
  return a list of books.
* borrowBook and returnBook are operations performed on a resource which have no easy mapping to
  to CRUD verbs.
    * PATCH isn't really suitable since we're just asking for an update
    * The results really isn't cachable and isn't idempotent
    * Probably best modelled as a POST. One option might be to use JSON but the data model
      doesn't seem rich enough to justify the extra complexity of a JSON model.
    * Let's assume that a POSTing to path param is reasonable
    * From an APi perspective, it feels like the client may really want the current book
      details, and perhaps that the updated details should be returned in the body.
      We'll keep the API simple for now, though.

* End To End test - just the golden path
* Let's assume all attributes are required and that available copies and publication year must be positive.
* 409 CONFLICT ->
    * The client should GET the book before trying to borrow. If the book has been subsequently borrowed then the client
      state is in conflict with the server state. The client should retry the GET to discover when a copy has been
      returned/

## Configuration
* I'll need to get around to documenting the configuration at some stage.
* Added configuration property to switch auth on and off. Added no authentication profile to
  allow manual and automated testing.

# Additional Features and Optimizations

## Caching Books
* Maintain even a simple cache of limited size imposes costs, especially when concurrency is
  considered. The most reasonable assumption is that books will be added and remove, borrowed and returned
  relatively infrequently, and that findByISBN is quick. We'll cache search results in a map and
  invalidate. Provided that we synchronize, we can build a simple LRU cache based on LinkedHashMap.
* Let's separate concerns around invalidation logic from the actual cache by using an interface.
* This is going to be tricky, since we need to invalidate based on isbn but we search on author.
*
* A front side cache for searches is an interesting problem but on reading more carefully,
  we're asked for frequently accessed books.
    * Let's accept that searches will be slow but that's an interest problem. Let's assume that
      it's just findByISbn that should be cached. This simplifies the logic.
    * We could perhaps add books returned by search but that seems like adding complexity without
      compelling reason.

## Basic Rate Limits

* Design discussion around rate limiter.
    * For rate limiting the services, a delegate or perhaps AOP annotations would have been an elegant solution.
    * We could limit the endpoints indirectly by rate limiting the services in this fashion but
      a more direct approach would seem more natural.
    * Opt for a HandlerInterceptors for the end points until /api/books
    * There are different ways rate limits might be accomplished but we'll opt for a RESTful
      approach and return an appropriate HTTP code when the rate is beyond the limit.
    * Thinking about what we are limiting, there are several reasonable interpretations
        * we could limit the number of concurrent requests in flight
        * or limit the number of requests within a time period
    * Limiting concurrent requests protects the but is more friendly to clients. Let's assume that
      billing isn't related to rate limiting and limit concurrent requests.

## Simple JWT Authentication

* Good security design requires knowledge, both of the technologies and the context. A production
  standard JWT implementation would rest on a lot of assumptions.
    * Let's assume that this API will be accessed by other microservices who will bwe able to mint tokens either
      directly for example by a service like Amazon Cognito or by passing through.
        * JWT could be passed via a cookie, but we'll opt for a header since this is usually more
          natural for microservice to microservice calls.
        * We'll parse the token and check for a claim about the subject (sub). If this is present,
          we'll check using a simple interface whether this subject is allowed access.
        * Let's assume that only a handful of microservices are authorised. So we'll externalise
          the configuration. For larger numbers of subjects, we'd probably opt for a data store or
          a dedicated identity federator.
        * There's also the expiry, which we should really check. Typically a production token issued
          by a system such Amazon Cognito would have a limited time before expiry.
        * There is also the question of algorithm and key. Public/private key cryptography is
          the more robust solution. The public key is not confidential and could be safely
          externalised as part of the configuration.
    * There is also the question of testing, both manual and automated.
        * Profiles are likely to be an attractive option.
        * "jwt" and "unrestricted" profiles
* Some interesting design decisions around Jwt authenticator. Library now parser and validates in a single operation.
  So makes sense to inject the validation key.
    * I opted to use a domain object for readability.
* The assumption is that isn't a user/password but a signed subject. This means we
    * In Spring Security terms, this is a PreAuthenticated scenario. Spring Security provides a little framework for
      scenarios such as this. Let's adopt it.
    * This is a authentication, rather than authorization.
    * For production use,
    * Responsibility of the token provider to add expired. So won't check.

## Compare And Swap

* TODO CAS approach for borrow




 


  
