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

package org.crazydan.studio.library.graphic;

import com.sun.jna.Native;
import org.crazydan.studio.library.graphic.hivision.HivisionConfig;
import org.crazydan.studio.library.graphic.hivision.HivisionJNA;

/**
 * 证件照制作
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2024-12-09
 */
public class IDCardPhotoMaker {
    private static final HivisionJNA jna;

    static {
        // Note: JNA 默认支持从环境变量 LD_LIBRARY_PATH 中指定的路径搜索本地库，不需要单独设置
        // Platform.RESOURCE_PREFIX -> linux-x86-64
        //System.setProperty("jna.library.path", System.getenv("LD_LIBRARY_PATH"));

        jna = Native.load(HivisionJNA.NATIVE_LIBRARY_NAME, HivisionJNA.class);
    }

    public static void createPhoto(HivisionConfig config) {
        jna.ID_photo(config, -1, false);
    }
}
