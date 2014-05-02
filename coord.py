import re
import sys

infile = open(sys.argv[1], 'r')
outfile = open('coord.txt', 'w')

coordpattern = re.compile('\{\{coord.*\}\}', re.IGNORECASE)
pagepattern = re.compile('<page>')

pagecount = 0
for line in infile:
  result = re.search(pagepattern, line)
  if result is not None:
    pagecount += 1
    if pagecount % 10000 == 0:
      print pagecount
  result = re.search(coordpattern, line)
  if result is not None:
    outfile.write(result.group())
    outfile.write("\n")

print pagecount
print "done"


