FROM java:8

EXPOSE 8080

CMD ["java", "-jar", "twilio-2.0-SNAPSHOT.jar"]

WORKDIR /app

ADD . ./
