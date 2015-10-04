3XL:An efficient OWL/RDF Triple Store Supporting Bulk Operations
====================
# Introduction

An increasing number of (semantic) web applications store a very large number of triples in specialized storage engines called triple-stores. Often, triple-stores are used mainly as plain data stores, i.e., for inserting and retrieving large amounts of triples, but not using more advanced features such as logical inference, etc. However, current triple-stores are not optimized for such operations and/or do not support OWL Lite. This paper presents 3XL, a triple-store that efficiently supports operations on very large amounts of OWL Lite triples. The distinguishing features of 3XL include a) using object-relational DBMS features such as inheritance, b) using specialized data-dependent schema (intelligent partitioning), c) extensive use of bulk-loading techniques, and d) efficient use of main memory. 3XL supports both efficient loading as well as efficient query operations, especially when the subject and/or predicate is known. Extensive experiments with a PostgreSQL-based implementation show that 3XL performs very well for such operations.
 
 
# Installation
* **pre-requisites**

JDK 6 or above
PostgreSQL 8.3 or above
* **setup**

> Download 3XL to the desitination directory, unpack it.
> Create a database, and a superuser account (superuser account is required to do the COPY);
> Create a directory for temporarly saving csv files, e.g., /path/to/csv, and the directory for Berkeley DB, e.g., /path/to/bdb;
> Setup the configuration file /bin/config.xml
# Run the programs:

Currently, 3XL only supports NTriple data loading.
* **Use the GUI client**

Start the 3XL server by bin/startserver.sh
Start the client by bin/startclient.sh
* **Use the command Line**

* Setup the running environment:
- export LIB=`pwd`/lib
- export CLASSPATH=.:3xlsystem.jar;$LIB/je-4.0.103.jar:$LIB/commons-collections-3.2.1.jar:$LIB/postgresql-8.3-604.jdbc4.jar:$LIB/owlapi-bin.jar:$LIB/commons-dbutils-1.3.jar:$LIB/SuperCSV-1.52.jar:$LIB/commons-io-1.4.jar
- Create the database schema:
- java -cp $CLASSPATH xlsystem.DBMain [--verbose] [--config=/path/to/config.xml]
* *Load:*
- java -Xms1024m -Xmx2500m -XX:-UseGCOverheadLimit -XX:+UseParallelGC -XX:+UseParallelOldGC -cp $CLASSPATH xlsystem.LoadMain [--timing] [--config=/path/to/config.xml] --src=/path/to/ntriple
* *Query:*
- java -cp $CLASSPATH xlsystem.QueryMain [--runmode [single|server]] [--timing] [--config=/path/to/config.xml] --query=/path/to/query.txt
 
