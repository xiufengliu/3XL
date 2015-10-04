#!/bin/bash


#University506_5.owl

#-index
# One university = 140668.77906976745 * 712=100 M triples
#ts-remove -u rdf -p rdf -d rdf -a -v --force
#index=0
#for i in  0 1 2 3 4 5 6 7 8 9
#do
#	un=10		

#	if [ $i -eq 9 ] 
#       then
#	   un=2
#	fi

#	idx=$[$i*10+$index]
		
#	rm *.owl
#	java -jar /home/xiliu/workspace/tmp/uba.jar -onto "http://www.w3c.org/rdf/2.0" -univ $un -index $idx > /dev/null
	#echo "Removing 3store..."
	#ts-remove -u rdf -p rdf -d rdf -a -v --force
#	echo "Loading $i *.owl into 3store..."
#	ts-import -u rdf -p rdf -d rdf -f rdfxml *.owl
	
	#if [ $i -eq 18 ]; then
	 #  for j in 0 1 2 3 4 5 6 7 8 9 10
	#	do
	#	time ts-query -u rdf -p rdf -d rdf -r localhost -l rdql "select * where (<http://www.Department0.University0.edu/GraduateStudent>, <http://www.w3c.org/rdf/2.0#takesCourse>, ?o)" >/dev/null  
         # 	done
	#fi

	#echo "Generating ntriples..."
	#ts-query -u rdf -p rdf -d rdf -r localhost -f ascii -l rdql "select * where (?s,?p,?o)" >/tmp/ntripels.txt
	#echo "Cleaning ntriples..."
	#sed "s/<//g;s/>//g;s/\"//g" /tmp/ntripels.txt|sed  -e 1d -e /subClassOf/d -e /Class/d -e /file:/d -e /triplestore/d>/tmp/test.txt
	#echo "Loading triples into MySQL..."
	#mysql  rdf -uxiliu -pabcd1234 -e "load data infile '/tmp/test.txt' into table ds_rdf_uba(s,p,o)"
	#rm *.owl
#	echo "################ NEXT INDEX = " $[$idx+10] "###########################"  >> NextIndex.txt
#done
#echo "###Done###"

#java -cp  tdb.tdbloader


Workingdir=/home/xiliu/workspace/3xlsystem
Log=/home/xiliu/workspace/3xlsystem/test/benchmark.log
Libs=/home/xiliu/workspace/3xlsystem/lib/postgresql-8.3-604.jdbc4.jar:/home/xiliu/workspace/3xlsystem/lib/mysql-connector-java-5.1.7-bin.jar:/home/xiliu/workspace/3xlsystem/lib/owlapi-bin.jar:/home/xiliu/workspace/3xlsystem/lib/simmetrics_jar_v1_6_2_d07_02_07.jar:/home/xiliu/workspace/3xlsystem/lib/je-3.3.75.jar:/home/xiliu/workspace/3xlsystem/lib/junit-4.5.jar

Src="/home/xiliu/workspace/3xlsystem/src/xlsystem/*.java /home/xiliu/workspace/3xlsystem/src/xlsystem/bdb/*.java"


/usr/lib/jvm/jdk1.6.0_13/bin/javac  -cp .:$Libs $Src -d /home/xiliu/workspace/3xlsystem/build/classes

cd /home/xiliu/workspace/3xlsystem/build/classes
jar -cmf /home/xiliu/workspace/3xlsystem/test/mainClass 3xl.jar xlsystem
mv 3xl.jar /tmp
#for bsize in 60 80 100 120 140  
#do
	cd $Workingdir/config	
	#cp config$bsize config.xml
	cd $Workingdir
        echo "=========== No Optimization =================================" >> $Log
	/usr/lib/jvm/jdk1.6.0_13/bin/java -Xms1024m -Xmx2048m -XX:-UseGCOverheadLimit -cp .:/tmp/3xl.jar:$Libs xlsystem.TripleAdditionTest >>$Log 2>&1
#done



