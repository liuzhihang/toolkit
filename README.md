Toolkit
=======

[![JetBrains Plugins](https://img.shields.io/jetbrains/plugin/v/12384-toolkit.svg)](https://plugins.jetbrains.com/plugin/12384-toolkit)
[![Version](http://phpstorm.espend.de/badge/12384/version)](https://plugins.jetbrains.com/plugin/12384-toolkit/versions)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/12384-toolkit.svg)](https://plugins.jetbrains.com/plugin/12384-toolkit)
[![License](https://img.shields.io/badge/license-MIT-red.svg)](https://github.com/liuzhihangs/toolkit/blob/master/LICENSE)


一个小工具包, 暂时还有很多功能需要扩展.

特征
----
- Mybatis
    - 通过侧栏箭头在 MyBatis XML文件和 Mapper文件之间相互跳转
    - mapper文件id简单检查
- Json
    - JavaBean复制为Json字符串
    - Json字符串格式化
    - Json字符串转换为JavaBean
- XML: Xml格式化

演示
----
- 文末演示

安装
----
- **在线安装:**
  - `File` -> `Setting` -> `Plugins` -> 搜索 `Toolkit`

- **手动安装:**
  - [下载插件](https://github.com/liuzhihangs/toolkit/releases) -> `File` -> `Setting` -> `Plugins` -> `Install Plugin from Disk...`

使用
----
- 右键菜单选择 `Tookit`
            
更新
----
## [v1.0.4](https://github.com/liuzhihangs/toolkit/releases/tag/v1.0.4) (2021-01-21)

- 修改默认快捷键
- 支持Json字符串生成JavaBean中的字段
- mapper文件id简单检查


[查看更多历史更新记录](./doc/ChangeNotes.md)

感谢
----

##### MyBatis:
&emsp;mybatis support: [https://github.com/zhaoqin102/mybatis-support](https://github.com/zhaoqin102/mybatis-support)

&emsp;free-idea-mybatis: [https://github.com/wuzhizhan/free-idea-mybatis](https://github.com/wuzhizhan/free-idea-mybatis)

##### Json:
&emsp;GsonFormat: [https://github.com/zzz40500/GsonFormat](https://github.com/zzz40500/GsonFormat)


演示
----

![copy-as-json](https://liuzhihang.com/oss/pic/toolkit/copy-as-json.png)
![gif](https://liuzhihang.com/oss/pic/toolkit/gif.gif)