FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8400

ENTRYPOINT ["java","-jar","app.jar"]