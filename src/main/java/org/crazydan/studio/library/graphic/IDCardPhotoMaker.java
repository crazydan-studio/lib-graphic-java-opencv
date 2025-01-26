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
        // Note: 指定 JNA 的动态库搜索路径
        System.setProperty("jna.library.path", System.getenv("LD_LIBRARY_PATH"));

        jna = Native.load(HivisionJNA.NATIVE_LIBRARY_NAME, HivisionJNA.class);
    }

    public static void createPhoto(HivisionConfig config) {
        jna.ID_photo(config, -1, false);
    }
}
