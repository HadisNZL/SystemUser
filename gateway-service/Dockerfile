# 网关服务运行镜像。先在 gateway-service 目录执行 Maven 打包，再构建镜像。
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/gateway-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 9000

ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
