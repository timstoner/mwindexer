mwindexer
========

Project that reads a MediaWiki dump file and posts to a Solr Server. Based upon the mwdumper project. This project uses a combination of Apache Maven, Spring and SolrJ to read a MediaWiki XML dump file and posts pages to a Solr Server. Supports using the example server right out of the box. 

Some capabilities:
Uses the filters from the mwdumper project to filer out pages and revisions. Filters are configured using the Spring applicationContext.xml, command line support is a TODO. Filters 'decorate' (design pattern) each dump writer. mwindexer supports multiple dump writers, although only one is configured in the project. Also working on supporting custom text indexers. You can see that in the CoordIndexer/CategoryIndexer, which parses the text in the article revision and looks for coord templates and category links.

Hope you use it and it meets your needs. Also thinking about building a StackExchange dumper/indexer using mwdumper/this as a code baseline.

To get started:
Compile and build JAR: mvn package
java -jar mwindexer.jar -i [input_path]
