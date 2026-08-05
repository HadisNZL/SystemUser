# 文件服务运行镜像。先通过根聚合工程完成打包。
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/file-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 9401

ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
