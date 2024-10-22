# Library Management System with RESTful API

## Assumptions And Design Decisions
* Java 8 is now end of life, has known security vulnerability 
and is not supported by SpringBoot 3. The language used is consistently
**Java 8+** (rather than **Java 8**). Let's assume Java 17 is an acceptable compromise.
* Let's assume that library is intended to be reused at the microservice level by
calling the RESTful API, rather than as a Java library. So no need for a multi-module
set up.
* We could model Book as a value object using a Record. This lends itself to an
event driven architecture which would have advantages in a distributed system
context. 
  * Let's keep things simple for now by assuming that we're aiming at first
  to create a singleton microservice backed by an ephemeral library class. 
  This is a classic choice for rapid prototyping a proof of concept. We should be mindful around SOLID principles since we're likely to
  want to be able to switch out this implementation for a data store (either RDBMS or NoSQL) 
  or else some sort of distributed cache for production.
* We'll model as a classic POJO. Let's assume that only available copies is
mutable. At some later stage, should the API evolve to include additional
mutation methods, we can add that to the domain object. 
  * The Library API includes a method that passes in a book object. Interesting 
  design question about whether to allow subclasses. I tend to prefer making objects 
  final in case of doubt around whether the API is intended to allow subclassing.
  Let's go with that for now.
  * There are a lot of parameters with the same type. Let's add a builder to increase
  readability.
* This project may well be too small to justify this, but I like to organise using packages.
* I'm from the minimal comments school of thought but the spec has 
* Looking ahead
* Going be relying on equals to have domain meaning so let's test
* For a small library with a few books then a set or list. Let's assume that the library 
management system should support large numbers of books whilst providing an efficient 
find by unique attribute (ISBN). Based on a Map. 
  * The simplest thread safe design would have been to use synchronisation on the public methods.
  Effectively serializing access to the service. This would have had the advantage of simplicity and maintainability.
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
  




  
