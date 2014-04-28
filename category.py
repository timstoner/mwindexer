import re

infile = open('enwiki-20140304-pages-articles.xml', 'r')
outfile = open('category.txt', 'w')

pattern = re.compile('\[\[Category:.*\]\]', re.IGNORECASE)
i = 0
for line in infile:
  i += 1
  result = re.search(pattern, line)
  if result is not None:
    outfile.write(result.group())
    outfile.write("\n")
  if i % 10000 == 0:
    print i


