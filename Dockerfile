FROM maven:3.9.6-amazoncorretto-21 as build
RUN mkdir -p /api
WORKDIR /api
COPY pom.xml /api
COPY src /api/src
COPY startup.sh /api/startup.sh
RUN chmod +x /api/startup.sh
RUN mvn -f pom.xml clean package

FROM amazoncorretto:21.0.2-alpine3.19
COPY --from=build /api/target/*.jar app.jar
COPY --from=build /api/startup.sh /startup.sh
RUN chmod +x /startup.sh

RUN apk add --no-cache openssl
EXPOSE 8080/tcp
ENTRYPOINT ["/bin/sh", "/startup.sh"]