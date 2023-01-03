FROM gradle:7.2-jdk17 as build

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM openjdk:17-oracle

WORKDIR /umweltrechner

EXPOSE 8081 9999

COPY --from=build /home/gradle/src/build/libs/umweltrechner-0.0.1-SNAPSHOT.jar /umweltrechner/umweltrechner.jar

ENTRYPOINT ["java", "-jar", "umweltrechner.jar"]