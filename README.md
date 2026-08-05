# Admin System

基于 Spring Boot 3.5.16 的后台管理微服务项目，包含统一网关、认证、系统管理、文件和操作日志服务。

## 技术栈

- JDK 17
- Spring Boot 3.5.16
- Spring Cloud 2025.0.0
- Spring Cloud Alibaba 2025.0.0.0
- Sentinel 1.8.9
- Spring Security
- Nacos 注册中心与配置中心
- OpenFeign 服务调用
- Springdoc OpenAPI 2.8.17
- Knife4j Gateway 5.2.1
- Spring AOP
- JJWT 0.12.7
- MyBatis-Plus 3.5.5
- MySQL 8.x
- Redis 7
- RabbitMQ 3
- Docker Compose
- Lombok

## 当前能力

- 用户登录：`POST /auth/login`
- JWT 签发与校验
- BCrypt 密码加密与校验
- Spring Security 无状态认证
- `@PreAuthorize` 接口级权限控制
- RBAC 五表权限模型
- 用户分页查询、新增、修改、逻辑删除、物理删除
- `system-service` 承接用户、角色、菜单、字典和当前用户资料
- `file-service` 独立承接文件上传、下载和元数据管理
- MyBatis-Plus 分页、逻辑删除、乐观锁、自动填充
- Jakarta Validation 参数校验
- 统一响应码与全局异常处理
- DTO / VO / Entity 分层
- `system-service` 操作日志采集与 `log-service` 异步入库
- 本地文件上传下载
- 用户 Excel 导入导出
- 字典配置管理
- Redis 验证码缓存
- Redis 接口限流
- RabbitMQ 操作日志消息队列
- Spring Task 定时清理历史操作日志
- 网关统一聚合 OpenAPI 接口文档
- Sentinel 核心接口 QPS 限流
- OpenFeign 调用降级与网关异常统一响应

## 项目结构

```text
admin-common/         公共响应、异常和服务间消息模型
admin-operation-log/ 操作日志采集公共模块
auth-service/         登录、验证码、JWT、退出登录
system-service/       用户、角色、菜单、字典和当前用户资料
file-service/         文件上传、下载和元数据管理
log-service/          操作日志消费、入库和定时清理
gateway-service/      统一入口、路由和 JWT 校验
pom.xml               Maven 聚合父工程
```

运行时调用关系：

