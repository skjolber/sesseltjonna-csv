# sesseltjonna-csv: High-performance CSV databinding
![alt text][hytta.jpg]

[![Build Status](https://travis-ci.org/skjolber/sesseltjonna-csv.svg)](https://travis-ci.org/skjolber/sesseltjonna-csv)

**sesseltjonna-csv** is a high-performance CSV databinding library with developer-friendly configuration options.

Projects using this library will benefit from:

 * dynamically generated CSV parsers per file (at runtime, using [ASM])
 * per-field configuration options
 * builder with support for syntactic sugar and customization options
 * world-class performance according to the [benchmarks]. 

In a nutshell, a very specific parser is generated per unique CSV file header, which yields extremely fast processing while allowing for per-field customizations. 

The primary use-case for this library is __large csv files__ with more than 1000 lines where the CSV file format is known and reasonable stable.

Bugs, feature suggestions and help requests can be filed with the [issue-tracker].

## Obtain
The project is implemented in Java and built using [Maven]. The project is available on the central Maven repository.

Example dependency config:

```xml
<dependency>
    <groupId>com.github.skjolber.sesseltjonna-csv</groupId>
    <artifactId>sesseltjonna-csv</artifactId>
    <version>1.0.1</version>
</dependency>
```

# Usage
Use the builder to configure your parser.

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

where each field must be either `required` or `optional`. Use of the `setter` is optional; a setter with the same name as the field name will be automatically selected in its abscense. 

Then create a `CsvReader` using


```java
Reader reader = ...; // your input

CsvReader<Trip> csvReader = mapper.create(reader);
```

and parse untill `null` using

```java
do {
    Trip trip = csvReader.next();
    if(trip == null) {
        break;
    }

   // your code here    
} while(true);
```

If you're into doing some custom logic before applying values, add your own `consumer`:

```java
CsvMapper<City> mapping = CsvMapper.builder(City.class)
    .longField("Population")
        .consumer((city, n) -> city.setPopulation(n * 1000))
        .optional()
	.build();
```

# Performance
The generated instances are quite fast (i.e. as good as a __hardcoded__ version), but note that the assumption is that the number of different CSV files for a given application or format is limited, so that parsing effectively is performed by a JIT-compiled class and not by a newly generated class for each file.

To maximize performance (like response time) it is always necessary to pre-warm the JVM regardless of the underlying implementation.

JMH [benchmark results](https://github.com/skjolber/csv-benchmark#results). 

# Compatibility
The following rules / restrictions apply, mostly for keeping in sync with RFC-4180

 * Quoted fields must be declared as quoted (in the builder) and can contain all characters. 
 * The first character of a quoted field must be a quote. If not, the value is treated as a plain field. 
 * Quoting must be done using double quotes. Two double quotes must be used to escape a single double quote.
 * Plain fields must not contain the separator or newline, otherwise can contain all characters.
 * All fields are either required or optional (no empty string is ever propagated to the target). Missing values result in CsvException.
 * Columns which have no mapping are skipped (ignored).
 * All lines must contain the same number of columns
 * Corrupt files can result in CsvException
 * Newline and carriage return + newline line endings are supported (and auto-detected).

Also note that

 * The default mode assumes the first line is the header. For fixed formats, a default parser can be created.
 * Maximum line length is per default 64K. 64K should be enough to hold a lot of lines, if not try increasing the buffer size to improve performance.

# Contact
If you have any questions, comments or feature requests, please open an issue.

Contributions are welcome, especially those with unit tests ;)

## License
[Apache 2.0]

# History
 - [1.0.1]: Improve exception handling
 - 1.0.0: Initial release.

[Apache 2.0]: 			http://www.apache.org/licenses/LICENSE-2.0.html
[issue-tracker]:		https://github.com/skjolber/sesseltjonna-csv/issues
[Maven]:				http://maven.apache.org/
[1.0.1]:		    	https://github.com/skjolber/sesseltjonna-csv/releases
[benchmarks]:			https://github.com/skjolber/csv-benchmark
[hytta.jpg]:			http://skjolber.github.io/img/hytta.jpg
[ASM]:					https://asm.ow2.io/
