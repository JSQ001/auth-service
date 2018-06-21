#!/bin/sh
echo $JAVA_OPTS
echo $PROJECT_NAME
if [ -n "$JAVA_OPTS" ]; then
  java -Djava.security.edg=file:/dev/./urandom $JAVA_OPTS -jar /$PROJECT_NAME.war
else
  java -Djava.security.edg=file:/dev/./urandom -jar -Xmx1024m /$PROJECT_NAME.war
fi
