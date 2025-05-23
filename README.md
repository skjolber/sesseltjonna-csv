![Build Status](https://github.com/skjolber/sesseltjonna-csv/actions/workflows/maven.yml/badge.svg) 
[![Maven Central](https://img.shields.io/maven-central/v/com.github.skjolber.sesseltjonna-csv/parent.svg)](https://mvnrepository.com/artifact/com.github.skjolber.sesseltjonna-csv)
[![codecov](https://codecov.io/gh/skjolber/sesseltjonna-csv/branch/master/graph/badge.svg)](https://codecov.io/gh/skjolber/sesseltjonna-csv)

# sesseltjonna-csv: CSV processing insanity
**sesseltjonna-csv** is an advanced, high-performance CSV library with developer-friendly configuration options.

Projects using this library will benefit from:

 * friendly builder-pattern for creating CSV-parser with data-binding
    * syntactic sugar
    * per-field customization options
 * under-the-hood dynamic code generator for optimal performance
    * best in class according to the [benchmarks]. 

In a nutshell, a very specific parser is generated per unique CSV file header (i.e. column order), which yields __extremely fast processing while allowing for per-field customizations__. The overhead of doing this is low and pays of surprisingly fast. 

The library also hosts traditional CSV parsers (statically typed) for those wanting to work directly on `String` arrays. 

Bugs, feature suggestions and help requests can be filed with the [issue-tracker].

## Obtain
The project is implemented in Java and built using [Maven]. The project is available on the central Maven repository.

<details>
  <summary>Maven coordinates</summary>

Add

```xml
<properties>
    <sesseltjonna-csv.version>1.0.26</sesseltjonna-csv.version>
</properties>
```

and

```xml
<dependency>
    <groupId>com.github.skjolber.sesseltjonna-csv</groupId>
    <artifactId>databinder</artifactId>
    <version>${sesseltjonna-csv.version}</version>
</dependency>
```

or

```xml
<dependency>
    <groupId>com.github.skjolber.sesseltjonna-csv</groupId>
    <artifactId>parser</artifactId>
    <version>${sesseltjonna-csv.version}</version>
</dependency>
```
</details>

or

<details>
  <summary>Gradle coordinates</summary>
  
For

```groovy
ext {
    sesseltjonnaCsvVersion = '1.0.26'
}
```

add

```groovy
implementation("com.github.skjolber.sesseltjonna-csv:databinder:${sesseltjonnaCsvVersion}")
```
or

```groovy
implementation("com.github.skjolber.sesseltjonna-csv:parser:${sesseltjonnaCsvVersion}")
```

</details>

# Usage - databinding
Use the builder to configure your parser.

```java
CsvMapper<Trip> mapper = CsvMapper.builder(Trip.class)
        .stringField("route_id")
            .quoted()
            .optional()
        .stringField("service_id")
            .required()
        .build();
```

where each field must be either `required` or `optional`. The necessary `Trip` setters will be deducted from the field name (see further down for customization).

Then create a `CsvReader` using

```java
Reader reader = ...; // your input

CsvReader<Trip> csvReader = mapper.create(reader);
```

and parse until `null` using

```java
do {
    Trip trip = csvReader.next();
    if(trip == null) {
        break;
    }

   // your code here    
} while(true);
```

To run some custom logic before applying values, add your own `consumer`:

```java
CsvMapper<City> mapping = CsvMapper.builder(City.class)
    .longField("Population")
        .consumer((city, n) -> city.setPopulation(n * 1000))
        .optional()
    .build();
```

or with custom (explicit) setters:

```java
CsvMapper<Trip> mapper = CsvMapper.builder(Trip.class)
        .stringField("route_id")
            .setter(Trip::setRouteId)
            .quoted()
            .optional()
        .stringField("service_id")
            .setter(Trip::setServiceId)
            .required()
        .build();
```

## Intermediate processor
The library supports an `intermediate processor` for handling complex references. In other words when a column value maps to a child or parent object, it can be resolved at parse or post-processing time. For example by resolving a `Country` when parsing a `City` using an instance of `MyCountryLookup` - first the mapper:

```java
CsvMapper2<City, MyCountryLookup> mapping = CsvMapper2.builder(City.class, MyCountryLookup.class)
    .longField("Country")
        .consumer((city, lookup, country) -> city.setCountry(lookup.getCountry(country))
        .optional()
    .build();
```

Then supply an instance of of the `intermediate processor` when creating the `CsvRader`:

```java
MyCountryLookup lookup = ...;

CsvReader<City> csvReader = mapper.create(reader, lookup);
```

Using this feature can be essential when parsing multiple CSV files in parallel, or even fragments of the same file in parallel, with entities referencing each other, storing the values in intermediate processors and resolving references as a post-processing step. 

# Usage - traditional parser
Create a `CsvReader<String[]>` using

```java
Reader input = ...; // your input
CsvReader<String[]> csvReader = StringArrayCsvReader.builder().build(input);
        
String[] next;
do {
    next = csvReader.next();
    if(next == null) {
        break;
    }
    
   // your code here    
} while(true);
```
Note that the String-array itself is reused between lines. The column indexes can be rearranged  by using the builder `withColumnMapping(..)` methods, which should be useful when doing your own (efficient) hand-coded data-binding. 

# Performance
The dynamically generated instances are extremely fast (i.e. as good as a parser tailored very specifically to the file being parsed), but note that the assumption is that the number of different CSV files for a given application or format is limited, so that parsing effectively is performed by a JIT-compiled class and not by a newly generated class for each file.

To maximize performance (like response time) it is always necessary to pre-warm the JVM regardless of the underlying implementation.

JMH [benchmark results](https://github.com/skjolber/csv-benchmark#results). 

If the parser runs alone on a multi-core system, the [ParallelReader](https://github.com/arnaudroger/SimpleFlatMapper/blob/master/sfm-util/src/main/java/org/simpleflatmapper/util/ParallelReader.java) from the [SimpleFlatMapper](https://simpleflatmapper.org/) might further improve performance by approximately 50%.

## Class-loading / footprint
Performance note for single-shot scenarios and `CsvMapper`: If a custom setter is specified, the library will invoke it to determine the underlying method invocation using `ByteBuddy`, so some additional class-loading will take place.

# Compatibility
The following rules / restrictions apply, mostly for keeping in sync with [RFC-4180]:

 * Quoted fields must be declared as quoted (in the builder) and can contain all characters. 
 * The first character of a quoted field must be a quote. If not, the value is treated as a plain field. 
 * Non-qouted fields must not contain the newline (or separator).
 * Each field is either required or optional (no empty string is ever propagated to the target). Missing values result in `CsvException`.
 * All lines must contain the same number of columns
 * Corrupt files can result in `CsvException`
 * Newline and carriage return + newline line endings are supported (and auto-detected)
   * Must be consistent throughout the document
 * Columns which have no mapping are skipped (ignored).

Also note that

 * The default mode assumes the first line is the header. If column order is fixed, a default parser can be created.
 * Read buffer / max line length is per default 64K. 64K should be enough to hold a lot of lines, if not try increasing the buffer size.

# See it in action
See the project [gtfs-databinding](https://github.com/skjolber/gtfs-databinding) for a full working example.

# Contact
If you have any questions, comments or feature requests, please open an issue.

Contributions are welcome, especially those with unit tests ;)

## License
[Apache 2.0]

# History

 - 1.0.26: Maintenance release
 - 1.0.25: Maintenance release
 - 1.0.24: Maintenance release
 - 1.0.23: Allow for access to the underlying buffer, for error correction purposes - see issue #38.
 - 1.0.22: Parse first line of dynamic parser with static parser.
 - 1.0.21: Make CsvReader AutoCloseable. Remove (unused) ASM dependencies from static parser. Bump dependencies
 - 1.0.20: Fix AbstractCsvReader; better names and EOF fix.
 - 1.0.19: Improve JDK9+ support using moditech plugin, fix parsing of single line without linebreak.
 - 1.0.18: Add default module names for JDK9+, renamed packages accordingly.
 - 1.0.17: Improve parse of quoted columns

[Apache 2.0]:           http://www.apache.org/licenses/LICENSE-2.0.html
[issue-tracker]:        https://github.com/skjolber/sesseltjonna-csv/issues
[Maven]:                http://maven.apache.org/
[benchmarks]:           https://github.com/skjolber/csv-benchmark
[hytta.jpg]:            http://skjolber.github.io/img/hytta.jpg
[ASM]:                  https://asm.ow2.io/
[RFC-4180]:             https://tools.ietf.org/html/rfc4180
