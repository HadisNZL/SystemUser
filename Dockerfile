# 后端应用运行镜像。先执行 ./mvnw clean package -DskipTests，再构建镜像。
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/admin-system-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
