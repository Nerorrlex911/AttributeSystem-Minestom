# AttributeSystem-Minestom

AttributeSystem from Glom for DevoutServer

This project is on heavy development and not ready for production.

本项目仍在开发中，不可用于生产环境。

# 主要改动

由于Minestom与Bukkit fork的API有极大差异，本项目对原版AttributeSystem进行了大幅重构，
尽管作者想要尽可能地保留原项目的使用体验，但仍然有不少改动，不能与原项目的配置文件完全兼容。

主要改动如下：

## 属性读取
**默认不开启Lore读取，仅读取NBT**

在Minestom平台，NBT不再拥有操作困难的缺点，同时具有更好的性能、更高的灵活性，**全方面**优于Lore读取。

作者想不到任何应当使用Lore而非NBT的案例，但仍然选择保留Lore读取功能并默认关闭。
## 属性的更新
与原项目类似的，AttributeSystem-Minestom同样采用事件驱动+定时更新两种方式，以保证属性的及时更新

其中事件驱动与原版基本一致，而定时更新任务不再由插件自己的线程池执行，而是提交到实体独有的调度器中，以保证线程安全并提高性能
## 战斗系统FightSystem
作者得出结论，AttributeSystem与FightSystem在几乎所有的应用场景中都是一起使用的，

因此FightSystem被合并到了AttributeSystem中

## 公式计算
读取组的公式计算不再依赖于Asahi，而是使用内置的简易计算器，支持常规数学运算符号以及“~”(取随机数)符号

部分复杂的表达如三元运算符，需要采用JavaScript解析，在表达式字符串前加入"js::"以使用js解析公式计算
## 占位符
Minestom平台目前没有类似PlaceholderAPI的库，本项目的占位符依赖PouPlaceholder插件
## 原版属性
本项目暂未实现原版属性兼容

## 更新计划
1. 合并FightSystem
2. NBT序列化重构，以更好地兼容Tag系统
3. 原版属性支持，包括血量压缩等
4. 优化语言文件，将旧版的颜色表达替换为minimessage格式

