package org.crazydan.studio.library.graphic.test;

import java.io.File;

import org.crazydan.studio.library.graphic.ImageRectificationV1;
import org.crazydan.studio.library.graphic.ImageRectificationV2;
import org.junit.Test;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * 注意，运行单元测试前，需设置环境变量 <code>LD_LIBRARY_PATH=lib</code>
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2024-12-01
 */
public class ImageRectificationTest {

    @Test
    public void test_correct_v1() {
        File resourceDir = new File(getClass().getResource("/").getPath());
        File targetDir = new File(resourceDir, "..");

        File imageFile = new File(resourceDir, "images/correct-test.png");
        File outImageFile = new File(targetDir, "correct-test.v1.jpg");

        Mat mat = ImageRectificationV1.correct(imageFile.getAbsolutePath());
        // Note: 输出文件必须带后缀名，否则将报异常：could not find a writer for the specified extension in function 'imwrite_'
        Imgcodecs.imwrite(outImageFile.getAbsolutePath(), mat);
    }

    @Test
    public void test_correct_v2() {
        File resourceDir = new File(getClass().getResource("/").getPath());
        File targetDir = new File(resourceDir, "..");

        File imageFile = new File(resourceDir, "images/correct-test.png");
        File outImageFile = new File(targetDir, "correct-test.v2.jpg");

        Mat mat = ImageRectificationV2.correct(imageFile.getAbsolutePath());
        // Note: 输出文件必须带后缀名，否则将报异常：could not find a writer for the specified extension in function 'imwrite_'
        Imgcodecs.imwrite(outImageFile.getAbsolutePath(), mat);
    }
}
