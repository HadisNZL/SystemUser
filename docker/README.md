# Docker 本地编排说明

本目录只保存配置、初始化脚本和备份文件，不保存真实数据。

真实数据由 Docker named volume 管理：

```text
admin_mysql_data
admin_redis_data
admin_rabbitmq_data
admin_app_upload
```

目录说明：

```text
mysql/conf/      MySQL配置，可提交Git
mysql/init/      MySQL首次初始化SQL，可提交Git
mysql/backup/    MySQL备份目录，真实备份不提交Git
redis/redis.conf Redis配置，可提交Git
```

只启动中间件：

```bash
docker compose up -d mysql redis rabbitmq
./scripts/app.sh middleware
```

启动后端应用和中间件：

```bash
./mvnw clean package -DskipTests
docker compose up -d --build
./scripts/app.sh start
```

停止：

```bash
docker compose stop
```
