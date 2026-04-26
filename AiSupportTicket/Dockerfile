FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY target/AiSupportTicket-0.0.1-SNAPSHOT.jar app.jar

ENV TZ=Asia/Shanghai
ENV JAVA_OPTS="-Xms256m -Xmx512m"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
