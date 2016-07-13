FROM jetty:alpine

COPY demo-war.war /var/lib/jetty/webapps/${APP_ID}
CMD ["-d", "supervise", "-Djetty.port", "8081"]


EXPOSE 8081