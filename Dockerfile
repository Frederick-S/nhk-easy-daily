FROM openjdk:8

WORKDIR /app
ADD . /app

RUN wget https://services.gradle.org/distributions/gradle-6.3-bin.zip
RUN unzip gradle-6.3-bin.zip
RUN gradle-6.3/bin/gradle bootJar

EXPOSE 8080

CMD ["java", "-jar", "build/libs/nhk-1.0-SNAPSHOT.jar"]
