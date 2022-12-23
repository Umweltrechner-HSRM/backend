FROM openjdk:17-oracle
WORKDIR /umweltrechner

EXPOSE 8081 9999

COPY build/libs/umweltrechner-0.0.1-SNAPSHOT.jar /umweltrechner/build/

WORKDIR /umweltrechner/build

ENTRYPOINT java -jar umweltrechner-0.0.1-SNAPSHOT.jar