# http://blog.pernix-solutions.com/pcastro/2012/08/02/debugging-maven-apps-in-netbeans/

DIR=`dirname $0`/..

MAVEN_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8000" mvn -f $DIR clean jetty:run -o
