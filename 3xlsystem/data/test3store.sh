#!/bin/sh

query(){
	case $1 in 
	0) s=\<$2\>
	   p=\<$3\>
	   o="?o"
	;;
	1) s=\<$2\>
	   p=\<$3\>
	   o=\'$4\'
	;;
	2) s=\<$2\>
	   p=\<$3\>
	   o="?o"
	;;
	3) s=\<$2\>
	   p="?p"
	   o=\'$4\'
	;;
	4) s=\<$2\>
	   p="?p"
	   o="?o"
	;;
	5) s="?s"
	   p=\<$3\>
	   o=\'$4\'
	;;
	6) s="?s"
	   p=\<$3\>
	   o="?o"
	;;
	7) s="?s"
	   p="?p"
	   o=\'$4\'
	;;
	8) s="?s"
	   p="?p"
	   o="?o"
	;;
	esac

	if [ $1 -eq 0 ]; then 
		ts-query -u rdf -p rdf -d rdf -r localhost -l rdql "select * where ($s,$p,$o)"  	
	elif [ $1 -eq 8 ]; then
		#echo "Running $i ..."
		elapsed_seconds_3s=0
		before_3s="$(date +%s%N)"
		ts-query -u rdf -p rdf -d rdf -r localhost -l rdql "select * where ($s,$p,$o)"  >/dev/null
		after_3s="$(date +%s%N)"
		elapsed_seconds_3s=$(expr $elapsed_seconds_3s + $after_3s - $before_3s)		
		echo "$elapsed_seconds_3s/1000000.0" |bc -l
	else
		elapsed_seconds_3s=0
		for i in 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20; do
			#echo "Running $i ..."
			before_3s="$(date +%s%N)"
			ts-query -u rdf -p rdf -d rdf -r localhost -l rdql "select * where ($s,$p,$o)"  >/dev/null
			after_3s="$(date +%s%N)"
		
			elapsed_seconds_3s=$(expr $elapsed_seconds_3s + $after_3s - $before_3s)		
		done
		echo "$elapsed_seconds_3s/1000000.0/20.0" |bc -l
	fi
}


for i in 1 2 3 4 5 6 7 8; do
	#query $i "http://www.eiao.net/rdf/2.0#PageSurvey_http://www.eiao.net/rdf/2.0/PageSurvey_header143" "http://www.eiao.net/rdf/2.0#headerset_cookie" "ASP.NET_SessionId=h1x2dz55f4cbvevbvfwtpy45; path=/"
	query $i "http://www.eiao.net/rdf/2.0/SiteSurvey_0" "http://www.eiao.net/rdf/2.0#pageSurvey" "http://www.eiao.net/rdf/2.0#PageSurvey_http://www.eiao.net/rdf/2.0/PageSurvey_45"
done

