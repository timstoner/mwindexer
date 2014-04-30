import re

infile = open('enwiki-20140304-pages-articles.xml', 'r')
outfile = open('category.txt', 'w')

categorypattern = re.compile('\[\[Category:.*\]\]', re.IGNORECASE)
pagepattern = re.compile('<page>')
i = 0
for line in infile:
  result = re.search(pagepattern, line)
  if result is not None:
    i += 1
    if i % 10000 == 0:
      print i
  result = re.search(categorypattern, line)
  if result is not None:
    outfile.write(result.group())
    outfile.write("\n")

print i
print "done"


