# 系统业务服务运行镜像。先在 system-service 目录执行 Maven 打包，再构建镜像。
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/system-service-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
