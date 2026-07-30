# Admin System

基于 Spring Boot 3.5.16 的后台管理系统练手项目。当前重点已经从基础 CRUD 演进到企业后台常见能力：用户管理、JWT 登录、Spring Security 鉴权、RBAC 权限模型、Knife4j 接口文档、MyBatis-Plus 持久层能力。

## 技术栈

- JDK 17
- Spring Boot 3.5.16
- Spring Security
- Spring AOP
- JJWT 0.12.7
- MyBatis-Plus 3.5.5
- MySQL 8.x
- Knife4j Next 5.0.18
- MapStruct 1.5.5.Final
- Lombok

## 当前能力

- 用户登录：`POST /sys/login`
- JWT 签发与校验
- BCrypt 密码加密与校验
- Spring Security 无状态认证
- `@PreAuthorize` 接口级权限控制
- RBAC 五表权限模型
- 用户分页查询、新增、修改、逻辑删除、物理删除
- MyBatis-Plus 分页、逻辑删除、乐观锁、自动填充
- Knife4j / OpenAPI 文档
- Jakarta Validation 参数校验
- 统一响应码与全局异常处理
- DTO / VO / Entity 分层
- 操作日志注解与 AOP 入库

## 项目结构

```text
src/main/java/com/system
├── common                  通用响应、异常、常量
├── config                  Spring Security、MyBatis-Plus、跨域、Knife4j、Jackson 配置
├── config/prop             JWT 配置属性
├── controller              接口层
├── convert                 MapStruct 对象转换
├── dto                     请求入参对象
├── entity                  数据库实体
├── filter                  Spring Security JWT Filter
├── handler                 MyBatis-Plus 自动填充处理器
├── mapper                  MyBatis Mapper
├── security/handler        401 / 403 安全异常处理
├── service                 业务接口
├── service/impl            业务实现
├── util                    JWT 工具
└── vo                      响应视图对象
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
./mvnw -Dtest=PasswordEncoderTests test
```

生成指定明文的密文：

```bash
./mvnw -Dtest=PasswordEncoderTests -DrawPassword=123456 test
```

更新 admin 密码示例：

```sql
UPDATE sys_user
SET password = '这里替换为 BCrypt 密文'
WHERE username = 'admin';
```

BCrypt 每次生成的密文都不同，这是正常现象。只要 `matches("123456", 密文)` 能通过即可。

## 启动项目

开发环境默认激活 `dev`：

```bash
./mvnw spring-boot:run
```

切换 profile：

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=test
```

接口文档：

```text
http://localhost:8080/doc.html
http://localhost:8080/v3/api-docs
```

## 登录与鉴权测试

先获取验证码：

```bash
curl http://localhost:8080/captcha
```

返回的 `data.captchaImage` 可以直接作为图片展示，`data.captchaKey` 登录时回传。

登录：

```bash
curl -X POST http://localhost:8080/sys/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456","captchaKey":"这里替换为captchaKey","captchaCode":"图片里的验证码"}'
```

成功后返回的 `data` 是 JWT。

验证码当前使用内存保存，2 分钟过期，校验后立即失效。后续 Redis 阶段会迁移到 Redis。

访问受保护接口：

```bash
curl "http://localhost:8080/sys/user/search_list?status=1&pageNum=1&pageSize=10" \
  -H "Authorization: Bearer 这里替换为登录返回的token"
```

未带 token 时预期返回 401。带 token 但没有权限时预期返回 403。

## 用户接口与权限

| 接口 | 方法 | 权限 |
| --- | --- | --- |
| `/sys/user/{id}` | GET | `sys:user:detail` |
| `/sys/user/search_list` | GET | `sys:user:list` |
| `/sys/user/status` | PUT | `sys:user:status` |
| `/sys/user/reset-password` | PUT | `sys:user:resetPwd` |
| `/sys/user/change-password` | PUT | 登录用户 |
| `/sys/user/{id}/roles` | GET | `sys:user:assignRole` |
| `/sys/user/{id}/roles` | PUT | `sys:user:assignRole` |
| `/sys/user/add` | POST | `sys:user:add` |
| `/sys/user/modify` | POST | `sys:user:edit` |
| `/sys/user/delete/{id}` | DELETE | `sys:user:remove` |
| `/sys/user/delete_admin/{id}` | DELETE | `sys:user:physicalDel` |
| `/sys/role/search_list` | GET | `sys:role:list` |
| `/sys/role/add` | POST | `sys:role:add` |
| `/sys/role/modify` | POST | `sys:role:edit` |
| `/sys/role/status` | PUT | `sys:role:status` |
| `/sys/role/{id}/permissions` | GET | `sys:role:assignPermission` |
| `/sys/role/{id}/permissions` | PUT | `sys:role:assignPermission` |
| `/sys/role/delete/{id}` | DELETE | `sys:role:remove` |
| `/sys/profile` | GET | 登录用户 |
| `/sys/menu/tree` | GET | `sys:menu:list` |
| `/sys/menu/current` | GET | 登录用户 |
| `/sys/menu/add` | POST | `sys:menu:add` |
| `/sys/menu/modify` | POST | `sys:menu:edit` |
| `/sys/menu/delete/{id}` | DELETE | `sys:menu:remove` |

新增用户使用 `UserAddDTO`，需要传 `password`。查询返回使用 `UserPageVO`，不返回密码。

## DTO / VO 边界

- `LoginDTO`：登录入参，包含 `username/password/captchaKey/captchaCode`
- `UserAddDTO`：新增用户入参，包含 `password`
- `UserUpdateDTO`：修改用户入参，不直接修改密码
- `UserStatusDTO`：启用 / 禁用用户入参，只包含 `id/status`
- `UserResetPasswordDTO`：管理员重置用户密码入参，只包含 `id/newPassword`
- `UserChangePasswordDTO`：当前用户修改自己的密码入参，包含 `oldPassword/newPassword`
- `UserSearchDTO`：查询条件
- `RoleAssignPermissionDTO`：角色分配权限入参，空数组表示清空权限
- `ProfileVO`：当前登录用户信息，包含用户、角色、权限标识
- `MenuAddDTO`：新增目录、菜单、按钮权限入参
- `MenuUpdateDTO`：修改目录、菜单、按钮权限入参
- `MenuTreeVO`：菜单权限树返回对象，包含 `children`
- `UserPageVO`：用户分页返回对象，不包含密码
- `UserDetailVO`：用户详情返回对象，不包含密码、逻辑删除、版本号
- `SysUser`：数据库实体，不直接作为主要响应对象暴露

原则：入参用 DTO，出参用 VO，数据库映射用 Entity。

## 响应码约定

统一响应码定义在 `ResultCode`：

```text
200 操作成功
400 参数错误
401 未登录或登录已失效
403 没有访问该接口的权限
500 系统异常
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

- `PasswordEncoderTests`：生成并校验 BCrypt 密文
- `CaptchaControllerTest`：验证码接口响应
- `LoginControllerTest`：登录接口成功响应、参数校验
- `SysUserControllerTest`：用户详情响应不暴露敏感字段
- `SysRoleControllerTest`：角色分页、新增、修改、删除接口响应
- `SecurityHandlerTest`：未登录 401、无权限 403 返回
- `AdminSystemApplicationTests`：Spring Boot 上下文启动

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

说明 Spring Security 默认登录页还在生效。当前项目已经通过 `SecurityConfig` 关闭了 `formLogin` 和 `httpBasic`，并放行 `/sys/login`。

### 访问接口返回 401

通常是没有带 token，或者请求头格式不对。当前标准格式是：

```text
Authorization: Bearer token
```

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
