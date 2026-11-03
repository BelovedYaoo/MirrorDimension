# MirrorDimension

为 Minecraft 添加「弦世界」维度的 Forge 模组。

## 当前变体

| 变体 | Minecraft | 加载器 | 说明 |
|---|---|---|---|
| `1.19.2-forge` | 1.19.2 | Forge 43.x | |
| `1.20.1-forge` | 1.20.1 | Forge 47.x | 基准变体 |

构建使用 [Stonecutter](https://stonecutter.kikugie.dev/) + [Modstitch](https://github.com/isXander/modstitch)。

## 构建

需要 JDK 21：

```bash
./gradlew chiseledBuild
```

产物位于 `versions/<变体>/build/libs/mirrordim-<版本>.jar`。

> `chiseledBuild` 会为所有变体生成源码后再构建；直接构建单个变体可能因为缺少生成源码而得到空 jar。
