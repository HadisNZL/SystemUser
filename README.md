# Admin System

一个基于 **Spring Boot 3.5.16 + MyBatis-Plus** 的后台管理系统练手项目，当前已完成用户列表、分页、条件查询、新增、删除，以及统一返回、全局异常、跨域、时间格式化、逻辑删除、自动填充等基础能力。

---

## 1. 技术栈

- **Spring Boot 3.5.16** - 核心框架
- **MyBatis-Plus 3.5.5** - 持久层框架，简化 CRUD 与分页开发
- **MySQL 8.x** - 当前使用数据库
- **Spring Validation** - 参数校验依赖
- **Jackson** - JSON 序列化 / 反序列化
- **Lombok** - 简化实体类与 DTO/VO 代码
- **HikariCP** - 数据库连接池
- **JDK 17** - 开发环境

---

## 2. 你当前已经增加的内容总结

相比最初版本，目前项目已经补充了下面这些能力：

### 基础接口能力
- ✅ 用户列表查询
- ✅ 用户分页查询
- ✅ 用户条件分页查询
- ✅ 新增用户
- ✅ 删除用户

### 数据访问能力
- ✅ MyBatis-Plus 基础 CRUD
- ✅ MyBatis-Plus 分页插件
- ✅ 逻辑删除配置
- ✅ 雪花算法主键 ID
- ✅ 创建时间、更新时间、删除标记自动填充

### 通用基础设施
- ✅ 统一响应结果封装 `Result<T>`
- ✅ 统一分页结果封装 `PageResult<T>`
- ✅ 全局异常处理 `GlobalExceptionHandler`
- ✅ 全局跨域配置 `CorsConfig`
- ✅ 全局时间格式化配置 `JacksonConfig`

### 工程配置能力
- ✅ 多环境配置拆分：`dev / test / prod`
- ✅ 开发、测试、生产环境日志区分
- ✅ 开发环境默认激活
- ✅ MyBatis-Plus SQL 日志输出

### 分层与对象设计
- ✅ `entity` 实体类
- ✅ `dto` 入参对象
- ✅ `vo` 出参对象（已创建，后续可逐步替代直接返回实体）
- ✅ `common` 通用返回与异常类
- ✅ `handler` 自动填充处理器

---

## 3. 项目结构

当前项目 `com.system` 包下结构如下：

```text
com.system
├── AdminSystemApplication        启动类
├── config                        所有配置类：跨域、MP、时间序列化
├── controller                    控制层接口
├── service                       业务接口层
│   └── iml                       业务实现层（当前代码目录名为 iml）
├── mapper                        数据持久层
├── entity                        数据库实体类
├── dto                           入参传输对象
├── vo                            出参视图对象
├── common                        公共返回类、异常类
├── handler                       MyBatis-Plus 自动填充处理器
```

> 说明：
>
> 1. 你计划中的 `util`、`config.prop` 这两个包目前代码里还没有正式创建。
> 2. 你已经把项目往比较标准的企业分层方向推进了，后面如果继续扩展，补上 `util`、`constant`、`config.prop` 会更完整。

### 对应源码目录

```text
src/main/java/com/system/
├── AdminSystemApplication.java
├── common/
│   ├── GlobalExceptionHandler.java
│   ├── PageResult.java
│   └── Result.java
├── config/
│   ├── CorsConfig.java
│   ├── JacksonConfig.java
│   └── MyBatisPlusConfig.java
├── controller/
│   └── SysUserController.java
├── dto/
│   └── UserSearchDTO.java
├── entity/
│   └── SysUser.java
├── handler/
│   └── MyMetaObjectHandler.java
├── mapper/
│   └── SysUserMapper.java
├── service/
│   ├── SysUserService.java
│   └── iml/
│       └── SysUserServiceImpl.java
└── vo/
    └── UserPageVO.java
```

资源配置目录：

```text
src/main/resources/
├── application.yaml
├── application-dev.yaml
├── application-test.yaml
└── application-prod.yaml
```

---

## 4. 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- IntelliJ IDEA / VS Code 等 Java IDE

### 创建数据库

```sql
CREATE DATABASE admin_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 创建用户表

```sql
USE admin_system;

CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_flag` tinyint DEFAULT '0' COMMENT '删除标记：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';
```

### 初始化测试数据

```sql
INSERT INTO sys_user (username, password, nickname, phone, email) VALUES
('admin', '123456', 'Super管理员', '18510044400', 'niu_zilin@163.com'),
('test', '123456', '测试用户', '18932550885', '');
```

