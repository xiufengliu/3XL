#!/bin/bash

export JAVA_HOME=/usr/lib/jvm/jdk1.6.0_16
export xl_home=/home/xiliu/workspace/3xlsystem
export libs=.:$xl_home/build/classes:$xl_home/lib/je-3.3.75.jar:$xl_home/lib/junit-4.5.jar:$xl_home/lib/mysql-connector-java-5.1.7-bin.jar:$xl_home/lib/postgresql-8.3-604.jdbc4.jar:$xl_home/lib/owlapi-bin.jar:$xl_home/lib/simmetrics_jar_v1_6_2_d07_02_07.jar

export src="$xl_home/src/xlsystem/*.java $xl_home/src/xlsystem/*/*.java $xl_home/src/xlsystem/*/*/*.java $xl_home/src/xlsystem/*/*/*/*.java"
export dst=$xl_home/build/classes

workdir=$xl_home/test
log=$workdir/query.log

javac -cp $libs $src -d $dst
cd $dst

for i in 1 2 3 4 5 6 7 8 9 10 11 12 14
do
	echo "---------------------" >> $log
	echo "Q $i" >> $log
	sudo service postgresql restart
	su -c "sync; echo 3>/proc/sys/vm/drop_caches; free"
	cat /home/xiliu/Documents/Windows7/en_windows_7_professional_x86_dvd_x15-65804.iso>/dev/null
	java -Xms1024m -Xmx2048m -XX:-UseGCOverheadLimit -cp $libs xlsystem.QueryMain --config config.conf --query $workdir/queries/query$i.rq  --time >>$log 2>&1
done
cd $workdir

