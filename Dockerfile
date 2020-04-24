FROM maven:3.6-jdk-11-slim AS build
WORKDIR /root
COPY scinote-server /root/scinote-server
COPY scinote-client /root/scinote-client
COPY pom.xml /root/pom.xml
COPY package.json /root/package.json
COPY package-lock.json /root/package-lock.json
COPY index.html /root/index.html
RUN mvn -f /root/pom.xml clean package

FROM adoptopenjdk/openjdk11:ubuntu-jre
WORKDIR /root
COPY --from=build /root/scinote-server/target/scinote.jar ./scinote.jar
ENTRYPOINT ["java", "-jar", "-Dhttps.protocols=TLSv1,TLSv1.1,TLSv1.2", "-Duser.timezone=Europe/Warsaw", "-Xms1024m", "-Xmx1024m", "scinote.jar"]
