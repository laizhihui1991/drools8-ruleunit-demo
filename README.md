# drools8-ruleunit-demo

一个基于 Drools 8 Rule Unit API 的最小示例。它演示了怎么定义 Rule Unit、在规则里用 OOPath 匹配数据，以及怎么通过查询（query）拿回匹配结果。

## 技术栈

| 组件 | 版本 |
| --- | --- |
| JDK | 11 及以上 |
| Maven | 3.x |
| Drools | 8.44.0.Final |
| SLF4J Simple | 1.7.36 |

主要依赖：

- `drools-ruleunits-engine`：Rule Unit 核心引擎
- `drools-wiring-dynamic`：动态类加载支持。缺少它运行时会报错
- `slf4j-simple`：日志实现，用来消除 SLF4J 警告

## 目录结构

```
drools8-ruleunit-demo
├── pom.xml
└── src
    └── main
        ├── java/org/example
        │   ├── Measurement.java      # 事实对象（id + val）
        │   ├── MeasurementUnit.java  # Rule Unit 数据定义
        │   └── RuleTest.java         # 程序入口
        └── resources/org/example
            └── rules.drl             # 规则与查询
```

## 核心代码说明

### Measurement

一个简单的事实对象，有两个字段：`id`（测量类型，比如 `"color"`）和 `val`（测量值，比如 `"red"`）。

### MeasurementUnit

实现 `RuleUnitData` 接口，是规则单元的数据载体：

- `measurements`：`DataStore<Measurement>`，由 `DataSource.createStore()` 创建，规则里通过 `/measurements` 访问
- `controlSet`：`Set<String>`，规则在 RHS 中把匹配到的颜色值写入这里

### rules.drl

通过 `unit MeasurementUnit;` 绑定到对应的 Rule Unit。

```drl
rule "Collect colors"
when
    /measurements[ id == "color", $colorVal : val ]
then
    controlSet.add($colorVal);
    System.out.println("匹配到颜色: " + $colorVal);
end

query FindColor
    $m: /measurements[ id == "color" ]
end
```

- 规则 `Collect colors`：匹配 `id == "color"` 的数据，把 `val` 加入 `controlSet` 并打印
- 查询 `FindColor`：返回所有 `id == "color"` 的 `Measurement`，绑定变量为 `$m`

### RuleTest

执行流程：

1. 创建 `MeasurementUnit`，并通过 `RuleUnitProvider.get().createRuleUnitInstance(...)` 得到 `RuleUnitInstance`
2. 向 `measurements` 添加 5 条颜色数据：red、green、blue、yellow、cyan
3. 调用 `instance.executeQuery("FindColor")` 执行查询
4. 遍历 `QueryResults`，通过 `row.get("$m")` 取出 `Measurement` 并打印 `val`，最后输出总数
5. 在 `finally` 中调用 `instance.close()` 释放资源

## 构建与运行

确认 `JAVA_HOME` 指向 JDK 11 或更高版本（`pom.xml` 中编译目标为 11，用 JDK 8 编译会报 `无效的目标发行版: 11`）。

编译：

```bash
mvn clean compile
```

运行（`pom.xml` 未配置 exec 插件，可以直接通过完整坐标调用）：

```bash
mvn compile org.codehaus.mojo:exec-maven-plugin:3.1.0:java -Dexec.mainClass=org.example.RuleTest
```

在 PowerShell 中需要给 `-D` 参数加引号：

```powershell
mvn compile org.codehaus.mojo:exec-maven-plugin:3.1.0:java "-Dexec.mainClass=org.example.RuleTest"
```

也可以直接在 IDE 中运行 `org.example.RuleTest` 的 `main` 方法。

## 预期输出

查询部分会列出全部 5 种颜色：

```
=== 开始执行查询 ===
匹配到的颜色列表：
  red
  green
  blue
  yellow
  cyan
总计匹配到 5 条记录
```

结果的顺序可能和插入顺序不同。如果引擎在执行查询前触发了规则，还会看到 `匹配到颜色: xxx` 形式的输出。如果需要明确触发规则，可以在查询前调用 `instance.fire()`。
