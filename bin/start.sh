DIR=`dirname $0`/..

MAVEN_OPTS="" mvn -f $DIR clean jetty:run -o
