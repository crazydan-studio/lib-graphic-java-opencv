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

import java.util.ArrayList;
import java.util.List;

import org.crazydan.studio.library.graphic.opencv.OpenCVLib;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.core.CvType.CV_8UC3;
import static org.opencv.imgproc.Imgproc.CHAIN_APPROX_NONE;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.COLOR_RGB2GRAY;
import static org.opencv.imgproc.Imgproc.FILLED;
import static org.opencv.imgproc.Imgproc.RETR_EXTERNAL;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;

/**
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2025-01-26
 */
public class ImageRectificationV2 extends OpenCVLib {

    /** 参考实现《<a href="https://www.cnblogs.com/skyfsm/p/6902524.html">OpenCV探索之路（十六）：图像矫正技术深入探讨</a>》 */
    public static Mat correct(String filename) {
        Mat srcImg = Imgcodecs.imread(filename);

        // 灰度化
        Mat gray = new Mat();
        Imgproc.cvtColor(srcImg, gray, COLOR_RGB2GRAY);
        Imgcodecs.imwrite(filename + ".0.gray.jpg", gray);

        // 二值化
        Mat binaryImg = new Mat();
        Imgproc.threshold(gray, binaryImg, 100, 200, THRESH_BINARY);
        Imgcodecs.imwrite(filename + ".1.binary.jpg", gray);

        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        // 注意第 5 个参数为 RETR_EXTERNAL，只检索外框
        Imgproc.findContours(binaryImg, contours, hierarchy, RETR_EXTERNAL, CHAIN_APPROX_NONE); // 找轮廓

        for (int i = 0; i < contours.size(); i++) {
            // 需要获取的坐标
            RotatedRect rect = Imgproc.minAreaRect(new MatOfPoint2f(contours.get(i).toArray()));
            // 获取 4 个顶点坐标
            Point[] rectpoint = new Point[4];
            rect.points(rectpoint);

            // 与水平线的角度
            double angle = rect.angle;

            double line1 = Math.sqrt((rectpoint[1].y - rectpoint[0].y) * (rectpoint[1].y - rectpoint[0].y)
                                     + (rectpoint[1].x - rectpoint[0].x) * (rectpoint[1].x - rectpoint[0].x));
            double line2 = Math.sqrt((rectpoint[3].y - rectpoint[0].y) * (rectpoint[3].y - rectpoint[0].y)
                                     + (rectpoint[3].x - rectpoint[0].x) * (rectpoint[3].x - rectpoint[0].x));
            // 面积太小的直接 pass
            if (line1 * line2 < 600) {
                continue;
            }

            // 为了让正方形横着放，所以旋转角度是不一样的。竖放的，给他加 90 度，翻过来
            if (line1 > line2) {
                angle = 90 + angle;
            }

            // 新建一个感兴趣的区域图，大小跟原图一样大
            Mat roiSrcImg = new Mat(srcImg.rows(), srcImg.cols(), CV_8UC3); // 注意这里必须选 CV_8UC3
            roiSrcImg.setTo(new Scalar(0)); // 颜色都设置为黑色
            //Imgcodecs.imwrite(filename + ".1.0.roi.jpg", roiSrcImg);
            // 对得到的轮廓填充一下
            Imgproc.drawContours(binaryImg, contours, -1, new Scalar(255), FILLED);
            //Imgcodecs.imwrite(filename + ".1.1.roi.jpg", binaryImg);

            // 抠图到 roiSrcImg
            srcImg.copyTo(roiSrcImg, binaryImg);
            // 再显示一下看看，除了感兴趣的区域，其他部分都是黑色的了
            Imgcodecs.imwrite(filename + ".2.roi.jpg", roiSrcImg);

            // 创建一个旋转后的图像
            Mat rotatedImg = new Mat(srcImg.rows(), srcImg.cols(), CV_8UC1);
            rotatedImg.setTo(new Scalar(0));

            // 对 roiSrcImg 进行旋转
            Point center = rect.center;  // 中心点
            // 计算旋转加缩放的变换矩阵
            Mat m2 = Imgproc.getRotationMatrix2D(center, angle, 1);
            // 仿射变换
            Imgproc.warpAffine(srcImg, rotatedImg, m2, srcImg.size(), 1, 0, new Scalar(0));
            // 将矫正后的图片保存下来
            Imgcodecs.imwrite(filename + ".3.rotate.jpg", rotatedImg);

            // 对 ROI 区域进行抠图
            // 对旋转后的图片进行轮廓提取
            Mat secondFindImg = new Mat();
            Imgproc.cvtColor(rotatedImg, secondFindImg, COLOR_BGR2GRAY);  // 灰度化
            Imgproc.threshold(secondFindImg, secondFindImg, 80, 200, THRESH_BINARY);

            List<MatOfPoint> contours2 = new ArrayList<>();
            Imgproc.findContours(secondFindImg, contours2, new Mat(), RETR_EXTERNAL, CHAIN_APPROX_NONE);
            for (int j = 0; j < contours2.size(); j++) {
                // 这时候其实就是一个长方形了，所以获取 rect
                Rect rect2 = Imgproc.boundingRect(contours2.get(j));
                // 面积太小的轮廓直接 pass，通过设置过滤面积大小，可以保证只拿到外框
                if (rect2.area() < 600) {
                    continue;
                }

                Mat dstImg = rotatedImg.submat(rect2);
                return dstImg;
            }
        }

        return null;
    }
}
