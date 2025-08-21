# 🌤️ 天穹 - 智能天气查询系统

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2022.x-blue.svg)](https://spring.io/projects/spring-cloud)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-orange.svg)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-6.x-red.svg)](https://redis.io/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

> 基于Spring Cloud微服务架构的智能化天气查询系统，集成AI分析、IP定位、智能缓存等先进功能

## 📋 目录

- [项目简介](#项目简介)
- [功能特性](#功能特性)
- [技术架构](#技术架构)
- [快速开始](#快速开始)
- [项目结构](#项目结构)
- [API文档](#api文档)
- [部署指南](#部署指南)
- [开发指南](#开发指南)
- [常见问题](#常见问题)
- [贡献指南](#贡献指南)
- [许可证](#许可证)

## 🎯 项目简介

天穹是一个基于Spring Cloud微服务架构的现代化天气查询系统，集成了多种先进技术：

- **微服务架构**：城市服务、省份服务、天气服务独立部署
- **智能缓存**：Redis缓存 + 按需加载 + 智能过期策略
- **AI分析**：讯飞星火大模型提供智能天气分析
- **IP定位**：高德地图API自动定位用户位置
- **现代化UI**：响应式设计 + 动画效果 + 数据可视化

## ✨ 功能特性

### 🌟 核心功能
- **实时天气查询**：支持全国3400+城市天气查询
- **多天预报**：1天、7天天气预报
- **智能定位**：基于IP地址自动定位用户城市
- **AI分析**：智能天气分析和生活建议
- **古诗词匹配**：根据天气自动匹配相关古诗词

### 🎨 用户体验
- **响应式设计**：完美适配PC、平板、手机
- **动画效果**：流畅的页面切换和交互动画
- **数据可视化**：温度图表、天气图标展示
- **智能匹配**：城市名称模糊匹配算法
- **表单验证**：实时输入验证和友好提示

### 🔧 技术特性
- **微服务架构**：高可用、可扩展的服务设计
- **智能缓存**：减少API调用，提升响应速度
- **容错机制**：服务熔断、降级、重试机制
- **监控日志**：完整的系统运行日志
- **安全防护**：API限流、数据验证

## 🏗️ 技术架构

### 技术栈
```
后端技术栈：
├── Spring Boot 3.x          # 应用框架
├── Spring Cloud 2022.x      # 微服务框架
├── Spring Cloud Netflix     # 服务发现
├── Spring Cloud OpenFeign   # 服务调用
├── Spring Data JPA          # 数据访问
├── Spring Data Redis        # 缓存
├── MySQL 8.0               # 数据库
└── Redis 6.x               # 缓存数据库

前端技术栈：
├── Thymeleaf               # 模板引擎
├── Bootstrap 5             # UI框架
├── JavaScript ES6+         # 交互逻辑
├── Chart.js               # 数据可视化
├── Anime.js               # 动画库
└── jQuery                 # DOM操作

外部服务：
├── 天行API                 # 天气数据
├── 高德地图API             # IP定位
└── 讯飞星火API             # AI分析
```

### 系统架构图
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Eureka Server │    │   Eureka Server │    │   Frontend      │
│   (8888/9999)   │    │   (8888/9999)   │    │   (7070)        │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
         ┌───────────────────────┼───────────────────────┐
         │                       │                       │
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  City Service   │    │ Province Service│    │ Weather Service │
│   (8080)        │    │   (8090)        │    │   (7070)        │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
                    ┌─────────────────┐    ┌─────────────────┐
                    │     MySQL       │    │     Redis       │
                    │   (weather DB)  │    │   (Cache)       │
                    └─────────────────┘    └─────────────────┘
```

## 🚀 快速开始

### 环境要求
- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- Node.js 16+ (可选，用于前端构建)

### 1. 克隆项目
```bash
git clone https://github.com/your-username/weather-forecast.git
cd weather-forecast
```

### 2. 数据库配置
```sql
-- 创建数据库
CREATE DATABASE weather CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 导入数据（可选，系统会自动创建表）
-- 城市和省份数据已包含在项目中
```

### 3. 配置文件
复制并修改配置文件：

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/weather?characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
  redis:
    host: localhost
    port: 6379
    password: your_redis_password  # 如果有密码

# 外部API配置
weather:
  tianapi:
    key: your_tianapi_key  # 天行API密钥
  amap:
    key: your_amap_key     # 高德地图API密钥
  xunfei:
    key: your_xunfei_key   # 讯飞星火API密钥
```

### 4. 启动服务
```bash
# 1. 启动Eureka注册中心
cd eureka-server
mvn spring-boot:run

# 2. 启动省份服务
cd getProvinceList8090
mvn spring-boot:run

# 3. 启动城市服务
cd getCityList8080
mvn spring-boot:run

# 4. 启动天气服务
cd showWeather7070
mvn spring-boot:run
```

### 5. 访问系统
- 主页面：http://localhost:7070/getWeatherThy
- Eureka控制台：http://localhost:8888

## 📁 项目结构

```
weather-forecast/
├── eureka-server/                 # Eureka注册中心
│   ├── src/main/java/
│   └── pom.xml
├── getProvinceList8090/           # 省份服务 (端口8090)
│   ├── src/main/java/
│   │   ├── controller/            # 控制器
│   │   ├── service/              # 服务层
│   │   ├── dao/                  # 数据访问层
│   │   └── po/                   # 实体类
│   └── pom.xml
├── getCityList8080/              # 城市服务 (端口8080)
│   ├── src/main/java/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── dao/
│   │   └── po/
│   └── pom.xml
├── showWeather7070/              # 天气服务 (端口7070)
│   ├── src/main/java/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── client/               # Feign客户端
│   │   ├── config/               # 配置类
│   │   └── job/                  # 定时任务
│   ├── src/main/resources/
│   │   ├── templates/            # Thymeleaf模板
│   │   ├── static/               # 静态资源
│   │   └── application.yml
│   └── pom.xml
├── common-interface/             # 公共接口
│   └── pom.xml
└── README.md
```

## 📚 API文档

### 天气查询API
```http
GET /getWeatherThy?city={cityId}&type={type}

参数：
- cityId: 城市ID (必填)
- type: 天气类型 (1=实时, 7=7天预报)

响应：
{
  "code": 200,
  "msg": "success",
  "result": {
    "date": "2025-08-21",
    "weather": "雨",
    "real": "25℃",
    "lowest": "24℃",
    "highest": "31℃",
    "wind": "南风",
    "humidity": "95%",
    "tips": "生活建议...",
    "poem": "古诗词..."
  }
}
```

### 城市列表API
```http
GET /api/city/FINDALL

响应：
[
  {
    "cityId": "110100",
    "city": "北京",
    "father": "110000"
  }
]
```

### 省份列表API
```http
GET /api/province/FINDALL

响应：
[
  {
    "provinceId": "110000",
    "province": "北京市"
  }
]
```

### AI分析API
```http
POST /api/ai-analyze
Content-Type: application/json

{
  "weatherInfo": "天气数据..."
}

响应：
{
  "result": "AI分析结果..."
}
```

### 缓存管理API
```http
POST /api/clear-cache

响应：
{
  "message": "缓存清除成功"
}
```

## 🚀 部署指南

### Docker部署
```dockerfile
# Dockerfile示例
FROM openjdk:17-jdk-slim
COPY target/*.jar app.jar
EXPOSE 7070
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

```bash
# 构建镜像
docker build -t weather-service .

# 运行容器
docker run -d -p 7070:7070 --name weather-app weather-service
```

### Docker Compose部署
```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: weather
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

  redis:
    image: redis:6-alpine
    ports:
      - "6379:6379"

  eureka-server:
    build: ./eureka-server
    ports:
      - "8888:8888"
    depends_on:
      - mysql

  weather-service:
    build: ./showWeather7070
    ports:
      - "7070:7070"
    depends_on:
      - eureka-server
      - redis

volumes:
  mysql_data:
```

## 👨‍💻 开发指南

### 开发环境搭建
1. **IDE推荐**：IntelliJ IDEA 2023+
2. **插件安装**：
   - Spring Boot Assistant
   - Spring Cloud Assistant
   - Lombok
   - Redis

### 代码规范
- 遵循阿里巴巴Java开发手册
- 使用Lombok简化代码
- 统一异常处理
- 完善日志记录

### 测试
```bash
# 单元测试
mvn test

# 集成测试
mvn verify

# 性能测试
mvn spring-boot:run -Dspring.profiles.active=test
```

### 调试技巧
1. **日志级别**：设置`logging.level.com.jnu.weather=DEBUG`
2. **缓存调试**：使用Redis Desktop Manager查看缓存
3. **网络调试**：使用Postman测试API接口

## ❓ 常见问题

### Q1: 服务启动失败
**A**: 检查端口是否被占用，确保MySQL和Redis服务正常运行

### Q2: 天气数据获取失败
**A**: 检查天行API密钥配置，确认API调用次数是否超限

### Q3: IP定位不准确
**A**: 这是正常现象，某些网络环境下IP定位精度有限，建议手动选择城市

### Q4: 缓存数据过期
**A**: 系统会自动清理过期缓存，也可以手动调用`/api/clear-cache`接口

### Q5: AI分析失败
**A**: 检查讯飞星火API密钥配置，确认API调用权限

## 🤝 贡献指南

### 贡献流程
1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

### 贡献类型
- 🐛 Bug修复
- ✨ 新功能
- 📝 文档更新
- 🎨 界面优化
- ⚡ 性能优化

### 代码审查
- 所有PR都需要通过代码审查
- 确保测试覆盖率不低于80%
- 遵循项目代码规范

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 🙏 致谢

- [Spring Boot](https://spring.io/projects/spring-boot) - 应用框架
- [Spring Cloud](https://spring.io/projects/spring-cloud) - 微服务框架
- [天行API](https://www.tianapi.com/) - 天气数据服务
- [高德地图API](https://lbs.amap.com/) - 地理定位服务
- [讯飞星火](https://xinghuo.xfyun.cn/) - AI分析服务
- [Bootstrap](https://getbootstrap.com/) - UI框架
- [Chart.js](https://www.chartjs.org/) - 数据可视化

---

⭐ 如果这个项目对你有帮助，请给它一个星标！
