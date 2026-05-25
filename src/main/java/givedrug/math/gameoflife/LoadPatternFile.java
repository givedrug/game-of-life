package givedrug.math.gameoflife;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 加载 conwaylife.com 标准的 .cells（plaintext）格式。
 *
 * 改进点：
 *  - 自动测量图样尺寸，不再要求调用方手填 boundx/boundy
 *  - 返回稀疏的活细胞坐标，避免对大图样浪费内存
 *  - 不再对每行 println，启动不刷屏
 */
public class LoadPatternFile {

    /**
     * 从 classpath 读取 .cells 文件并解析为 Pattern。
     *
     * @param fileName 资源文件名（位于 src/main/resources 根下）
     * @return 解析后的图样
     * @throws IOException 读取或解析失败
     */
    public Pattern load(String fileName) throws IOException {
        try (InputStream in = getClass().getResourceAsStream("/" + fileName)) {
            if (in == null) {
                throw new IOException("Pattern file not found on classpath: " + fileName);
            }
            return parse(in);
        }
    }

    /** 仅供单元测试 / 外部调用方使用：从任意输入流解析。 */
    public Pattern parse(InputStream in) throws IOException {
        List<long[]> rowsAlive = new ArrayList<>();
        int maxWidth = 0;
        int height = 0;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty() || line.startsWith("!")) continue; // 注释/空行
                if (!isDataLine(line)) continue;

                int len = line.length();
                if (len > maxWidth) maxWidth = len;

                // 暂存这一行的活细胞 x 坐标
                long[] aliveX = new long[len];
                int idx = 0;
                for (int i = 0; i < len; i++) {
                    char c = line.charAt(i);
                    if (c != '.' && c != ' ') {
                        aliveX[idx++] = i;
                    }
                }
                long[] trimmed = new long[idx];
                System.arraycopy(aliveX, 0, trimmed, 0, idx);
                rowsAlive.add(trimmed);
                height++;
            }
        }

        // 把 (row, x) 折成 (x, y) 编码列表
        int total = 0;
        for (long[] row : rowsAlive) total += row.length;
        long[] alive = new long[total];
        int p = 0;
        for (int y = 0; y < rowsAlive.size(); y++) {
            long[] row = rowsAlive.get(y);
            for (long xL : row) {
                alive[p++] = Pattern.encode((int) xL, y);
            }
        }
        return new Pattern(maxWidth, height, alive);
    }

    private static boolean isDataLine(String s) {
        // 数据行只包含 '.', 'O', 'o', 或者 ' '（兼容某些导出工具）
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c != '.' && c != 'O' && c != 'o' && c != ' ') return false;
        }
        return true;
    }

    // ---- 旧 API 兼容（保留以防外部调用，内部不再使用）----
    /** @deprecated 使用 {@link #load(String)} 改进版本，自动测尺寸。 */
    @Deprecated
    public boolean[][] getPatternMap(String fileName, int boundx, int boundy) {
        boolean[][] map = new boolean[boundy][boundx];
        try {
            Pattern p = load(fileName);
            for (long key : p.getAliveCells()) {
                int x = Pattern.decodeX(key);
                int y = Pattern.decodeY(key);
                if (x >= 0 && x < boundx && y >= 0 && y < boundy) map[y][x] = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }
}
