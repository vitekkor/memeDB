FROM gradle:8.5.0-jdk17-alpine as build
WORKDIR /opt/memeDB/telegram
COPY build.gradle.kts build.gradle.kts
COPY src src
RUN gradle clean bootJar

FROM azul/zulu-openjdk-alpine:17-jre
WORKDIR /opt/memeDB/telegram
COPY --from=build /opt/memeDB/telegram/build/libs/telegram.jar .
CMD java -jar telegram.jar