FROM gradle AS Build

WORKDIR /app

COPY . /app

RUN gradle build -x test

FROM openjdk:17

COPY --from=Build /app/build/libs/coinjoiner-0.0.1-SNAPSHOT.jar /app/coinjoiner.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/coinjoiner.jar", "--spring.profiles.active=prod"]