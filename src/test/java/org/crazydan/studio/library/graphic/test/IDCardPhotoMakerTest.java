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

package org.crazydan.studio.library.graphic.test;

import java.io.File;

import org.crazydan.studio.library.graphic.IDCardPhotoMaker;
import org.crazydan.studio.library.graphic.hivision.HivisionConfig;
import org.junit.Test;

/**
 * 注意，运行单元测试前，需设置环境变量 <code>LD_LIBRARY_PATH=lib</code>
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-01-26
 */
public class IDCardPhotoMakerTest {

    @Test
    public void test_createPhoto() {
        File resourceDir = new File(getClass().getResource("/").getPath());
        File targetDir = new File(resourceDir, "..");
        File rootDir = new File(targetDir, "..");

        File modelDir = new File(rootDir, "model");
        File portraitModelFile = new File(modelDir, "modnet_photographic_portrait_matting.onnx");

        File imageFile = new File(resourceDir, "images/idphoto-test.jpg");

        HivisionConfig config = new HivisionConfig();
        config.portraitMattingModelFile = portraitModelFile.getAbsolutePath();
        config.faceDetectingModelDir = modelDir.getAbsolutePath();

        config.outImageDir = targetDir.getAbsolutePath();
        config.srcImageFile = imageFile.getAbsolutePath();

        IDCardPhotoMaker.createPhoto(config);
    }
}