```text
前端 -> gateway-service:9000
  |-- /auth/**   -> auth-service:9101 -> OpenFeign -> system-service
  |-- /system/** -> system-service:9201
  |-- /file/**   -> file-service:9401 -> OpenFeign -> system-service
  `-- /log/**    -> log-service:9301  -> OpenFeign -> system-service
```

## 数据库

数据库关系和初始化数据见：

```text
00-数据库表结构文档.md
```

当前 RBAC 核心链路：

```text
sys_user
  -> sys_user_role
  -> sys_role
  -> sys_role_permission
  -> sys_permission.permission_key
  -> Spring Security GrantedAuthority
```

admin 用户要访问用户管理接口，至少需要这些权限：

```text
sys:user:list
sys:user:detail
sys:user:status
sys:user:resetPwd
sys:user:add
sys:user:edit
sys:user:remove
sys:user:physicalDel
sys:user:assignRole
```

角色管理接口需要这些权限：

```text
sys:role:list
sys:role:add
sys:role:edit
sys:role:status
sys:role:remove
sys:role:assignPermission
```

菜单权限管理接口需要这些权限：

```text
sys:menu:list
sys:menu:add
sys:menu:edit
sys:menu:remove
```

## 密码说明

数据库中的 `sys_user.password` 必须存 BCrypt 密文，不能存明文 `123456`。

生成密文：

```bash
./mvnw -pl auth-service -am -Dtest=PasswordEncoderTests -Dsurefire.failIfNoSpecifiedTests=false test
```

生成指定明文的密文：

```bash
./mvnw -pl auth-service -am -Dtest=PasswordEncoderTests -Dsurefire.failIfNoSpecifiedTests=false -DrawPassword=123456 test
```

更新 admin 密码示例：

```sql
UPDATE sys_user
SET password = '这里替换为 BCrypt 密文'
WHERE username = 'admin';
```

BCrypt 每次生成的密文都不同，这是正常现象。只要 `matches("123456", 密文)` 能通过即可。

## 启动项目

开发环境默认激活 `dev`，根工程是聚合工程，不再直接启动单体应用。

开发环境依赖 MySQL、Redis、RabbitMQ 和 Nacos。项目根目录已提供 Docker Compose 编排，也支持把后端应用一起容器化运行：

```text
docker-compose.yml
admin-common/
admin-operation-log/
auth-service/
system-service/
file-service/
log-service/
gateway-service/
docker/mysql/conf/my.cnf
docker/mysql/init/
docker/mysql/backup/
docker/redis/redis.conf
```

如果本机没有 Docker，先安装 Docker Desktop，并确认：

```bash
docker --version
docker compose version
```

本地开发时，常用方式是只启动中间件，后端仍用 IDEA 或 Maven 启动：

```bash
docker compose up -d mysql redis rabbitmq nacos
```

也可以使用脚本：

```bash
./scripts/dev.sh start
./scripts/dev.sh status
./scripts/dev.sh restart
./scripts/dev.sh stop
./scripts/dev.sh logs
```

完整 Docker 方式启动后端和中间件：

```bash
./mvnw clean package -DskipTests
docker compose up -d --build
```

也可以使用脚本：

```bash
./scripts/app.sh start
```

`dev.sh` 会在宿主机启动 Java 服务，`app.sh` 会在 Docker 中启动 Java 服务。两种模式占用相同端口，不要同时运行。

完整 Docker 方式使用 `docker` profile，容器内连接地址使用 Compose 服务名：

```text
MySQL:    mysql:3306
Redis:    redis:6379
RabbitMQ: rabbitmq:5672
Nacos:    nacos:8848
```

宿主机访问端口：

```text
网关入口: http://localhost:9000
认证服务: http://localhost:9101
系统服务: http://localhost:9201
日志服务: http://localhost:9301
文件服务: http://localhost:9401
MySQL:    127.0.0.1:3306 root / 123456
Redis:    127.0.0.1:6379
RabbitMQ: 127.0.0.1:5672 guest / guest
Nacos:    http://localhost:8848/nacos
```

RabbitMQ 管理页面：

```text
http://localhost:15672
```

Nacos 控制台：

```text
http://localhost:8848/nacos
```

统一接口文档：

```text
http://localhost:9000/doc.html
```

文档按认证、系统、文件和日志服务分组。各业务服务只生成 `/v3/api-docs`，Knife4j 页面由网关统一提供；`prod` 环境默认关闭文档。

Knife4j 首页会提示四个服务分组。通过左上角下拉框切换服务后，左侧再按 Controller 的 `@Tag` 展示业务模块。

认证服务启动后，可以在 Nacos 控制台的 `服务管理 -> 服务列表` 中看到 `auth-service`。
系统服务启动后，可以在 Nacos 控制台的 `服务管理 -> 服务列表` 中看到 `system-service`。
日志服务启动后，可以在 Nacos 控制台的 `服务管理 -> 服务列表` 中看到 `log-service`。
文件服务启动后，可以在 Nacos 控制台的 `服务管理 -> 服务列表` 中看到 `file-service`。
网关启动后，可以在 Nacos 控制台的 `服务管理 -> 服务列表` 中看到 `gateway-service`。

网关路由验证：

```text
POST http://localhost:9000/auth/login
GET  http://localhost:9000/system/user/search_list?status=1&pageNum=1&pageSize=10
GET  http://localhost:9000/system/user/1
GET  http://localhost:9000/system/demo/ping
POST http://localhost:9000/file/upload
GET  http://localhost:9000/file/download/{id}
GET  http://localhost:9000/log/operation/search_list?pageNum=1&pageSize=10
```

登录请求进入 `auth-service`，`/system/**` 进入 `system-service`，`/file/**` 进入 `file-service`，`/log/**` 进入 `log-service`。
`/system/user/search_list` 需要先登录，再带 `Authorization: Bearer ...`。

调用链路：

```text
登录：gateway-service -> auth-service -> OpenFeign -> system-service
gateway-service -> system-service
gateway-service -> file-service -> OpenFeign -> system-service
```

Gateway 访问 auth-service 的方式：

```text
1. auth-service 启动后，以服务名 auth-service 注册到 Nacos
2. gateway-service 配置 /auth/** 路由，uri 使用 lb://auth-service
3. lb:// 表示通过服务发现和负载均衡找服务，不写死 localhost:9101
4. 请求 http://localhost:9000/auth/login 会被转发到 auth-service
5. system-service 启动后，以服务名 system-service 注册到 Nacos
6. gateway-service 配置 /system/** 路由，uri 使用 lb://system-service
7. 请求 http://localhost:9000/system/demo/ping 会被转发到 system-service
```

网关统一能力：

```text
统一跨域：前端请求统一访问 9000，由 gateway-service 处理 CORS
请求日志：每个经过网关的请求都会记录 method/path/status/cost/clientIp
统一鉴权：非白名单请求必须携带有效 Authorization
身份透传：网关校验 token 后透传 X-Gateway-Forwarded / X-User-Id
```

本地查看网关日志：

```bash
./scripts/dev.sh logs
```

网关配置中心本地开发使用的 Data ID：

```text
gateway-service-dev.yaml
```

示例配置：

```yaml
spring:
  main:
    banner-mode: console
```

应用健康检查：

```text
http://localhost:9000/actuator/health
```

正常响应示例：

```json
{"status":"UP"}
```

Sentinel 已接入五个应用服务，网关默认保护以下接口：

```text
POST /auth/login                    5 QPS
GET  /system/user/search_list      20 QPS
POST /file/upload                   3 QPS
```

阈值可通过 `SENTINEL_LOGIN_QPS`、`SENTINEL_USER_SEARCH_QPS`、`SENTINEL_FILE_UPLOAD_QPS` 调整。限流返回 HTTP 429；Feign 下游不可用或网关无法找到服务实例时返回 HTTP 503。Sentinel 状态可通过各服务 `/actuator/sentinel` 查看，日志目录可用 `SENTINEL_LOG_DIR` 调整。

Docker Compose 使用具名卷保存真实数据：

```text
admin_mysql_data
admin_redis_data
admin_rabbitmq_data
admin_app_upload
admin_nacos_data
```

配置、初始化 SQL、备份目录放在项目 `docker/` 目录，真实数据不放项目目录。

### 从 brew MySQL / Redis 迁移到 Docker

先备份当前 brew MySQL 数据：

```bash
mkdir -p ~/admin-system-backup
mysqldump -u root -p --single-transaction --set-gtid-purged=OFF admin_system > ~/admin-system-backup/admin_system.sql
```

确认备份文件非空：

```bash
ls -lh ~/admin-system-backup/admin_system.sql
head -n 20 ~/admin-system-backup/admin_system.sql
```

停止 brew 服务，释放端口：

```bash
brew services stop mysql
brew services stop redis
```

启动 Docker 中间件：

```bash
docker compose up -d mysql redis rabbitmq nacos
```

创建数据库：

```bash
docker exec -it admin-mysql mysql -uroot -p123456
```

进入 MySQL 后执行：

```sql
CREATE DATABASE admin_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
EXIT;
```

导入备份：

```bash
docker exec -i admin-mysql mysql -uroot -p123456 admin_system < ~/admin-system-backup/admin_system.sql
```

验证数据：

```bash
docker exec -it admin-mysql mysql -uroot -p123456 -e "USE admin_system; SHOW TABLES;"
```

确认项目启动和接口测试都正常后，再卸载 brew 版本：

```bash
brew uninstall mysql
brew uninstall redis
```

常用 Docker Compose 命令：

```bash
docker compose ps
docker compose logs -f
docker compose logs -f file-service
docker compose stop
docker compose start
docker compose down
```

不要随手执行 `docker compose down -v`，它会删除具名卷里的 MySQL / Redis / RabbitMQ 数据。

常用脚本命令：

```bash
./scripts/app.sh middleware
./scripts/app.sh build
./scripts/app.sh start
./scripts/app.sh stop
./scripts/app.sh restart
./scripts/app.sh logs
./scripts/app.sh ps
./scripts/dev.sh start
./scripts/dev.sh stop
./scripts/dev.sh logs
./scripts/dev.sh status
```

`app.sh` 偏 Docker Compose 部署，`dev.sh` 偏本地开发。`dev.sh start` 会启动中间件和五个微服务。

切换 profile：

```bash
./mvnw -f system-service/pom.xml spring-boot:run -Dspring-boot.run.profiles=test
```

## 登录与鉴权测试

先获取验证码：

```bash
curl http://localhost:9000/auth/captcha
```

返回的 `data.captchaImage` 可以直接作为图片展示，`data.captchaKey` 登录时回传。

登录：

```bash
curl -X POST http://localhost:9000/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456","captchaKey":"这里替换为captchaKey","captchaCode":"图片里的验证码"}'
```

成功后返回的 `data` 是 JWT。验证码和登录都走 `gateway-service -> auth-service`。

验证码使用 Redis 保存，key 前缀为 `captcha:`，2 分钟过期，校验后立即删除。

用户权限标识也使用 Redis 缓存，key 前缀为 `user:permissions:`，30 分钟过期。用户分配角色、角色分配权限、角色状态变化、菜单权限变化时会清理权限缓存。

退出登录会把当前 JWT 的摘要写入 Redis 黑名单，key 前缀为 `token:blacklist:`，过期时间等于 token 剩余有效期。退出后旧 token 会被拦截，需要重新登录。

接口限流使用 Redis 计数，key 前缀为 `rate_limit:`，当前限制：

```text
/auth/login     每个 IP 60 秒最多 5 次
/auth/captcha   每个 IP 60 秒最多 10 次
```

操作日志使用 RabbitMQ 异步入库，交换机、队列、路由键：

```text
exchange: admin.operation.log.exchange
queue:    admin.operation.log.queue
routing:  admin.operation.log
```

如果 RabbitMQ 未启动，主业务接口不受影响，但操作日志消息发送失败，不会写入 `sys_operation_log`。

当前链路：

```text
system-service 写操作
  -> @OperationLog + AOP 采集
  -> RabbitMQ
  -> log-service 消费
  -> sys_operation_log
```

操作日志查询通过网关访问：

```text
前端 -> gateway-service -> log-service
  -> OpenFeign 查询 system-service 中的用户状态和权限
  -> @PreAuthorize 校验 sys:log:list
  -> sys_operation_log 分页查询
```

查询接口支持 `module`、`operation`、`operatorId`、`status`、`startTime`、`endTime` 条件，时间格式为 `yyyy-MM-dd HH:mm:ss`。

操作日志定时清理由 `log-service` 使用 Spring Task 执行，默认保留 90 天，每天凌晨 3 点执行。配置项位于 `log-service`：

```yaml
system:
  operation-log:
    retention-days: 90
    clean-cron: "0 0 3 * * ?"
```

访问受保护接口：

```bash
curl "http://localhost:9000/system/user/search_list?status=1&pageNum=1&pageSize=10" \
  -H "Authorization: Bearer 这里替换为登录返回的token"
```

未带 token 时预期返回 401。带 token 但没有权限时预期返回 403。

## 用户接口与权限

| 接口 | 方法 | 权限 |
| --- | --- | --- |
| `/auth/logout` | POST | 登录用户 |
| `/system/user/{id}` | GET | `sys:user:detail` |
| `/system/user/search_list` | GET | `sys:user:list` |
| `/system/user/status` | PUT | `sys:user:status` |
| `/system/user/reset-password` | PUT | `sys:user:resetPwd` |
| `/system/user/change-password` | PUT | 登录用户 |
| `/system/user/{id}/roles` | GET | `sys:user:assignRole` |
| `/system/user/{id}/roles` | PUT | `sys:user:assignRole` |
| `/system/user/add` | POST | `sys:user:add` |
| `/system/user/modify` | POST | `sys:user:edit` |
| `/system/user/delete/{id}` | DELETE | `sys:user:remove` |
| `/system/user/delete_admin/{id}` | DELETE | `sys:user:physicalDel` |
| `/system/user/export` | GET | `sys:user:export` |
| `/log/operation/search_list` | GET | `sys:log:list` |
| `/system/user/import-template` | GET | `sys:user:import` |
| `/system/user/import` | POST | `sys:user:import` |
| `/system/role/search_list` | GET | `sys:role:list` |
| `/system/role/add` | POST | `sys:role:add` |
| `/system/role/modify` | POST | `sys:role:edit` |
| `/system/role/status` | PUT | `sys:role:status` |
| `/system/role/{id}/permissions` | GET | `sys:role:assignPermission` |
| `/system/role/{id}/permissions` | PUT | `sys:role:assignPermission` |
| `/system/role/delete/{id}` | DELETE | `sys:role:remove` |
| `/system/profile` | GET | 登录用户 |
| `/system/menu/tree` | GET | `sys:menu:list` |
| `/system/menu/current` | GET | 登录用户 |
| `/system/menu/add` | POST | `sys:menu:add` |
| `/system/menu/modify` | POST | `sys:menu:edit` |
| `/system/menu/delete/{id}` | DELETE | `sys:menu:remove` |
| `/system/dict/type/search_list` | GET | `sys:dict:type:list` |
| `/system/dict/type/add` | POST | `sys:dict:type:add` |
| `/system/dict/type/modify` | POST | `sys:dict:type:edit` |
| `/system/dict/type/delete/{id}` | DELETE | `sys:dict:type:remove` |
| `/system/dict/data/search_list` | GET | `sys:dict:data:list` |
| `/system/dict/data/type/{dictType}` | GET | 登录用户 |
| `/system/dict/data/add` | POST | `sys:dict:data:add` |
| `/system/dict/data/modify` | POST | `sys:dict:data:edit` |
| `/system/dict/data/delete/{id}` | DELETE | `sys:dict:data:remove` |
| `/file/upload` | POST | 登录用户 |
| `/file/download/{id}` | GET | 登录用户 |

用户管理接口位于 `system-service` 的 `/system/user/**`。新增用户使用 `UserAddDTO`，需要传 `password`。查询返回使用 `UserPageVO`，不返回密码。

文件上传使用 `multipart/form-data`，字段名为 `file`。文件元数据保存到 `sys_file`，文件本体默认保存到 `./upload`，最大 10MB。

用户 Excel 导入使用 `multipart/form-data`，字段名为 `file`。模板列为：账号、密码、昵称、手机号、邮箱、状态。状态可填 `1/正常` 或 `0/禁用`，为空默认正常。

字典配置分为字典类型和字典数据。前端下拉框通过 `/system/dict/data/type/{dictType}` 获取启用的字典数据。

## DTO / VO 边界

- `AuthLoginDTO`：登录入参，包含 `username/password/captchaKey/captchaCode`
- `UserAddDTO`：新增用户入参，包含 `password`
- `UserUpdateDTO`：修改用户入参，不直接修改密码
- `UserStatusDTO`：启用 / 禁用用户入参，只包含 `id/status`
- `UserResetPasswordDTO`：管理员重置用户密码入参，只包含 `id/newPassword`
- `UserChangePasswordDTO`：当前用户修改自己的密码入参，包含 `oldPassword/newPassword`
- `UserSearchDTO`：查询条件
- `UserExcelDTO`：用户 Excel 行数据
- `DictTypeAddDTO / DictTypeUpdateDTO / DictTypeSearchDTO`：字典类型入参
- `DictDataAddDTO / DictDataUpdateDTO / DictDataSearchDTO`：字典数据入参
- `RoleAssignPermissionDTO`：角色分配权限入参，空数组表示清空权限
- `ProfileVO`：当前登录用户信息，包含用户、角色、权限标识
- `MenuAddDTO`：新增目录、菜单、按钮权限入参
- `MenuUpdateDTO`：修改目录、菜单、按钮权限入参
- `MenuTreeVO`：菜单权限树返回对象，包含 `children`
- `FileUploadVO`：文件上传返回对象，包含文件ID和下载地址
- `UserImportResultVO`：用户导入结果，包含成功数量和失败明细
- `DictTypeVO`：字典类型返回对象
- `DictDataVO`：字典数据返回对象
- `UserPageVO`：用户分页返回对象，不包含密码
- `UserDetailVO`：用户详情返回对象，不包含密码、逻辑删除、版本号
- `SysUser`：数据库实体，不直接作为主要响应对象暴露

原则：入参用 DTO，出参用 VO，数据库映射用 Entity。

雪花 ID 使用 `Long` 存储，接口响应中的 ID 字段序列化为字符串，避免前端 JavaScript 数字精度丢失；`total`、`fileSize` 等统计类字段仍保持数字。

## 响应码约定

统一响应码定义在 `ResultCode`：

```text
200 操作成功
400 参数错误
401 未登录或登录已失效
403 没有访问该接口的权限
429 请求过于频繁
500 系统异常
503 服务暂时不可用
10001+ 业务错误
```

## 构建命令

只编译：

```bash
./mvnw clean compile
```

运行测试：

```bash
./mvnw test
```

完整打包：

```bash
./mvnw clean package
```

当前测试包含：

- `auth-service/PasswordEncoderTests`：生成并校验 BCrypt 密文
- `AuthLoginControllerTest`：认证接口成功响应、参数校验
- `AuthCaptchaServiceImplTest`：验证码生成和 Redis 写入
- `AuthLogoutServiceImplTest`：退出登录写入 Token 黑名单
- `SystemUserControllerTest`：系统服务用户接口响应不暴露敏感字段
- `SysRoleControllerTest`：角色分页、新增、修改、删除接口响应
- `file-service/SysFileControllerTest`：文件上传下载接口
- `file-service/SysFileServiceImplTest`：文件校验、磁盘存储和元数据保存
- `admin-operation-log/OperationLogMessageProducerTest`：操作日志消息发送
- `log-service/OperationLogMessageConsumerTest`：操作日志消息消费入库
- `log-service/OperationLogCleanServiceImplTest`：历史操作日志清理
- `log-service/OperationLogCleanTaskTest`：定时清理任务触发
- `log-service/OperationLogControllerTest`：操作日志分页接口与权限校验
- `log-service/OperationLogQueryServiceImplTest`：操作日志条件查询与分页转换
- `log-service/GatewayForwardAuthenticationFilterTest`：网关身份恢复与用户权限加载
- `GatewaySentinelConfigTest`：网关核心接口限流规则
- `GatewayExceptionHandlerTest`：网关 503 统一响应
- 各微服务 ApplicationTests：Spring Boot 上下文启动

## 常见问题

### 登录提示账号或密码错误

优先确认数据库密码是否是 BCrypt 密文，而不是明文 `123456`。

```sql
SELECT username, password, LENGTH(password), status
FROM sys_user
WHERE username = 'admin';
```

正常 BCrypt 密文长度通常是 60，用户状态 `status` 应为 1。

### 访问接口跳转到 /login

说明 Spring Security 默认登录页还在生效。当前项目已经通过 `SecurityConfig` 关闭了 `formLogin` 和 `httpBasic`，并放行登录相关入口。

### 访问接口返回 401

通常是没有带 token，或者请求头格式不对。当前标准格式是：

```text
Authorization: Bearer token
```

业务接口应通过 `http://localhost:9000` 访问。直接请求 `9201/9301/9401` 不会携带网关生成的可信身份头，受保护接口会返回 401。

Knife4j 中可以使用全局 Header `Authorization: Bearer token`，也可以在 `bearerAuth` 授权框只填写 token；不要同时配置，也不要形成 `Bearer Bearer token`。

### 访问接口返回 403

说明登录成功，但当前用户没有接口所需权限。检查：

```text
sys_user_role
sys_role_permission
sys_permission.permission_key
```

### Mapper 权限查询报 Invalid bound statement

检查 `SysPermissionMapper.xml` 的 namespace 是否是：

```xml
com.system.mapper.SysPermissionMapper
```

## 相关文档

- `00-数据库表结构文档.md`：数据库 DDL、RBAC 表关系、初始化数据
- `01-项目开发说明.md`：认证鉴权流程、开发约定、测试流程、排错清单
- `02-企业级后端学习规划.md`：阶段目标和项目进度
- `03-微服务部署运维手册.md`：微服务部署、日志、健康检查和备份恢复
- `04-微服务拆分设计.md`：单体平滑演进到微服务的服务边界和拆分顺序
