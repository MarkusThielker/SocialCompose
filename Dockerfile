# The shadow jar has to be build before executing to reduce build times!

# Container setup --------
FROM openjdk:8-jre-alpine

EXPOSE "8080:8080"
EXPOSE "8443:8443"

# Copying needed files
COPY /server/build/libs/*all.jar /app/social_compose_server.jar
COPY /server/src/main/resources/ /resources/

# Entrypoint definition
ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/social_compose_server.jar"]
# End Container setup --------
