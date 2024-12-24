# Use a base image with Java 17
FROM openjdk:17

WORKDIR .

# Copy the JAR package into the image
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# Expose the application port
EXPOSE 7024

# Run the App
ENTRYPOINT ["java", "-jar", "/app.jar"]