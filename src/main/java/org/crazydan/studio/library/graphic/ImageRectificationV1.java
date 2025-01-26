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
import java.util.Arrays;
import java.util.List;

import org.crazydan.studio.library.graphic.opencv.OpenCVLib;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import static org.opencv.imgproc.Imgproc.CHAIN_APPROX_NONE;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.RETR_EXTERNAL;

/**
 * 图像校正
 * <p/>
 * 校正歪曲的图像，使其恢复水平平直状态
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2024-12-06
 */
public class ImageRectificationV1 extends OpenCVLib {

    /** 参考实现《<a href="https://juejin.cn/post/6844903495145914382">Android 端基于 OpenCV 的边框识别功能</a>》 */
    public static Mat correct(String filename) {
        Mat src = Imgcodecs.imread(filename);

        // 灰度化
        Mat gray = new Mat();
        Imgproc.cvtColor(src, gray, COLOR_BGR2GRAY);
        Imgcodecs.imwrite(filename + ".0.gray.jpg", gray);

        Mat blur = new Mat();
        Imgproc.GaussianBlur(gray, blur, new Size(5, 5), 0);
        Imgcodecs.imwrite(filename + ".1.blur.jpg", blur);

        Mat canny = new Mat();
        Imgproc.Canny(blur, canny, 0, 5);
        Imgcodecs.imwrite(filename + ".2.canny.jpg", canny);

        List<MatOfPoint> contours = new ArrayList<>();
        // 找轮廓，注意第 5 个参数为 RETR_EXTERNAL，只检索外框
        Imgproc.findContours(canny, contours, new Mat(), RETR_EXTERNAL, CHAIN_APPROX_NONE);
        // 按面积排序，以找出最大面积的边界
        contours.sort((a, b) -> (int) (Math.abs(Imgproc.contourArea(b)) - Math.abs(Imgproc.contourArea(a))));

        MatOfPoint2f contour = new MatOfPoint2f(contours.get(0).toArray());
        double arc = Imgproc.arcLength(contour, true);

        MatOfPoint2f outDP = new MatOfPoint2f();
        // 使用 approxPolyDP 多边形逼近来减少线段数量，期望是四边形，也就是 4 条线段
        Imgproc.approxPolyDP(contour, outDP, 0.02 * arc, true);

        // 筛选去除相近的点
        MatOfPoint2f selectedPoints = selectPoints(outDP, 1);
        if (selectedPoints.total() != 4) {
            // 如果筛选出来之后不是四边形，那么使用最小矩形包裹
            RotatedRect rect = Imgproc.minAreaRect(contour);
            Point[] rectPoints = new Point[4];
            rect.points(rectPoints);

            selectedPoints.fromArray(rectPoints);
        }

//        double scale = Math.min(500f / srcImg.rows(), 500f / srcImg.cols());
//        Point[] points = selectedPoints.toArray();
//        for (Point point : points) {
//            point.x *= scale;
//            point.y *= scale;
//        }
//        selectedPoints.fromArray(points);
//
//        selectedPoints = sortPointClockwise(selectedPoints);
//
//        Mat dst = Mat.zeros(500, 500, srcImg.type());
//        MatOfPoint2f srcTriangle = new MatOfPoint2f(selectedPoints);
//        MatOfPoint2f dstTriangle = new MatOfPoint2f(new Point(0, 0),
//                                                    new Point(dst.rows(), 0),
//                                                    new Point(0, dst.cols()),
//                                                    new Point(dst.rows(), dst.cols()));
//
//        Mat transform = Imgproc.getPerspectiveTransform(srcTriangle, dstTriangle);
//        Imgproc.warpPerspective(srcImg, dst, transform, dst.size());

        RotatedRect rect = Imgproc.minAreaRect(selectedPoints);
        Point center = rect.center;  // 中心点
        double angle = rect.angle;
        // 计算旋转加缩放的变换矩阵
        Mat m2 = Imgproc.getRotationMatrix2D(center, angle, 1);

        // 创建一个旋转后的图像
        Mat rotated = new Mat(src.rows() * 2, src.cols() * 2, src.type());
        rotated.setTo(new Scalar(0));
        // 仿射变换
        Imgproc.warpAffine(src, rotated, m2, src.size(), 1, 0, new Scalar(0));

        return rotated;
    }

    private static MatOfPoint2f selectPoints(MatOfPoint2f mat, int selectTimes) {
        if (mat.total() <= 4) {
            return mat;
        }

        double arc = Imgproc.arcLength(mat, true);
        Point[] points = mat.toArray();

        List<Point> list = new ArrayList<>();
        list.add(points[0]);

        for (int i = 1; i < points.length; i++) {
            Point prev = points[i - 1];
            Point curr = points[i];

            double pointLength = Math.sqrt(Math.pow((curr.x - prev.x), 2) + Math.pow((curr.y - prev.y), 2));
            if (pointLength < arc * 0.01 * selectTimes && mat.total() > 4) {
                continue;
            }

            list.add(curr);
        }

        mat.fromList(list);
        if (mat.total() > 4) {
            return selectPoints(mat, selectTimes + 1);
        }
        return mat;
    }

    private static MatOfPoint2f sortPointClockwise(MatOfPoint2f mat) {
        if (mat.total() != 4) {
            return mat;
        }

        Point unfoundPoint = new Point();
        Point[] result = new Point[] { unfoundPoint, unfoundPoint, unfoundPoint, unfoundPoint };

        double minDistance = -1;
        Point[] points = mat.toArray();
        List<Point> list = new ArrayList<>(Arrays.asList(points));
        for (Point point : points) {
            double distance = point.x * point.x + point.y * point.y;

            if (minDistance == -1 || distance < minDistance) {
                result[0] = point;
                minDistance = distance;
            }
        }

        Point leftTop = result[0];
        if (leftTop != unfoundPoint) {
            list.remove(leftTop);

            if ((pointSideLine(leftTop, points[0], points[1]) * pointSideLine(leftTop, points[0], points[2])) < 0) {
                result[2] = points[0];
            } else if ((pointSideLine(leftTop, points[1], points[0]) * pointSideLine(leftTop, points[1], points[2]))
                       < 0) {
                result[2] = points[1];
            } else if ((pointSideLine(leftTop, points[2], points[0]) * pointSideLine(leftTop, points[2], points[1]))
                       < 0) {
                result[2] = points[2];
            }
        }

        Point rightBottom = result[2];
        if (leftTop != unfoundPoint && rightBottom != unfoundPoint) {
            list.remove(rightBottom);

            if (pointSideLine(leftTop, rightBottom, points[0]) > 0) {
                result[1] = points[0];
                result[3] = points[1];
            } else {
                result[1] = points[1];
                result[3] = points[0];
            }
        }

        if (result[0] != unfoundPoint
            && result[1] != unfoundPoint
            && result[2] != unfoundPoint
            && result[3] != unfoundPoint) {
            mat.fromArray(result);
        } else {
//            mat.fromList(list);
        }
        return mat;
    }

    private static double pointSideLine(Point lineP1, Point lineP2, Point point) {
        double x1 = lineP1.x;
        double y1 = lineP1.y;
        double x2 = lineP2.x;
        double y2 = lineP2.y;
        double x = point.x;
        double y = point.y;

        return (x - x1) * (y2 - y1) - (y - y1) * (x2 - x1);
    }
}
