'''
 
 * Copyright (c) 2011, Xiufeng Liu (xiliu@cs.aau.dk) and the eGovMon Consortium
 * Permission to use, copy, modify, and distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *
 *
 '''

import socket
import sys
import re, time



HOST = 'localhost'    # The remote host
PORT = 9000           # The same port as used by the server
END='End\n'             # The flag of tranfer data end
p=re.compile('(.*)\n')

def connect_server(host, port):
        client = None
        for res in socket.getaddrinfo(host, port, socket.AF_INET, socket.SOCK_STREAM):
                af, socktype, proto, canonname, sa = res
                try:
                        client = socket.socket(af, socktype, proto)
                except socket.error, msg:
                        client = None
                        continue
                try:
                        client.connect(sa)
                except socket.error, msg:
                        client.close()
                        client = None
                        continue
                break
        return client

def recv_data(client):
        total_data=[];data=''
        prefix = ''
        total_time = 0
        while True:
                start_time=time.time() 
                data=client.recv(8192)
                total_time += time.time()-start_time
                da = prefix + data
                result = p.findall(da)
                for rdf in result:
                        print rdf
                prefix = da.split('\n'.join(result) + '\n')[-1]
                if End in da:
                        break
                
        print "Total time used=%s\n" % total_time


if __name__=='__main__':
        client = connect_server(HOST, PORT)
        if client is None:
                print 'could not open socket'
                sys.exit(1)
        for i in range(1,2):
                #Experiment 1: 4b
                #client.send("(http://www.Department0.University0.edu/GraduateStudent1,http://www.w3c.org/rdf/2.0#takesCourse,*)")
                #client.send("(http://www.Department0.University0.edu/GraduateStudent1,http://www.w3c.org/rdf/2.0#0#undergraduateDegreeFrom,*)");
                #client.send("(http://www.Department0.University0.edu/GraduateStudent24,http://www.w3c.org/rdf/2.0#emailAddress,*)");
                
                #Experiment 1: 4c
                #client.send("(http://www.Department0.University0.edu/GraduateStudent1,*,*)");
                #client.send("(http://www.Department0.University0.edu/GraduateStudent1,*,GraduateStudent1@Department0.University0.edu)");
                #client.send("(http://www.Department0.University0.edu/GraduateStudent12,http://www.w3c.org/rdf/2.0#takesCourse,*)");
                
                
                #Experiment 1: 4d
                #client.send("(*,http://www.w3c.org/rdf/2.0#researchInterest,Research6)")
                #client.send("(*,http://www.w3c.org/rdf/2.0#teachingAssistantOf,*)")
                
                #Experiment 1: 4e
                #client.send("(*,*,Research1)")
                #client.send("(*,*,http://www.Department0.University0.edu/AssistantProfessor1)")
                #client.send("http://www.Department0.University0.edu/AssistantProfessor1/Publication1,http://www.w3c.org#publicationAuthor,http://www.Department0.University0.edu/AssistantProfessor1")
               
		
		# Query 1		
		'''		
		client.send("(GraduateStudent, type, ?X)");
		client.send("(?X, takesCourse, http://www.Department0.University0.edu/GraduateCourse0)");

		# Query 2
		client.send("(GraduateStudent, type, ?X)");
		client.send("(University, type, ?Y)"); 
		client.send("(Department, type, ?Z)"); 
		client.send("(?X, memberOf,  ?Z)"); 
		client.send("(?Z, subOrganizationOf, ?Y)");
		client.send("(?X, undergraduateDegreeFrom, ?Y)");

		# Query 3

		client.send("(Publication,type,?X)")
		client.send("(?X,publicationAuthor,http://www.Department0.University0.edu/FullProfessor0)")


		# Query 4 
		
		client.send("(Professor,type,?X)")
		client.send("(?X,worksFor,http://www.Department0.University0.edu)")
		client.send("(?X,name,?Y1)") 
		client.send("(?X,emailAddress,?Y2)")
		client.send("(?X,telephone,?Y3)")
		

		# Query 5 
		
		client.send("(Person,type,?X)")
		client.send("(?X,memberOf,http://www.Department0.University0.edu)")
		
		
		#Query 6 
		client.send("(Student,type,?X)")
		

		
		#Query 7 
		
		client.send("(Student,type,?X)")
		client.send("(Course,type,?Y)")
		client.send("(http://www.Department0.University0.edu/AssociateProfessor0,teacherOf,?Y)")
		client.send("(?X,takesCourse,?Y)")

		
		#Query 8

		client.send("(Student,type,?X)")
		client.send("(Department,type,?Y)")
		client.send("(?X,memberOf,?Y)")
		client.send("(?Y,subOrganizationOf,http://www.University0.edu)")
		client.send("(?X,emailAddress,?Z)")
		

		#Query 9

		client.send("(Student,type,?X)")
		client.send("(Faculty,type,?Y)")
		client.send("(Course,type,?Z)")
		client.send("(?X,advisor,?Y)")
		client.send("(?X,takesCourse,?Z)")
		client.send("(?Y,teacherOf,?Z)")

		
		#Query 10

		client.send("(Student,type,?X)")
		client.send("(?X,takesCourse,http://www.Department0.University0.edu/GraduateCourse0)")

		
		#Query 11

		client.send("(ResearchGroup,type,?X)")
		client.send("(?X,subOrganizationOf,http://www.Department0.University0.edu)")
		'''
		#Query 12 Note: We have changed "Chair" to "FullProfessor" as no inference is implemented in our program
		client.send("GenSchema\n")
		
		client.send("(FullProfessor,type,?X)\n")
		client.send("(Department,type,?Y)\n")
		client.send("(?X,worksFor,?Y)\n")
		client.send("(?Y,subOrganizationOf,http://www.University0.edu)\n")
		client.send(END)
		'''
		
		#Query 13  Note: Not implemented as inference is required.

		client.send("(Person,type,?X)")
		client.send("(?X,hasAlumnus,http://www.University0.edu)")
		

		#Query 14 
		client.send("(UndergraduateStudent,type,?X)")
		#recv_data(client)
		'''
		client.close()
