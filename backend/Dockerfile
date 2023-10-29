FROM gradle AS Build

WORKDIR /app

COPY . /app

RUN gradle build -x test

FROM openjdk:17

COPY --from=Build /app/build/libs/backend-0.0.1-SNAPSHOT.jar /app/backend.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/backend.jar", "--spring.profiles.active=prod"]