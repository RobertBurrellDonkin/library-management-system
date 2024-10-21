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
  




  
