#!/usr/bin/python

import RDF
import os, sys, getopt, time
import MySQLdb

def get_args():    
	try:
		opts, args = getopt.getopt(sys.argv[1:], "h", ["help"])
	except getopt.error, msg:
		print msg
		print "for help use --help"
		sys.exit(2)

	for o, a in opts:
		if o in ("-h", "--help"):
			print __doc__
			sys.exit(0)
	return args


def parse_rdf2n3_fromfile(owlfile, n3file):
	storage=RDF.Storage(storage_name="hashes", name="test", options_string="new='yes',hash-type='memory',dir='.'")
	if storage is None:
		raise "new RDF.Storage failed"

	model=RDF.Model(storage)
	if model is None:
		raise "new RDF.model failed"

	uri=RDF.Uri(string="file:" + owlfile)

	parser=RDF.Parser('raptor')
	if parser is None:
		raise "Failed to create RDF.Parser raptor"

	count=0
	for s in parser.parse_as_stream(uri,uri):
		model.add_statement(s)
		count=count+1

	serializer = RDF.NTriplesSerializer()
	#serializer.set_namespace("dc", RDF.Uri("http://purl.org/dc/elements/1.1/"))
	serializer.serialize_model_to_file(name=n3file, model=model)
	return count


def parse_rdf2n3_frommysql():
	storage=RDF.Storage(storage_name="hashes", name="test", options_string="new='yes',hash-type='memory',dir='.'")
	if storage is None:
		raise "new RDF.Storage failed"
	
	
	parser=RDF.Parser('raptor')
	if parser is None:
		raise "Failed to create RDF.Parser raptor"
	serializer = RDF.NTriplesSerializer()

	
	db = MySQLdb.connect(
	    host = "localhost",
	    user = "root",
	    passwd = "LXF814202", 
	    db = "feb2_7_30"
	)
	cursor = db.cursor()
	cursor.execute("SELECT rdf FROM RAWRDF")
	numrows = int(cursor.rowcount)
	stmtcount = 0
	fidx = 0
	
	logfile = open ('eiao_n3.log', 'a' )
	for x in range(0,numrows):
		model=RDF.Model(storage)
		row = cursor.fetchone()
		parser.parse_string_into_model(model, row[0], 'http://www.egov.org')
		serializer.serialize_model_to_file(name="eiao.n3", model=model)
		sts, log = run_shellcmd("wc -l eiao.n3|cut -d ' ' -f 1")
		stmtcount += int(log.strip())
		fname = "/data2/xltest/eiao%d.n3" %fidx
		sts, log = run_shellcmd("cat eiao.n3>>%s" % fname)
		sts, log = run_shellcmd("rm -f eiao.n3")
		if stmtcount>=25*1000000:
			fidx += 1
			logfile.write("%s=%d\n" %(fname, stmtcount))
			stmtcount = 0
	logfile.close()
		



def run_shellcmd(cmd):
	"""Return (status, output) of executing cmd in a shell."""
	if os.name in ['nt', 'dos', 'os2'] :
		pipe = os.popen(cmd + ' 2>&1', 'r')
	else :
		pipe = os.popen('{ ' + cmd + '; } 2>&1', 'r')
	text = pipe.read()
	sts = pipe.close()
	if sts is None: sts = 0
	if text[-1:] == '\n': text = text[:-1]
	return sts, text 