### 启动项目

```bash
./mvnw spring-boot:run
```

或：

```bash
mvn spring-boot:run
```

---

## 5. 多环境配置说明

项目已经拆分为多环境配置：

- `application.yaml`：主配置，负责激活环境与公共配置
- `application-dev.yaml`：开发环境
- `application-test.yaml`：测试环境
- `application-prod.yaml`：生产环境

当前默认激活：

```yaml
spring:
  profiles:
    active: dev
```

### 环境切换方式

例如切到测试环境：

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=test
```

---

## 6. API 接口文档

接口统一前缀：

```text
/sys/user
```

### 6.1 查询用户列表

```http
GET /sys/user/list
```

### 6.2 分页查询用户

```http
GET /sys/user/page?pageNum=1&pageSize=5
```

请求参数：
- `pageNum`：页码，默认 `1`
- `pageSize`：每页条数，默认 `5`

### 6.3 条件分页查询用户

```http
GET /sys/user/search_list?username=test&status=1&pageNum=1&pageSize=10
```

支持条件：
- `username`：用户名模糊查询
- `status`：状态精准查询
- `startTime`：开始时间
- `endTime`：结束时间
- `pageNum`：页码
- `pageSize`：每页条数

对应 DTO：

```java
public class UserSearchDTO {
    private String username;
    private Integer status;
    private String startTime;
    private String endTime;
}
```

### 6.4 新增用户

```http
POST /sys/user/add
Content-Type: application/json

{
  "username": "tom",
  "nickname": "汤姆",
  "phone": "13800138000",
  "email": "tom@qq.com"
}
```

说明：
- 如果 `password` 为空，系统默认补 `123456`
- 如果 `status` 为空，系统默认补 `1`

### 6.5 删除用户

```http
DELETE /sys/user/delete/{id}
```

当前实现为：
- **调用 `deleteById(id)` 删除**
- 但项目实体与全局配置里已经接入了 **逻辑删除能力**

---

## 7. 统一返回格式

项目接口统一返回 `Result<T>`：

```json
{
  "code": 200,
  "msg": "操作成功",
  "isSuccess": true,
  "data": {}
}
```

字段说明：
- `code`：状态码
- `msg`：消息说明
- `isSuccess`：是否成功
- `data`：实际返回数据

分页结果统一封装为：

```json
{
  "total": 100,
  "list": []
}
```

---

## 8. 关键配置说明

### 8.1 MyBatis-Plus 分页插件

```java
@Configuration
public class MyBatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
```

### 8.2 逻辑删除配置

`application.yaml` 中已配置：

```yaml
mybatis-plus:
  global-config:
    db-config:
      logic-delete-field: deleteFlag
      logic-not-delete-value: 0
      logic-delete-value: 1
