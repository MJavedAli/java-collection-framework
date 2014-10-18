#Java Collection Framework

The Java development team that created the original `java.util` collection classes was new to object
oriented design and made many of the common mistakes of novices:
 * *implementation inheritance* instead of *delegation* (a Stack is **not** a Vector!)
 * confusing *ordered* vs *sortable* collections (e.g. SortedSet)
 * exposing underlying implementations (e.g. hashtable, linked list, vs tree)

Later additions to the package helped address some of these issues, but the designers were required
to live with many of the original mistakes to maintain compatibility.

##Clean Slate
This project starts with a clean slate. It contains a set of Java collection classes that are independent
of the `java.util` collection classes but that still integrate with the built-in Java language features
that depend on the `java.lang.Iterable` interface. These classes provide the following nice features:
 * simple, easy to understand interfaces
 * self optimizing implementations
 * a framework in which to develop more specialized collection classes

##Collection Classes
The following collections are supported:
 * *Bag* - An ordered collection that allows duplicate elements.
 * *Set* - An ordered collection that supports set operations and does not allow duplicate elements.
 * *List* - A sortable collection that preserves the order that elements are placed in it.
 * *Map* - A sortable collection that associates keys with values where the keys and values can be of
any type.
 * *Dictionary* - A sortable collection that associates strings with values where the values can be of
any type.
 * *Stack* - A collection that implements a *last in first out* (LIFO) abstraction.
 * *Queue* - A thread safe collection that implements a blocking *first in first out* (FIFO) abstraction.

##Getting Started
To get started using these classes, include the following dependency in your maven pom.xml file:

```xml
    <dependency>
        <groupId>com.craterdog</groupId>
        <artifactId>java-collection-framework</artifactId>
        <version>3.0</version>
    </dependency>
```

##Choosing a Collection
The following flow chart provides a way to easily choose which collection type is right for your
needs:

TBD
