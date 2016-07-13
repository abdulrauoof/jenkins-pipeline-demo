FROM jetty:alpine
COPY demo-war.war /var/lib/jetty/webapps/demo.war
