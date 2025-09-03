# AttributeSystem-Minestom

AttributeSystem from Glom for DevoutServer

This project is on heavy development and not ready for production.

本项目仍在开发中，不可用于生产环境。

# 主要改动

## 属性读取
**默认不开启Lore读取，仅读取NBT**

在Minestom平台，NBT不再拥有操作困难的缺点，同时具有更好的性能、更高的灵活性，**全方面**优于Lore读取。

作者想不到任何应当使用Lore而非NBT的案例，但仍然选择保留Lore读取功能并默认关闭。
## 公式计算
读取组的公式计算不再依赖于Asahi，而是使用内置的简易计算器，支持常规数学运算符号以及“~”(取随机数)符号

部分复杂的表达如三元运算符，需要采用JavaScript解析，在表达式字符串前加入"js::"以使用js解析公式计算
## 占位符
Minestom平台目前没有类似PlaceholderAPI的库，本项目的占位符依赖PouPlaceholder插件
## 原版属性
本项目暂未实现原版属性兼容

