#!/bin/bash

classes=.:/home/xiliu/workspace/3xlsystem/build/classes:/home/xiliu/workspace/3xlsystem/lib/je-3.3.75.jar:/home/xiliu/workspace/3xlsystem/lib/junit-4.5.jar:/home/xiliu/workspace/3xlsystem/lib/mysql-connector-java-5.1.7-bin.jar:/home/xiliu/workspace/3xlsystem/lib/postgresql-8.3-604.jdbc4.jar:/home/xiliu/workspace/3xlsystem/lib/owlapi-bin.jar:/home/xiliu/workspace/3xlsystem/lib/simmetrics_jar_v1_6_2_d07_02_07.jar
src="/home/xiliu/workspace/3xlsystem/src/xlsystem/*/*.java /home/xiliu/workspace/3xlsystem/src/xlsystem/*.java /home/xiliu/workspace/3xlsystem/src/xlsystem/*/*/*.java"
dest=/home/xiliu/workspace/3xlsystem/build/classes

workingdir=/home/xiliu/workspace/3xlsystem/test
/usr/lib/jvm/jdk1.6.0_16/bin/javac -cp $classes $src -d $dest


cd $dest
jar -cmf /home/xiliu/workspace/3xlsystem/test/mainClass 3xl.jar xlsystem
mv 3xl.jar /tmp


cd $workingdir

echo "#####Start Time: `date` ############" >>$workingdir/benchmark.log
#echo "">>$workingdir/benchmark.log

for i in 1 
do
cp /home/xiliu/workspace/3xlsystem/config/config$i /home/xiliu/workspace/3xlsystem/config/config.xml
if [ $i -eq 10 ] 
then
  echo "##### One valueholder cache" >>$workingdir/benchmark.log
else
  echo "##### Multiple valueholder cache" >>$workingdir/benchmark.log
fi

for n in 4
do
rm -rf /home/xiliu/Music/*
rm -rf /home/xiliu/workspace/3xlsystem/csv/*.csv

/usr/lib/jvm/jdk1.6.0_16/bin/java -Xms1024m -Xmx2048m -XX:-UseGCOverheadLimit -cp $classes:/tmp/3xl.jar xlsystem.load.LoaderTest $n >> $workingdir/benchmark.log 2>&1
done
done

#echo "###################### Multi-Property Table #########################"  >> benchmark.log
#rm /home/xiliu/workspace/3xlsystem/src/xlsystem/Constants.java
#mv /home/xiliu/workspace/3xlsystem/src/xlsystem/Constants.bak /home/xiliu/workspace/3xlsystem/src/xlsystem/Constants.java
#/usr/lib/jvm/java-6-sun-1.6.0.13/bin/javac -cp .:/home/xiliu/workspace/3xlsystem/bin:/home/xiliu/workspace/3xlsystem/ext/je-3.3.75/lib/je-3.3.75.jar:/home/xiliu/eclipse/plugins/org.junit4_4.3.1/junit.jar /home/xiliu/workspace/3xlsystem/src/xlsystem/*.java /home/xiliu/workspace/3xlsystem/src/xlsystem/*/*.java -d /home/xiliu/workspace/3xlsystem/bin

#cd /home/xiliu/workspace/3xlsystem/bin
#jar -cmf /home/xiliu/workspace/3xlsystem/test/mainClass 3xl.jar xlsystem
#mv 3xl.jar /tmp
#cd $workingdir
#/usr/lib/jvm/java-6-sun-1.6.0.13/bin/java -Xms1024m -Xmx2048m -XX:-UseGCOverheadLimit -cp .:/tmp/3xl.jar:/home/xiliu/workspace/3xlsystem/lib/postgresql-8.3-604.jdbc4.jar:/home/xiliu/workspace/3xlsystem/lib/je-3.3.75.jar:/home/xiliu/workspace/3xlsystem/lib/mysql-connector-java-5.1.7-bin.jar xlsystem.TripleAdditionTest $start $end >> benchmark.log 2>&1

#echo "###################### Test 3Store #########################"  
#cd /home/xiliu/workspace/3xlsystem/bin
#/usr/lib/jvm/java-6-sun-1.6.0.13/bin/java -cp . xlsystem.Test3Store  >> /home/xiliu/workspace/3xlsystem/3store.log 2>&1

#echo "######################End Time: `date`#########################"  >> benchmark.log
