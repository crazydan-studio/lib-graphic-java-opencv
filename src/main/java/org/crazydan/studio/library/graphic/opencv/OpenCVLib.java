package org.crazydan.studio.library.graphic.opencv;

import org.opencv.core.Core;

/**
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-01-26
 */
public class OpenCVLib {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
}
