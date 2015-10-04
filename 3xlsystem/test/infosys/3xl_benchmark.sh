#!/bin/bash

source /home/xiliu/.bashrc

#JAVA_HOME=/usr/lib/jvm/jdk1.6.0_21
JAVA_HOME=/usr/lib/jvm/java-1.6.0-openjdk.x86_64
WORKDIR=/home/xiliu/workspace/3xlsystem/test/infosys
BUILDDIR=/home/xiliu/workspace/ebuild
PRJ_DIR=/home/xiliu/workspace/3xlsystem
LIB=$PRJ_DIR/lib
CONFIG=$PRJ_DIR/config
SRC=/data2/xltest/n3
CLASSPATH=.:$LIB/je-4.0.103.jar:$LIB/commons-collections-3.2.1.jar:$LIB/postgresql-8.3-604.jdbc4.jar:$LIB/owlapi-bin.jar:$LIB/commons-dbutils-1.3.jar:$LIB/SuperCSV-1.52.jar:$LIB/commons-io-1.4.jar:/home/xiliu/workspace/3xlsystem/build/dist/3xlsystem.jar


for d in  "lubm"; do

		for i in   "mp" "array"
		do
			rm -f /data2/xltest/csv/*
			rm -f /data2/xltest/bdb/*
			#rm -rf /home/xiliu/workspace/3xlsystem/build/*

			#cd $BUILDDIR
			#ant -Dproject=3xlsystem

			cd $WORKDIR			
			LOG=/tmp/3XL_"${i}"_"${d}"_2.5GRAM_250kVH_80commit.log
			cp $CONFIG/config_"$d"_"$i".xml $CONFIG/config.xml
			$JAVA_HOME/bin/java -cp $CLASSPATH xlsystem.DBMain --verbose --config=$CONFIG/config.xml
			$JAVA_HOME/bin/java -Xms1024m -Xmx2500m -XX:-UseGCOverheadLimit  -XX:+UseParallelGC -XX:+UseParallelOldGC -cp $CLASSPATH xlsystem.LoadMain --timing --src=$SRC --config=$CONFIG/config.xml >$LOG 2>&1
		done
		
#		rm -rf $SRC
#		scp 172.25.25.49:"/data1/eiaodata/n3.tar.gz" /data2/xltest
#		cd /data2/xltest
#		tar zxvf n3.tar.gz
#		rm n3.tar.gz
done

#psql  xiliu << EOF
#drop table thing cascade;
#EOF


######BigOWLIM##########
#WORKDIR=/home/xiliu/workspace/3xlsystem/paper/data/benchmarkresults/BigOWLIM/program
#cd $WORKDIR
#REPOSITORY=/data2/xltest/bigowlim/repositories
#BIGOWLIM_HOME=/data2/big-owlim-3.3.2264
#EXT_LIB=$BIGOWLIM_HOME/ext
#SESAME_JAR=$EXT_LIB/openrdf-sesame-onejar.jar:$EXT_LIB/slf4j-api-1.5.0.jar:$EXT_LIB/slf4j-jdk14-1.5.0.jar:$EXT_LIB/junit.jar:$EXT_LIB/lucene-core-3.0.0.jar
#CP_TESTS=$BIGOWLIM_HOME/lib/owlim-big-3.3.2264.jar:bin:.:$BIGOWLIM_HOME/ext/lubm.jar:$SESAME_JAR
#$JAVA_HOME/bin/javac  -cp "$CP_TESTS" Benchmark.java 

#rm -f /home/xiliu/workspace/*.log

#FNUM=0
#for datatype in "n3" "owl"; do
#	for dataset in "lubm" "eiao"; do
#		if test ${dataset} = "lubm"; then
#				FNUM=14487
#		else
#				FNUM=3281
#		fi
		
#		n=`ls -l $SRC/*.${datatype} |wc -l`	
#		while [[ $n -lt $FNUM ]]
#		do
#			rm -f $SRC/*.*
#			scp 172.25.25.49:"/home/xiliu/workspace/big-owlim-3.3.2264/test/${dataset}data/${datatype}/*.${datatype}" $SRC
#			n=`ls -l $SRC/*.${datatype} |wc -l`	
#		done
		
#		for jvmsize in 1024 2048; do
#				rm -rf $REPOSITORY/*
#				LOG=/home/xiliu/workspace/BigOWLIM_"$dataset"_"$datatype"_"$jvmsize".log
#				CONFIG=/home/xiliu/workspace/3xlsystem/paper/data/benchmarkresults/BigOWLIM/program/"$dataset".ttl
#				starttime=`date +%s`
#				$JAVA_HOME/bin/java -Xms"$jvmsize"m -cp "$CP_TESTS" Benchmark config=$CONFIG  repoid=$dataset  >$LOG 2>&1
#				echo "-Xms${jvmsize}m -cp ${CP_TESTS} Benchmark config=${CONFIG}  repoid=${dataset}"
#				endtime=`date +%s`
#				totaltime=$(( $endtime-$starttime ))
#				echo "Total time = $totaltime \n" >>$LOG
#		done
#		rm -f $SRC/*.*

#	done 
#done

cd $SRC
for (( i=182; i<=724; ++i )); do
	rm -f University"$i"_*
done

######RDF3X##########
#WORKDIR=/data2/rdf3x-0.3.4/bin
cd /data2/rdf3x-0.3.4/bin

LOG=/tmp/RDF3X_load_lubm_25M.log
starttime=`date +%s`
./rdf3xload rdf3x_lubm25M $SRC/*.n3  >$LOG 2>&1
endtime=`date +%s`
totaltime=$(( $endtime-$starttime ))
echo "Total time = $totaltime \n" >>$LOG

#rm -f rdf3x_lubm100M*