def load_lumbdataset():
	rdf3x_db = "/data2/xltest/rdf3xdb"
	rdf3x_home = "/home/xiliu/workspace/3xlsystem/test/rdf3x-0.3.4"
	#run_shellcmd("rm -f %s" % rdf3x_db)

	logfile = open ('rdf3xl_load.log', 'a' )
	resultfile = open ('result.txt', 'a')
	logfile.write("\n\n\n========%s======"% time.strftime("%a, %d %b %Y %H:%M:%S +0000", time.gmtime()))
	resultfile.write("\n\n\n========%s======"% time.strftime("%a, %d %b %Y %H:%M:%S +0000", time.gmtime()))

	mpoints = [25*1000000, 50*1000000, 75*1000000, 100*1000000]
	#mpoints = [4*6000, 8*6000, 12*6000, 16*6000]

	n3files = ['/data2/xltest/lubm0.n3', '/data2/xltest/lubm1.n3', '/data2/xltest/lubm2.n3', '/data2/xltest/lubm3.n3' ]
	for s in n3files:
		run_shellcmd("rm -f %s" % s)



	indices_at_mpoints = {}
	totaltriplenum = 0

	fidx = 0
	try:
		for i in range(0, 1000):
			sts, log = run_shellcmd("rm -f *.owl *.n3")
			sts, log = run_shellcmd("java -jar uba.jar -univ 1 -index %d -onto http://www.w3c.org" % i)
			sts, log = run_shellcmd("ls University%d_*.owl|wc -l" % i)
			owlfilenum = int(log)

			for j in range(owlfilenum):
				owlfilename = "University%d_%d.owl" % (i, j)
				n3filename = "University%d_%d.n3" % (i, j)

				triplenum = parse_rdf2n3_fromfile(owlfilename, n3filename)
				run_shellcmd("cat %s>>%s" % (n3filename, n3files[fidx]))
				totaltriplenum += triplenum

				if i>0 and i%100==0:
					print '(%d, %d) = %d\n' % (i, j, totaltriplenum)

				if totaltriplenum>=mpoints[fidx]:
					indices_at_mpoints[n3files[fidx]] =  (i, j, totaltriplenum)
					fidx += 1
					if fidx>=len(mpoints):
						raise StopIteration()
	except StopIteration:
		pass

	for i in range(len(n3files)):
		run_shellcmd("rm -f %s" % rdf3x_db)
		starttime = time.time()
		sts, log = run_shellcmd("%s/bin/rdf3xload %s %s" % (rdf3x_home, rdf3x_db, ' '.join(n3files[:i+1])))
		endtime = time.time()
		logfile.write("\n%s" % log)
		logfile.write("\n-------------------------------------\n")
		totalloadtime = endtime - starttime
		univ, idx, triplenum = indices_at_mpoints[n3files[i]]
		resultfile.write("\n (%d, %d, %d) = %f" % (univ, idx, triplenum, totalloadtime))


	logfile.close()	
	resultfile.close()


	#sts, log = run_shellcmd("/home/xiliu/workspace/3xlsystem/test/rdf3x-0.3.4/bin/rdf3xquery %s %s" % (rdf3x_db, "query.spql"))

	
def load_eiaodataset():
	rdf3x_db = "/data2/xltest/rdf3xdb"
	rdf3x_home = "/home/xiliu/workspace/3xlsystem/test/rdf3x-0.3.4"

	logfile = open ('rdf3xl_load_eiao.log', 'a' )
	logfile.write("\n\n\n========%s======"% time.strftime("%a, %d %b %Y %H:%M:%S +0000", time.gmtime()))
	resultfile = open('eiao_result.log', 'a' )
	
	n3files = ['/data2/xltest/eiao0.n3', '/data2/xltest/eiao1.n3', '/data2/xltest/eiao2.n3', '/data2/xltest/eiao3.n3' ]
	for i in range(1):
		run_shellcmd("rm -f %s" % rdf3x_db)
		starttime = time.time()
		sts, log = run_shellcmd("%s/bin/rdf3xload %s %s" % (rdf3x_home, rdf3x_db, ' '.join(n3files[:4-i])))
		endtime = time.time()
		totaltime = endtime - starttime
		logfile.write("%s \n-------------\n" % log)
		resultfile.write("%d M=%f \n" % (25*(4-i), totaltime))
		resultfile.flush()
	logfile.close()
	resultfile.close()

def parse_rdf2n3file(database="", filename=""):
	storage=RDF.Storage(storage_name="hashes", name="test", options_string="new='yes',hash-type='memory',dir='.'")
	if storage is None:
		raise "new RDF.Storage failed"
	
	parser=RDF.Parser('raptor')
	if parser is None:
		raise "Failed to create RDF.Parser raptor"
	serializer = RDF.NTriplesSerializer()
	
	db = MySQLdb.connect(
	    host = "localhost",
	    user = "root",
	    passwd = "LXF814202", 
	    db = database
	)
	cursor = db.cursor()
	cursor.execute("SELECT rdf FROM RAWRDF")
	numrows = int(cursor.rowcount)

	for x in range(0,numrows):
		row = cursor.fetchone()
		model=RDF.Model(storage)
		parser.parse_string_into_model(model, row[0], 'http://www.egov.org')
		serializer.serialize_model_to_file(name="/data2/xltest/eiaotmp.n3", model=model)
		run_shellcmd("cat /data2/xltest/eiaotmp.n3>>/data2/xltest/%s.n3" %s)
		run_shellcmd("rm -f /data2/xltest/eiaotmp.n3")
	

def proces_eiao2n3():
	folders = []
	for s in folders:
		run_shellcmd('sudo service mysql stop')
		run_shellcmd('sudo rm -rf /data1/mysql/feb*')
		run_shellcmd('sudo scp -P 2222 -r localhost:"/home/xiliu/mysql/%s" /data1/mysql' % s)
		run_shellcmd('sudo -R chown mysql:mysql /data1/mysql/%s' % s)
		run_shellcmd('sudo service mysql start')
		parse_rdf2n3file(s, s)
		
		
	
		
	
if __name__== "__main__":
	#load_lumbdataset()
	parse_rdf2n3_frommysql_onefile()
	#load_eiaodataset()
	

