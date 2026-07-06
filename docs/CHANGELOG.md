# Changelog

本文件记录盈迹 (Gainful) 应用的所有重要变更。格式基于 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.0.0/)。

## [1.0.2] - 2026-07-07

### 新增功能

#### Dashboard (仪表盘)
- PnL 概览卡片
- 基于 K 线数据的真实 PnL 计算，替代模拟数据
- 股票详情弹窗，展示持仓和交易信息
- PnL 详情改用 ModalBottomSheet 展示

#### 持仓
- 持仓热力图替换为 squarify treemap 布局，提升可读性

#### Widgets (小组件)
- Android 和 iOS 小组件，展示当日盈亏
- Android 小组件尺寸优化为 2x2/2x4
- 小组件支持即时刷新和后台同步

#### UI
- 所有文本/Emoji 图标替换为矢量 XML drawable

### 修复问题

- 统一各页面底部间距和屏幕内边距一致性
- 禁用 R8 full mode 修复 release 构建中 Glance 小组件渲染问题

### 重构

- PnL 计算引入缓存机制，分离计算与查询

### 构建与依赖

- GitHub Actions 添加 spotlessCheck 代码格式化检查

### 文档

- 添加股票计算规则文档
- 更新项目文档

## [1.0.1] - 2026-06-30

盈迹 v1.0.1 是第一个正式发布版本。这是一款 Kotlin Multiplatform + Compose Multiplatform 投资组合管理应用，支持 Android、iOS 和 Desktop 三端。

### 新增功能

#### 核心架构
- 基于 Clean Architecture 的模块化架构，包含 core、feature、shared 三层结构
- Navigation3 导航框架，支持多端共享导航逻辑
- DataStore 用户偏好存储模块
- Koin 依赖注入框架集成
- GitHub Actions CI/CD 工作流，支持单元测试和构建验证

#### Dashboard (仪表盘)
- 实时展示投资组合数据，连接数据库和网络数据源
- 每日盈亏 (PnL) 卡片，直观展示当日收益

#### Holdings (持仓)
- 热力图式持仓展示，紧凑卡片设计
- 股票详情页面，支持导航跳转
- 已平仓列表展示
- 涨跌颜色方案自定义设置

#### Transactions (交易)
- 交易记录页面，支持资产搜索
- 新增交易界面，支持快速选择持仓资产
- 日历对话框日期选择器，替代文本输入
- 交易日期字段和 UUID 唯一标识
- 按交易日期排序，持仓按数量排序

#### Settings (设置)
- 频率选择器和时间选择器
- CSV 导入功能，支持重复检查、删除对话框和资产信息补全
- 益损颜色方案设置

#### UI 组件
- TimePicker 滚轮选择器组件
- DateTimePicker 组合日期时间选择器
- GainfulTopAppBar 顶部导航栏组件
- GainfulDialog 对话框组件
- 可复用按钮组件 (NavButton, PrimaryButton, SecondaryButton, SelectChip, SquareIconButton)
- BottomBar 底部导航栏组件
- ScreenScaffold 屏幕容器组件
- 设计系统模块 (core/designsystem)，统一管理 UI 组件

#### 数据层
- 东方财富 (EastMoney) API 客户端，实现股票数据获取
- Room 数据库，支持 BundledSQLiteDriver 跨平台
- 股票价格缓存和后台刷新
- iOS CsvFileUtil 原生 UIKit 实现

#### 国际化
- 多模块字符串本地化，支持中文和英文
- 应用名称本地化为 "盈迹"

#### 平台
- Android 图标设计
- Desktop 构建配置
- Android core/common 模块集成 android.icu

### 修复问题

- 修复导航栏 ViewModel 泄漏和 TopLevelBackStack removeLast 错误
- 修复 ViewModel 在组合重建时丢失的问题
- 修复 DatePicker 月份跨年导航崩溃
- 修复 CalendarDialog 月份切换时的高度动画问题
- 修复 formatDecimal 浮点精度错误，改用整数运算
- 修复交易记录排序和持仓芯片排序问题
- 修复持仓 sparkline X 轴方向
- 修复 iOS RoomDatabaseConstructor 问题
- 禁用 Android 预测性返回手势
- 禁用全局水波纹效果 (NoOpIndication)

### 重构

- 重新组织项目结构，引入 feature 子包和 Route 抽象
- 提取 GainfulScaffold 到 core/designsystem 模块
- 提取 BottomBar 到 core/ui 模块
- 提取 reusable button components 到 core/ui 模块
- 统一对话框模式，提取 GainfulDialog
- 集中化数字和日期格式化到 core/common
- 简化 TransactionCard 组件
- 重新设计设置页面布局
- 导航重构为 nowinandroid 模式，使用 entry providers

### 构建与依赖

- 添加 release 构建脚本，支持 R8 混淆和 ProGuard
- 简化 release 签名配置
- 添加 spotless 和 ktlint 代码格式化
- 替换废弃的 androidLibrary 块
- 更新 GitHub Actions 到 v7/v5/v6，支持 Node.js 24

### 文档

- 更新 README 和 AGENTS.md
- 添加 UI 组件参考文档
- 添加 ViewModel 重建修复文档 (MIUI 主屏幕启动)
- 添加 Screen Composition 规则

## [1.0.0] - 未发布

初始版本（内部开发）。
