FROM jetty:alpine

CMD ["-d", "supervise", "-Djetty.port", "8081"]

EXPOSE 8081