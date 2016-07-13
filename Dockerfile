FROM jetty:alpine

COPY demo-war.war /var/lib/jetty/webapps/demo.war}
CMD ["-d", "supervise", "-Djetty.port", "8081"]


EXPOSE 8081