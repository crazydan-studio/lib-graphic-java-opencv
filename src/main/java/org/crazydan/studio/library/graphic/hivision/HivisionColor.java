package org.crazydan.studio.library.graphic.hivision;

import com.sun.jna.Structure;

/**
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-01-26
 */
@Structure.FieldOrder({
        // 必须按 C/C++ 代码中结构体成员的位置，按顺序做映射
        "r", "g", "b",
})
public class HivisionColor extends Structure implements Structure.ByValue {
    public int r;
    public int g;
    public int b;

    /** Note: 必须定义无参构造函数 */
    public HivisionColor() {
        super();
    }

    public HivisionColor(int r, int g, int b) {
        this();

        this.r = r;
        this.g = g;
        this.b = b;
    }
}
