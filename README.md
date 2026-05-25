# game-of-life

JavaFX 实现的 Conway's Game of Life。

## 运行

```bash
# 默认 breeder1（749×338，二次增长）
mvn javafx:run

# Gosper 滑翔机枪
mvn javafx:run -Djavafx.args="gun"
```

> 需要 JDK 11+（JavaFX 17 已作为依赖引入）。

## 键盘控制

| 键 | 作用 |
|---|---|
| `Space` | 暂停 / 继续 |
| `S` | 单步（仅在暂停时） |
| `↑` / `↓` | 加速 / 减速 |
| `R` | 重置当前预设 |
| `C` | 清空画布 |
| 鼠标点击 | 切换格子状态 |

## 内置图样

- `gosperglidergun.cells` —— Gosper 滑翔机枪（首个无界生长图样）
- `breeder1.cells` —— Breeder 1（首个二次增长图样，Bill Gosper 1970s）

## 测试

```bash
mvn test
```

## 架构

```
GameOfLife       JavaFX UI / 输入处理 / Canvas 渲染
GameEngine       稀疏活细胞 Set 演化算法
LoadPatternFile  .cells 解析（自动测尺寸）
Pattern          解析结果（活细胞坐标列表 + 宽高）
PatternPreset    内置图样预设（取代注释切换）
```

主要重构点见 `game-of-life-代码解析.html` 末尾的"改进建议"。
