FROM gradle:8.5.0-jdk17-alpine as build
WORKDIR /opt/memeDB/search-engine
COPY build.gradle.kts build.gradle.kts
COPY src src
RUN gradle clean bootJar

FROM azul/zulu-openjdk-alpine:17-jre
WORKDIR /opt/memeDB/search-engine
COPY --from=build /opt/memeDB/search-engine/build/libs/search-engine-0.0.1-SNAPSHOT.jar .
CMD java -jar search-engine-0.0.1-SNAPSHOT.jar --spring.config.location=classpath:/application.yml,optional:/etc/memDB/search-engine/application.yml
