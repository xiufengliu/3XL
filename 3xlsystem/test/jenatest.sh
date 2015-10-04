#!/bin/bash

#Src=/home/xiliu/workspace/Jena/src/jena/*.java
#Workingdir=/home/xiliu/workspace/Jena/build/classes
#Dest=/home/xiliu/workspace/Jena/build/classes

Src=/home/xiliu/workspace/TDB/src/tdb/*.java
Workingdir=/home/xiliu/workspace/TDB/bin
Dest=/home/xiliu/workspace/TDB/bin


Log=/home/xiliu/workspace/3xlsystem/test/benchmark.log
#Libs_db=.:/home/xiliu/workspace/Jena/build/classes:/home/xiliu/workspace/Jena/lib/arq-extra.jar:/home/xiliu/workspace/Jena/lib/arq.jar:/home/xiliu/workspace/Jena/lib/icu4j_3_4.jar:/home/xiliu/workspace/Jena/lib/iri.jar:/home/xiliu/workspace/Jena/lib/jena.jar:/home/xiliu/workspace/Jena/lib/jenatest.jar:/home/xiliu/workspace/Jena/lib/json.jar:/home/xiliu/workspace/Jena/lib/junit-4.5.jar:/home/xiliu/workspace/Jena/lib/log4j-1.2.12.jar:/home/xiliu/workspace/Jena/lib/lucene-core-2.3.1.jar:/home/xiliu/workspace/Jena/lib/slf4j-api-1.5.6.jar:/home/xiliu/workspace/Jena/lib/slf4j-log4j12-1.5.6.jar:/home/xiliu/workspace/Jena/lib/stax-api-1.0.jar:/home/xiliu/workspace/Jena/lib/wstx-asl-3.0.0.jar:/home/xiliu/workspace/Jena/lib/xercesImpl.jar:/home/xiliu/workspace/3xlsystem/lib/postgresql-8.3-604.jdbc4.jar:/home/xiliu/workspace/Jena/lib/uba.jar 


Libs_file=.:/home/xiliu/workspace/TDB/bin:/home/xiliu/workspace/TDB/lib/arq-extra.jar:/home/xiliu/workspace/TDB/lib/arq.jar:/home/xiliu/workspace/TDB/lib/icu4j_3_4.jar:/home/xiliu/workspace/TDB/lib/iri.jar:/home/xiliu/workspace/TDB/lib/jena.jar:/home/xiliu/workspace/TDB/lib/jenatest.jar:/home/xiliu/workspace/TDB/lib/json.jar:/home/xiliu/workspace/TDB/lib/junit-4.5.jar:/home/xiliu/workspace/TDB/lib/log4j-1.2.15.jar:/home/xiliu/workspace/TDB/lib/lucene-core-2.3.1.jar:/home/xiliu/workspace/TDB/lib/slf4j-api-1.5.6.jar:/home/xiliu/workspace/TDB/lib/slf4j-log4j12-1.5.6.jar:/home/xiliu/workspace/TDB/lib/stax-api-1.0.jar:/home/xiliu/workspace/TDB/lib/tdb-0.8.1.jar:/home/xiliu/workspace/TDB/lib/wstx-asl-3.0.0.jar:/home/xiliu/workspace/TDB/lib/xercesImpl.jar:/home/xiliu/workspace/Jena/lib/uba.jar

javac -cp  $Libs_file $Src -d $Dest


cd $Workingdir
rm *.owl

st=0
for ed in 184 # 322 460 599 727
do
echo "$st $ed"
/usr/lib/jvm/jdk1.6.0_16/bin/java -Xms1024m -Xmx2048m -XX:-UseGCOverheadLimit -cp $Libs_file tdb.tdbloader $st $ed  >>$Log 2>&1

#for j in 10 11 12 13 14 15
#do
#sudo sync; echo 3>/proc/sys/vm/drop_caches; free
#/usr/lib/jvm/jdk1.6.0_16/bin/java -cp $Libs_file tdb.tdbquery --desc=tdb.ttl --query=query$j.rq --time | grep "Time"  >>$Log 2>&1
#echo $j
#done
#st=$(expr $ed + 1)

done

#echo "Remove model"
#/usr/lib/jvm/jdk1.6.0_16/bin/java -cp $Libs_db jena.dbremove --db jdbc:postgresql://localhost:5432/jena --dbType PostgreSQL --dbUser xiliu --dbPassword abcd1234  --model lubm

#echo "Create model"
#/usr/lib/jvm/jdk1.6.0_16/bin/java -cp $Libs_db jena.dbcreate  --db jdbc:postgresql://localhost:5432/jena --dbType PostgreSQL --dbUser xiliu --dbPassword abcd1234  --model lubm

#echo "Load model"
#/usr/lib/jvm/jdk1.6.0_16/bin/java -Xms1024m -Xmx2048m -XX:-UseGCOverheadLimit -cp $Libs_db jena.dbload >>$Log 2>&1

