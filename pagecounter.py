#!/usr/bin/python

import sys
import re

infile = open(sys.argv[1], 'r')
pagepattern = re.compile('<page>')
revisionpattern = re.compile('<revision>')

pagecount = 0
revisioncount = 0
for line in infile:
  result = re.search(pagepattern, line)
  if result is not None:
    pagecount += 1
    if pagecount % 10000 == 0:
      print "pagecount {}".format(pagecount)
  result = re.search(revisionpattern, line)
  if result is not None:
    revisioncount += 1
    if revisioncount % 10000 == 0:
      print "revisions {}".format(revisioncount)
    
print "pagecount {}".format(pagecount)
print "revisions {}".format(revisioncount)
print "done"


