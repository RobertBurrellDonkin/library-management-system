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


  




  