```

实体类中也已配置：

```java
@TableLogic
private Integer deleteFlag;
```

### 8.3 自动填充配置

`MyMetaObjectHandler` 已支持：
- 插入时自动填充 `createTime`
- 插入时自动填充 `updateTime`
- 插入时自动填充 `deleteFlag`
- 更新时自动刷新 `updateTime`

### 8.4 时间格式统一

`JacksonConfig` 已统一 `LocalDateTime` 序列化与反序列化格式：

```text
yyyy-MM-dd HH:mm:ss
```

### 8.5 跨域配置

项目已通过 `CorsConfig` 统一开放跨域，便于前后端分离联调。

---

## 9. 当前代码设计特点

### 9.1 实体层

`SysUser` 已具备：
- 雪花算法主键：`IdType.ASSIGN_ID`
- 逻辑删除：`@TableLogic`
- 创建 / 更新时间自动填充：`@TableField(fill = ...)`

### 9.2 DTO / VO 分离趋势

目前你已经开始往更规范的接口设计走：

- `UserSearchDTO`：负责接收查询参数
- `UserPageVO`：负责定义页面输出对象

虽然当前分页接口还主要返回 `SysUser`，但 `VO` 已经建好，后续可以逐步改成“返回 VO，不直接暴露实体”。这属于很好的演进方向。

### 9.3 查询写法

你已经使用了 `LambdaQueryWrapper` 做动态条件拼接，支持：
- `like` 模糊查询
- `eq` 精准查询
- `ge / le` 时间区间查询
- `orderByDesc` 倒序排序

这说明项目已经从“能查数据”进化到了“可扩展条件查询”的阶段。

---

## 10. 以后切换数据库是否方便？

### 结论

**相对比较好切换，但目前还不是“零成本切换”。**

### 为什么说“比较好切换”

你当前项目有几个点对数据库切换是友好的：

1. **业务层和数据库访问层已经分开**  
   Controller → Service → Mapper 分层清晰，不会把 SQL 写得到处都是。

2. **使用的是 MyBatis-Plus**  
   大部分基础 CRUD、分页、条件查询都通过 MP 提供的能力实现，不是手写大量原生 SQL。

3. **配置集中在 application*.yaml**  
   数据源、驱动、日志配置都比较集中，未来替换更容易定位。

4. **目前没有 XML 大量手写 SQL**  
   这对切换数据库是好事，改造成本会小一些。

### 为什么说“还不是零成本”

当前代码里仍有一些地方和 MySQL 绑定较紧：

1. **驱动固定写死为 MySQL**  
   `application*.yaml` 里现在使用的是 MySQL 驱动和 URL。

2. **分页插件写死了 `DbType.MYSQL`**  
   `MyBatisPlusConfig` 当前明确指定了 MySQL。

3. **数据库建表语句偏 MySQL 风格**  
   比如自增、字符集、引擎这些语法换 PostgreSQL / Oracle 时要调整。

4. **时间查询参数现在是字符串**  
   虽然能用，但如果以后切换数据库，不同数据库对字符串到时间的隐式转换兼容性可能不同。

### 如果以后要换库，主要改哪些地方

未来从 MySQL 切到 PostgreSQL / Oracle / SQL Server，大概率要改：

- 数据源驱动
- JDBC URL
- MyBatis-Plus 的 `DbType`
- 建表 DDL
- 个别数据库方言相关 SQL / 时间处理方式
- 主键生成策略是否继续沿用当前方案

### 综合评价

如果按 10 分来评估你的项目数据库可切换性：

- **当前大概在 7/10 左右**

意思是：
- **比把 SQL 写死在各处的项目好很多**
- **但还没有抽象到真正“切换配置就完事”的程度**

对你现在这个阶段来说，这个结构已经是一个不错的起点了。

---

## 11. 后续建议方向

你后面可以继续往这些方向演进：

- [ ] 用户修改接口
- [ ] 按 ID 查询用户详情
- [ ] 用户状态启用 / 禁用
- [ ] 参数校验注解真正落到 DTO 上
- [ ] VO 全面替代直接返回 Entity
- [ ] 统一错误码与常量类
- [ ] 自定义配置属性绑定类 `config.prop`
- [ ] 通用工具类 `util`
- [ ] 认证与权限控制（JWT / Spring Security）

---

## 12. 常见问题

### 1）IDEA 提示主类不在 source root

通常是 Maven 项目没有正确刷新，执行以下操作：

1. IDEA 右侧 Maven 面板点击刷新
2. 执行：

```bash
./mvnw clean compile
```

3. 重新运行项目

### 2）MyBatis 日志提示未注册事务同步

如果只是普通查询，这通常是正常现象，不代表报错。

### 3）为什么查询自动过滤已删除数据

因为你已经配置了：
- `@TableLogic`
- `logic-delete-field`
- `logic-not-delete-value`
- `logic-delete-value`

MyBatis-Plus 会自动拼接逻辑删除条件。

---

## 13. 更新日志

### v0.0.3
- ✨ 新增条件分页查询 DTO：`UserSearchDTO`
- ✨ 新增分页输出对象：`UserPageVO`
- ✨ 新增全局时间格式化配置：`JacksonConfig`
- ✨ 新增自动填充处理器：`MyMetaObjectHandler`
- ✨ 新增多环境配置：dev / test / prod
- ✨ 新增逻辑删除全局配置
- ✨ 实体支持雪花算法主键
- 📝 README 按当前代码结构重新整理完善

### v0.0.2
- ✨ 新增用户增删改查基础能力
- ✨ 新增 MyBatis-Plus 分页插件
- ✨ 新增统一响应封装（Result）
- ✨ 新增分页结果封装（PageResult）
- ✨ 新增全局异常处理
- ✨ 新增跨域配置（CORS）
- ✨ 新增参数校验依赖

### v0.0.1
- 初始化项目结构
- 完成用户查询接口
- 集成 MyBatis-Plus
- 配置数据库连接

---

**开发者**： [HadisNZL](https://github.com/HadisNZL)
