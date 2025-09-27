# AttributeSystem-Minestom

AttributeSystem from Glom for DevoutServer

This project is on heavy development and not ready for production.

本项目仍在开发中，不可用于生产环境。

# 主要改动

由于Minestom与Bukkit fork的API有极大差异，本项目对原版AttributeSystem进行了大幅重构，
尽管作者想要尽可能地保留原项目的使用体验，但仍然有不少改动，不能与原项目的配置文件完全兼容。

目前为止，本项目强依赖于DevoutServer核心

主要改动如下：

## 属性读取
**默认不开启Lore读取，仅读取NBT**

在Minestom平台，NBT不再拥有操作困难的缺点，同时具有更好的性能、更高的灵活性，**全方面**优于Lore读取。

作者想不到任何应当使用Lore而非NBT的案例，但仍然选择保留Lore读取功能并默认关闭。
## 属性的更新
与原项目类似的，AttributeSystem-Minestom同样采用事件驱动+定时更新两种方式，以保证属性的及时更新

其中事件驱动与原版基本一致，而定时更新任务不再由插件自己的线程池执行，而是提交到实体独有的调度器中，以保证线程安全并提高性能
## 原版属性
AttributeSystem-Minestom只对原版属性进行简单映射，本质上只是一个定时更新的AttributeModifier，不会完全接管原版属性
## 生命值
Minestom出于性能考虑不提供在监听数据包发送时修改包的功能，因此无法像Bukkit那样通过修改数据包来实现血量压缩等功能，这对RPG服务器造成很大的困扰。

因此AttributeSystem-Minestom选择不依赖原版生命值逻辑，依托Tag系统维护一套自己的生命值系统，并完全接管伤害处理

生命值系统维护在HealthManager中，读取"CustomMaxHealth"作为最大生命值属性

生命值系统会在所有其他监听器执行完毕之后监听EntityDamageEvent事件，调整真实生命值，处理血量缩放，然后取消掉伤害事件

然而实际上用户可能根本不会采用LivingEntity#damage方法来对实体造成伤害，用户只需要在自己的战斗系统中合适的位置调用FightAPI#runFight即可
## 战斗系统FightSystem
作者得出结论，AttributeSystem与FightSystem在几乎所有的应用场景中都是一起使用的，

因此FightSystem被合并到了AttributeSystem中

## 公式计算
读取组的公式计算不再依赖于Asahi

目前有三种计算方式
1. 简单公式，支持常规数学运算符号以及“~”(取随机数)符号 在表达式字符串前加入"Formula::"以使用
2. [EvalEx](https://ezylang.github.io/EvalEx/references/functions.html)公式计算，在表达式字符串前加入"EvalEx::"以使用EvalEx解析公式计算
3. JavaScript解析，在表达式字符串前加入"js::"以使用js解析公式计算，或通过"File::path::function"的方式调用脚本文件中的函数计算

三种计算方式的自由度依次递增，性能依次递减，根据你的需求，选择最有性价比的计算方式
## 占位符
Minestom平台目前没有类似PlaceholderAPI的库，本项目的占位符依赖PouPlaceholder插件
## 原版属性
本项目暂未实现原版属性兼容

# 更新计划
1. 合并FightSystem 
   1. 基本功能 [x]
   2. 将战斗机制组的Asahi改为纯JavaScript [x]
   3. ~~机制注册~~ 
   4. ~~弓箭、盾牌等特殊武器~~ 
2. NBT序列化重构，以更好地兼容Tag系统
3. 原版属性支持，包括血量压缩等 [ ]
4. 优化语言文件，将旧版的颜色表达替换为minimessage格式 [ ]
5. 使用EvalEx来处理较为简单的运算 [x]

