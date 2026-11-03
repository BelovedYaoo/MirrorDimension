# MirrorDimension

为 Minecraft 添加「弦世界」维度的 Forge / NeoForge / Fabric 模组。

## 当前变体

| 变体 | Minecraft | 加载器 | 说明 |
|---|---|---|---|
| `1.19.2-forge` | 1.19.2 | Forge 43.x | Create 兼容 |
| `1.19.2-fabric` | 1.19.2 | Fabric | |
| `1.20.1-forge` | 1.20.1 | Forge 47.x | 基准变体，Create 兼容 |
| `1.20.1-neoforge` | 1.20.1 | NeoForge 47.x | Create 兼容（Forge 兼容 API） |
| `1.20.1-fabric` | 1.20.1 | Fabric | |
| `1.21.1-neoforge` | 1.21.1 | NeoForge 21.1.x | DimensionTransition 传送门实现，Create 6.x 兼容 |
| `1.21.1-fabric` | 1.21.1 | Fabric | DimensionTransition 传送门实现 |

构建使用 [Stonecutter](https://stonecutter.kikugie.dev/) + [Modstitch](https://github.com/isXander/modstitch)。

## 构建

```bash
./gradlew chiseledBuild
```

需要 JDK 21。产物位于 `versions/<变体>/build/libs/mirrordim-<版本>.jar`。

> `chiseledBuild` 会为所有变体生成源码后再构建；直接构建单个变体可能因为缺少生成源码而得到空 jar。

## 为什么 mixins.json 放在 templates

`src/main/templates/mirrordim.mixins.json` 由 Modstitch 在 `processResources` 前统一渲染；
这样 `compatibilityLevel` 的 `${java_compatibility}` 占位符会在构建时展开，同时避免
`filesMatching { expand(...) }` 产生 IDEA 无法解析的 `MatchingCopyAction` 警告。
