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
  * An appropriate design trades **less valuable** qualities for **more valuable** ones.
    * For example
      * A high throughput concurrent solution might trade off addition costs per instance
        (more memory) and less elasticity (increased instance boot costs) for more maintainable and readable concurrency approaches 
        that are less heavily optimised.
      * A low latency design might adopt more sophisticated concurrency paradigms which require
      more time to safely maintain.
  * For the purpose of this exercise, we'll need to make some assumptions to make progress.
* A **Lean Proof of Concept** would be one suitable use case for the Library class described.
  * In this case, we might look to trade off some architectural qualities such as horizontal scalability
  and elasticity for reduced time to initial delivery into production. Development of solutions with improved
  elasticity and horizontal scalability would be on the future roadmap but could be informed 
  by data collected from this initial delivery.
  * Let assume that this microservice will be such a **Lean Proof of Concept** and be deployed
  as a singleton microservice on a capable instance.
* Data flow is an important to consider when designing concurrent solutions. Often how a 
system is used will only be known once it is in use in production. 
  * A Lean Proof of Concept based on some reasonable assumptions can be delivered into 
  production early and monitoring use to quantify the requirements.
  * Let's assume that 
    * books will 
        * be added and remove relatively rarely and
        * be borrowed and returned more frequently, 
    * and that searches 
      * by ISBN will be common, and
      * by Author uncommon,
    * and that there will be a large number of books in the library.
* Borrowing and returning are based on ISBN. 
  * Given our assumption that these operations occur much more frequently than addition and removal,
  we should trade in favour of retrieval by ISBN at the price of insertion and deletion.
  * We will base Library on a Map indexed by ISBN.
* Given our assumptions, retrieval operations by key (borrow, return and find by ISBN) are
expected to occur with far greater frequency than traversals (find by author), insertions (add book)
and deletions (remove book). 
  * Synchronising every method or the entire map would be inefficient in these circumstances.
* We need to consider whether to trade *strong consistency* for efficiency.
  * We are assuming that read operations on the map (get by key and traversals) are far more 
  common than write operations (insertions and deletions).
  * A *strongly consistent* solution would need to lock out reads during writes. This locking
  overhead would be paid on every read.
  * A *weakly consistent* solution would trade off some stale reads for less overhead per read.
  * Let's assume that allowing *weak consistency* is an acceptable starting point for a Lean 
  Proof of Concept.
  * The Library design adopted is based on a ConcurrentSkipListMap which is an efficient but 
  weakly consistent thread safe map. 
* Searches by ISBN will retrieve data by key and are expected to be efficient. 
  * Given our assumptions, it is reasonable to accept a slow traversal for the search by author.
    * We have the option of adding in a front side cache later for search results.
* There is an API design decision around how to support addBook when adding a book with the same
  ISBN twice to the same library. Though it might be reasonable to merge the availabilityCount,
  there seems no natural way to merge different titles, authors and publication years. 
  * Let's assume that a book cannot be added twice.
  * The only attribute which can be mutated by the API is availableCopies.
    * Let's make the reasonable assumption that all other attributes are immutable and
    that availableCopies can only be incremented or decremented
* Given these assumptions and our choice of a ConcurrentMap based approach, it seems reasonable
to use a mutable internal data class which will result in more obvious, readable and maintainable 
code than a more sophisticated approach.  
  * Given the front side cache proposed in the spec, this approach should be efficient enough
  to make this a reasonable trade off.
* The spec is silent on the required behaviour when a call is made to borrow a book when no
copies are available. 
  * Our imagined use case is **Lean Proof of Concept**. 
  Let's apply the Principal of Least Surprise and insist that our Library will only allow
  books to be borrowed when there are copies available.
  * This additional requirement introduces some interesting design factors which will be 
  discussed in the final section *Available Copies - Compare And Swap*.

### API Design Details
* Until
  business logic TODO let's assume that the library should just reject.
    * We could throw an exception or use a return value. The principle of least surprise
      leans towards throwing an explicit exception.
    * Java Set return a value from add, whilst throwing an IllegalArgumentException in the case
      of validation. Consistency with Java collection leans towards returning a value.
    * Let's go with consistency for the moment.
