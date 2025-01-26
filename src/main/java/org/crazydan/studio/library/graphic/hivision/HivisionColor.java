/*
 * Copyright (C) 2025 Crazydan Studio <https://studio.crazydan.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
