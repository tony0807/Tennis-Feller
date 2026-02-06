# 一网情深 (Qiuyou Tennis App)

> 勇网直前

一个专为网球爱好者打造的社交约球应用，支持球友、俱乐部、教练和赛事主办方发布和参与各类网球活动。

## 功能特性

### 核心功能
- 🎾 **约球活动** - 发布和参与单次网球活动
- 🏆 **赛事活动** - 组织和报名网球比赛
- 📚 **课程培训** - 发布和预约网球课程
- 👤 **个人中心** - 管理个人信息和活动记录

### 主要特点
- ✅ 网球绿色主题设计
- ✅ Material Design 3 UI
- ✅ 基于位置的活动推荐
- ✅ 活动分享功能（微信/网页链接）
- ✅ 多种支付方式（微信/支付宝/银行卡）
- ✅ 实时活动状态更新

## 技术栈

### Android端
- **语言**: Kotlin
- **UI框架**: Jetpack Compose
- **架构**: MVVM + Clean Architecture
- **依赖注入**: Hilt
- **数据库**: Room
- **网络**: Retrofit + OkHttp
- **图片加载**: Coil
- **异步处理**: Kotlin Coroutines + Flow

### 架构设计
```
presentation/     # UI层 (Compose)
├── screens/      # 各个页面
├── components/   # 可复用组件
├── navigation/   # 导航配置
└── theme/        # 主题配置

domain/           # 业务逻辑层
├── usecase/      # 用例
└── model/        # 领域模型

data/             # 数据层
├── local/        # Room数据库
├── remote/       # API接口
├── repository/   # 仓库实现
└── model/        # 数据模型

di/               # 依赖注入
utils/            # 工具类
```

## 开始使用

### 环境要求
- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 17
- Android SDK 34
- Gradle 8.2+

### 安装步骤

1. **克隆项目**
```bash
git clone <repository-url>
cd qiuyou-tennis-app
```

2. **打开项目**
- 使用 Android Studio 打开项目
- 等待 Gradle 同步完成

3. **运行应用**
```bash
# 使用 Gradle
./gradlew installDebug

# 或在 Android Studio 中点击运行按钮
```

### 构建APK
```bash
# Debug版本
./gradlew assembleDebug

# Release版本
./gradlew assembleRelease
```

生成的APK位于: `app/build/outputs/apk/`

## 项目结构

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/qiuyou/tennis/
│   │   │   ├── data/              # 数据层
│   │   │   │   ├── local/         # Room数据库
│   │   │   │   ├── model/         # 数据模型
│   │   │   ├── presentation/      # UI层
│   │   │   │   ├── screens/       # 页面
│   │   │   │   ├── components/    # 组件
│   │   │   │   ├── navigation/    # 导航
│   │   │   │   └── theme/         # 主题
│   │   │   ├── di/                # 依赖注入
│   │   │   ├── utils/             # 工具类
│   │   │   ├── MainActivity.kt
│   │   │   └── TennisApplication.kt
│   │   ├── res/                   # 资源文件
│   │   └── AndroidManifest.xml
│   └── test/                      # 测试
└── build.gradle.kts
```

## 数据库设计

### 核心表
- **users** - 用户信息
- **clubs** - 俱乐部信息
- **activities** - 活动信息（约球/课程/赛事）
- **registrations** - 报名记录

详细设计请参考: [implementation_plan.md](../brain/implementation_plan.md)

## 开发指南

### 代码规范
- 遵循 Kotlin 官方代码风格
- 使用 ktlint 进行代码检查
- 所有公共函数添加注释

### 提交规范
```
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 代码格式调整
refactor: 重构
test: 测试相关
chore: 构建/工具链相关
```

### 分支策略
- `main` - 主分支，稳定版本
- `develop` - 开发分支
- `feature/*` - 功能分支
- `bugfix/*` - 修复分支

## 待实现功能

### 高优先级
- [ ] 用户认证系统（手机号登录）
- [ ] 微信SDK集成（登录/分享）
- [ ] 真实支付接口集成
- [ ] 后端API开发和集成
- [ ] 图片上传功能

### 中优先级
- [ ] 位置服务集成（高德地图）
- [ ] 推送通知
- [ ] 活动评论功能
- [ ] 用户评分系统

### 低优先级
- [ ] 深色模式优化
- [ ] 多语言支持
- [ ] 离线模式
- [ ] 数据统计分析

## 迁移到微信小程序

本项目采用模块化架构，便于后续迁移到微信小程序：

### 可复用部分
- ✅ 业务逻辑层（需转换为JavaScript）
- ✅ 数据模型
- ✅ API接口定义
- ✅ 工具函数

### 需重写部分
- ❌ UI层（Compose → WXML/WXSS）
- ❌ 本地存储（Room → 微信云数据库）
- ❌ 导航系统（Jetpack Navigation → 小程序路由）

## 常见问题

### 构建失败
1. 确保使用正确的 JDK 版本 (17)
2. 清理项目: `./gradlew clean`
3. 删除 `.gradle` 和 `build` 目录后重新构建

### 依赖问题
```bash
./gradlew --refresh-dependencies
```

### 模拟器运行缓慢
- 建议使用真机调试
- 或使用 x86_64 架构的模拟器并启用硬件加速

## 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 许可证

[MIT License](LICENSE)

## 联系方式

- 项目主页: [GitHub Repository]
- 问题反馈: [Issues]

---

**一网情深，勇网直前！** 🎾
