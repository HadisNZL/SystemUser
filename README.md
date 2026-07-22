# Admin System

一个基于 Spring Boot 3.5.16 + MyBatis-Plus 的后台管理系统。

## 技术栈

- **Spring Boot 3.5.16** - 核心框架
- **MyBatis-Plus 3.5.5** - 持久层框架，简化 MyBatis 开发
- **MySQL 8.x** - 数据库
- **Lombok** - 简化实体类代码
- **JDK 17** - 开发环境

## 项目结构

```
admin-system/
├── src/main/java/com/system/
│   ├── AdminSystemApplication.java    # 启动类
│   ├── controller/                    # 控制器层
│   │   └── SysUserController.java     # 用户管理接口
│   ├── service/                       # 服务层
│   │   ├── SysUserService.java        # 用户服务接口
│   │   └── iml/
│   │       └── SysUserServiceImpl.java # 用户服务实现
│   ├── mapper/                        # 数据访问层
│   │   └── SysUserMapper.java         # 用户 Mapper
│   └── entity/                        # 实体类
│       └── SysUser.java               # 用户实体
└── src/main/resources/
    └── application.yaml               # 应用配置
```

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+

### 数据库配置

1. 创建数据库：
```sql
CREATE DATABASE admin_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 创建用户表：
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

3. 插入测试数据：
```sql
INSERT INTO sys_user (username, password, nickname, phone, email) VALUES
('admin', '123456', 'Super管理员', '18510044400', 'niu_zilin@163.com'),
('test', '123456', '测试用户', '18932550885', '');
```

### 配置修改

修改 `src/main/resources/application.yaml` 中的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/admin_system?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&allowMultiQueries=true
    username: root
    password: 123456  # 修改为你的数据库密码
```

### 运行项目

```bash
# 使用 Maven Wrapper
./mvnw spring-boot:run

# 或使用 Maven
mvn spring-boot:run
```

### 访问接口

启动成功后访问：

- **用户列表接口**：http://localhost:8080/sys/user/list

返回示例：
```json
[
  {
    "id": 1,
    "username": "admin",
    "password": "123456",
    "nickname": "Super管理员",
    "phone": "18510044400",
    "email": "niu_zilin@163.com",
    "status": 1,
    "createTime": "2026-07-21T10:36:01",
    "updateTime": "2026-07-21T13:22:27",
    "deleteFlag": 0
  }
]
```

## 功能特性

### 已实现

- ✅ 用户查询接口
- ✅ MyBatis-Plus 集成
- ✅ 数据库连接池（HikariCP）
- ✅ 驼峰命名自动转换
- ✅ SQL 日志输出

### 开发中

- 🔨 用户增删改功能
- 🔨 JWT 身份认证
- 🔨 权限管理
- 🔨 操作日志

## MyBatis-Plus 配置说明

项目使用 MyBatis-Plus 简化开发，无需编写 XML 映射文件：

```yaml
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true  # 数据库字段下划线转驼峰
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl  # 控制台打印 SQL
```

Mapper 继承 `BaseMapper<T>` 即可获得基础 CRUD 方法：

```java
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    // 无需写任何代码，已有 selectList、insert、update、delete 等方法
}
```

## 开发规范

### 分层说明

- **Controller** - 接收请求，参数校验，调用 Service
- **Service** - 业务逻辑处理，事务控制
- **Mapper** - 数据访问，SQL 操作
- **Entity** - 实体类，对应数据库表

### 命名规范

- 实体类：`Sys` + 功能名（如 `SysUser`）
- Mapper：实体名 + `Mapper`（如 `SysUserMapper`）
- Service：实体名 + `Service`（如 `SysUserService`）
- Controller：实体名 + `Controller`（如 `SysUserController`）

### 注解说明

- `@RestController` - RESTful API 控制器
- `@Service` - 业务服务层
- `@Mapper` - MyBatis 数据访问层
- `@Resource` - 依赖注入（推荐）
- `@Transactional` - 事务管理（用于增删改操作）

## 问题排查

### 1. 找不到主类

```bash
错误: 找不到或无法加载主类 com.system.AdminSystemApplication
```

**解决方案**：
1. IDEA 右侧 Maven 面板点击刷新
2. 执行 `./mvnw clean compile`
3. 重新运行项目

### 2. 数据库连接失败

```bash
Communications link failure
```

**检查清单**：
- MySQL 服务是否启动
- 数据库名称、用户名、密码是否正确
- 防火墙是否开放 3306 端口

### 3. MyBatis 日志提示 "not registered for synchronization"

这是**正常现象**，查询操作不需要事务管理。只有增删改操作需要在 Service 方法上添加 `@Transactional` 注解。

## 许可证

MIT License

## 更新日志

### v0.0.1 (2026-07-22)
- 初始化项目结构
- 完成用户查询接口
- 集成 MyBatis-Plus
- 配置数据库连接

---

**开发者**: [HadisNZL](https://github.com/HadisNZL)