* For consistency with Java collections, removeBook will return a boolean
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
* Maintaining even a simple cache of limited size imposes costs, especially when concurrency is
  considered. It is hard to know in advance whether caching would be worth the costs.
  * In the spirit of a Lean Proof of Concept, let's go ahead and add a cache but take advantage
  of dependency injection and a uniform interface to allow use to reconfigure easily without 
  the cache.
  * This is the sort of circumstances well suited to spring profiles but time was against me.
  * A natural pattern in these circumstances would be a write through cache.
* Searching by Author is likely to be slow, perhaps too slow for large libraries. 
  * Adding a search cache would be reasonable but the spec talks in terms of frequently 
  accessed books and effectively caching search results requires quite a lot of knowledge 
  around how search happens in production. Better to leave that problem will later.
* Let's assume that it's acceptable to 
  * cache a limited (but configurable) number of books
  by ISBN,
  * evicted least recently used books to make space, and
  * invalidate on write by evicting.
* This leads to a simple design which is good enough for an initial delivery until good data
is available from production.

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
  quality JWT implementation rests on many assumptions.
* Let's assume that this API will be accessed by other microservices, and that these 
microservices will be able to acquire JWT tokens independently.
    * Perhaps from a third party service like Amazon Cognito, or
    * by generating them.
* Spring Boot 3 recommends Spring Security. Spring Security is complex and sophisticated.  
  * The spec does not explicitly include authorization. An approach based on using a 
  simple HTTP filter without integration with Spring Security would satisfy the base 
  requirements.
  * Let's assume that authorization based on the subject of the token will be needed at
  some later stage and go with a Spring Security based approach, accepting that it will
  be complex.
  * Given the assumption that tokens will be minted by some third party - rather than 
  by this microservice - in Spring Security terms we will be going down the PreAuthentication
  route and should adopt the PreAuthentication framework, even though it might appear 
  a little unintuitive.
* There are numerous ways that caller could supply JWT tokens. The conventional approach 
for RESTful APIs is to pass the JWT token as a Bearer token in an HTTP AUTHORIZATION header.
  * Let's assume that this approach is appropriate.
* A JWT token consist of a payload and meta-data. The conventional design when JWT is used
for authentication is to use the payload to pass claims with a signature in the meta-data.
  * Let's assume that this approach is appropriate.
* JWT supports encryption to protect sensitive payloads. HTTPS would encrypt bearer tokens
together with the rest of the headers.
  * Let's assume that the tokens will not be encrypted.
* It is natural to map the Subject claimed by the token to the Spring Security 
Principal, though not always correct.
    * Let's assume that this is correct in this case.
* JWT tokens typically include a claim about expiry. When present, these should be enforced.
  * Let's adopt Postel's Law and permit JWT tokens with signed subjects who are 
  missing claims about expiry.
* Given our assumption that authentication will be the responsibility of a third party, 
we should pass dummy credentials to Spring Security rather than extract claims from the token.
  * Even if the token includes credential claims, there is no need for these to be known by 
  this microservice.
* JWT supports a wide range of signature algorithms, both symmetric (shared secret) 
and asymmetric (public-private key). Key distribution is a difficult problem, configuration
would be involved and extensive testing would be needed on a variety of platforms.
  * As a compromise in the spirit of a lean proof of concept, the `secure` profile configures an RSA public key whose private key
  is included in the integration testing source code. 

## Decrementing Available Copies - Compare And Swap 

Compare and Swap (CAS) is a concurrency technique often associated with non-blocking algorithms.
It is supported well in Java by the Atomic concurrency classes. 

In this case, we wish to conditionally decrement the number of available copies for a book. 

A blocking based approach would lock out concurrent access to the block that mutates the
number of available copies. Both increments and decrements to the same book would need to 
be serialized.

A non-blocking approach retries the validation if the value has been modified in the window
between retrieval and storing the updated value. 

This trades off decreased efficiency when borrow is heavily contended for improved efficiency
when borrowing is uncontested. It seems reasonable to assume that borrowers will rarely
compete to borrow the same book at the same time so this is a good starting point.
 
Of course, this is all paper theory. The only real way to tell is to profile alternative
approaches at volume using like-live data.