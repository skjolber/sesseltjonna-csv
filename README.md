# sesseltjonna-csv: Extremely fast CSV databinding
![alt text][hytta.jpg]

[![Build Status](https://travis-ci.org/skjolber/sesseltjonna-csv.svg)](https://travis-ci.org/skjolber/sesseltjonna-csv)

**sesseltjonna-csv** is an extremely fast CSV databinding library with developer-friendly configuration options.

Projects using this library will benefit from:

 * dynamically generated CSV parsers per file (at runtime)
 * per-field configuration options
 * builder with support for syntactic sugar and customization options
 * world class performance - fastest in the world according to the [benchmarks]. 

In a nutshell, a new parser is generated per unique CSV file header, which yields extremely fast processing while allowing for per-field customizations. 

Bugs, feature suggestions and help requests can be filed with the [issue-tracker].

## Obtain
The project is implemented in Java and built using [Maven]. The project is available on the central Maven repository.

Example dependency config:

```xml
<dependency>
    <groupId>com.github.skjolber.sesseltjonna-csv</groupId>
    <artifactId>sesseltjonna-csv</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

# Usage
Use the builder to configure your parser.

```java
CsvClassMapping<Trip> mapping = CsvClassMapping.builder(Trip.class)
		.stringField("route_id")
			.setter(Trip::setRouteId)
			.quoted()
			.optional()
		.stringField("service_id")
			.setter(Trip::setServiceId)
			.required()
		.build();
```

where each field must be either `required` or `optional`. Use of the `setter` is optional, if there is a setter with the same name as the field name, it will be automatically selected. Then create a `CsvClassFactory` using


```java
Reader reader = ...; // your input

CsvClassFactory<Trip> factory = mapping.create(reader);
```

and parse untill `null` using

```java
do {
	Trip trip = factory.next();
	if(trip == null) {
		break;
	}

   // your code here	
} while(true);
```

If you're into doing some custom logic before applying values, add your own `consumer`:

```java
CsvClassFactory<City> mapping = CsvClassMapping.builder(City.class)
	.longField("Population")
		.consumer((city, n) -> city.setPopulation(100 + n))
		.optional()
	.build();
```

## Performance
Although the default implementation is quite fast out-of-the-box, the assumption is that the number of different CSV files for a giving application or format is limited (probably to 1 or 2), so that parsing effectively is performed by a JIT-compiled class. 

To maximize performance (like response time) it is always necessary to pre-warm the JVM regardless of the underlying implementation.

JMH [benchmark results](https://github.com/skjolber/csv-benchmark#results). 

# Contact
If you have any questions or comments, please email me at thomas.skjolberg@gmail.com.

Feel free to connect with me on [LinkedIn], see also my [Github page].
## License
[Apache 2.0]

# History
 - [1.0.0]: Initial release.

[Apache 2.0]: 			http://www.apache.org/licenses/LICENSE-2.0.html
[issue-tracker]:		https://github.com/skjolber/sesseltjonna-csv/issues
[Maven]:				http://maven.apache.org/
[LinkedIn]:				http://lnkd.in/r7PWDz
[Github page]:			https://skjolber.github.io
[1.0.0]:		    	https://github.com/skjolber/sesseltjonna-csv/releases
[benchmarks]:			https://github.com/skjolber/csv-benchmark
[hytta.jpg]:			http://skjolber.github.io/img/hytta.jpg

