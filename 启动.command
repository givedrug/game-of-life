#!/bin/bash
# 双击启动 game-of-life（macOS）
# 自动切到 JDK 11+ 并用 mvn javafx:run 运行

set -e
cd "$(dirname "$0")"

echo "============================================"
echo "  game-of-life · Conway's Game of Life"
echo "============================================"
echo ""

# ---- 选 JDK：优先 21 → 17 → 11 ----
JAVA_BIN=""
for V in 21 17 11; do
    if /usr/libexec/java_home -v "$V" &>/dev/null; then
        export JAVA_HOME=$(/usr/libexec/java_home -v "$V")
        JAVA_BIN="$JAVA_HOME/bin/java"
        echo "[ok] 使用 JDK $V: $JAVA_HOME"
        break
    fi
done

if [ -z "$JAVA_BIN" ]; then
    echo "[ERROR] 没找到 JDK 11+，请先装一个（推荐 21）："
    echo "        brew install --cask temurin@21"
    read -p "按回车关闭..."
    exit 1
fi

# ---- 检查 maven ----
if ! command -v mvn &>/dev/null; then
    echo "[ERROR] 没找到 mvn，请先装："
    echo "        brew install maven"
    read -p "按回车关闭..."
    exit 1
fi

# ---- 选图样：第 1 个参数 / 默认 breeder1 ----
PRESET="${1:-breeder1}"
echo "[ok] 图样: $PRESET"
echo ""
echo "[控制] Space 暂停 · S 单步 · ↑↓ 调速 · R 重置 · C 清屏 · 鼠标点击切换格子"
echo ""

mvn -q javafx:run -Djavafx.args="$PRESET"

echo ""
echo "[已退出] 按回车关闭终端窗口..."
read
