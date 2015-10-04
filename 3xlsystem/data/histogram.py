#!/usr/bin/python
# vim:sw=2:sts=2:expandtab:
# This scripts allows the easy plotting of histograms with gnuplot.
# It uses boxes to achieve the desired effect.
# Usage: ./histogram.py < [data-file]
# Written by Mustafa Paksoy. Oct 06

import sys
import os
import tempfile

# Change the following constants to get the titles you want
OUTPUTFILE="out.png" # Change this to change the output file
THICKNESS=0.2 # Thickness of histogram boxes, adjust this if histograms look
              # ugly
FIELDSEP="\t" # Field seperator used in data file 
GRAPHTITLE="Default Title"
YTITLE="Loading time(s)"
XTITLE="Triple number loaded(M)"

# Make sure you know what you're doing if you change stuff after this line
GPLOTCONF="set term png\n"+\
          "set output '"+OUTPUTFILE+"'\n"+\
          "set boxwidth "+THICKNESS.__str__()+"\n"+\
          "set style fill pattern\n"+\
          "set xrange [-0.2:]\n"+\
          "set title '"+GRAPHTITLE+"'\n"+\
          "set ylabel '"+YTITLE+"'\n"+\
          "set xlabel '"+XTITLE+"'\n"

i=0
tempfiles=[]
xtics=[]
ytics=[]
for line in sys.stdin.readlines():
  line=line.lstrip().rstrip()
  if not (line[0].isdigit() or len(xtics)): # get column labels
    for field in line.split(FIELDSEP):
      xtics.append(field)
    continue

  fd=tempfile.NamedTemporaryFile()
  tempfiles.append(fd)
  fields=line.split(FIELDSEP)
  j=0
  for field in fields:
    if not field[0].isdigit():
      ytics.append(field)
    else:
      fd.write((j+i*THICKNESS).__str__()) #bar location
      fd.write(FIELDSEP)
      fd.write(field+"\n")
      fd.write((j+(i+1)*THICKNESS).__str__()) 
      fd.write(FIELDSEP)
      fd.write("0\n")
      fd.flush()
      j+=1

  i+=1

cols=i-1
gplot=os.popen("gnuplot -persist", "w")
gplot.write(GPLOTCONF)

# Gen plot command
plotcmd="plot "
i=0
for file in tempfiles:
  plotcmd += "'"+file.name+"' with boxes"
  if len(ytics):
   plotcmd += " title \""+ytics[i]+"\"" 
  plotcmd +=", "
  i+=1
plotcmd=plotcmd[:len(plotcmd)-2] # truncate the last ", "
plotcmd+="\n"

# Gen xtics command
xticscmd=""
if len(xtics):
  xticscmd+="set xtics ("
  i=0
  for xtic in xtics:
    if (xtic==xtics[0]) and len(ytics): # skip row name
      continue
    xticscmd+="\""+xtic+"\" "+(i+cols*THICKNESS/2).__str__()+", "
    i+=1

  xticscmd=xticscmd[:len(xticscmd)-2]
  xticscmd+=")\n"

gplot.write(xticscmd)
gplot.write(plotcmd)
gplot.close()

for file in tempfiles:
  file.close()
