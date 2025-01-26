package org.crazydan.studio.library.graphic.hivision;

import com.sun.jna.Structure;

/**
 * 注意，必须实现接口 {@link Structure.ByValue}，其表示结构体以 值 方式传入接口（另一种为 {@link Structure.ByReference 引用}），
 * 该接口的实现中的 {@link String} 将会自动通过 {@link com.sun.jna.Pointer Pointer} 做 <code>char*</code> 转换，
 * 否则，会出现指针引用等问题
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2024-12-10
 */
@Structure.FieldOrder({
        // 必须按 C/C++ 代码中结构体成员的位置，按顺序做映射
        "portraitMattingModelFile",
        "faceDetectingModelDir",
        "srcImageFile",
        "outImageDir",
        "outImageType",
        "outImageBgColor",
        "useThreadNumber",
        "faceDetectingModelScale",
        "portraitMeasureRatio",
        "outImageWidth",
        "outImageHeight",
})
public class HivisionConfig extends Structure implements Structure.ByValue {
    /** 人像抠图模型文件 */
    // https://github.com/Zeyi-Lin/HivisionIDPhotos#user-content-3-%E4%B8%8B%E8%BD%BD%E4%BA%BA%E5%83%8F%E6%8A%A0%E5%9B%BE%E6%A8%A1%E5%9E%8B%E6%9D%83%E9%87%8D%E6%96%87%E4%BB%B6
    public String portraitMattingModelFile;
    /** 人脸检测模型目录，由 {@link #faceDetectingModelScale} 确定模型文件名称 */
    public String faceDetectingModelDir;
    /**
     * 人脸检测模型类型
     * <p/>
     * 可选值：5, 8，其分别对应模型文件
     * <code>symbol_10_320_20L_5scales_v2_deploy.mnn</code>
     * 和 <code>symbol_10_560_25L_8scales_v1_deploy.mnn</code>
     */
    public int faceDetectingModelScale = 8;

    /** 并发线程数：默认为 CPU 核心数 */
    public int useThreadNumber = Runtime.getRuntime().availableProcessors();
    /** 人像在图片中的比例 */
    public float portraitMeasureRatio = 0.35f;

    /** 源图片的文件路径 */
    public String srcImageFile;
    /** 输出图片文件的存放目录 */
    public String outImageDir;

    /** 输出图片的文件类型 */
    public String outImageType = "jpg";
    /** 输出图片背景色 */
    public HivisionColor outImageBgColor = new HivisionColor(255, 255, 255);
    /** 输出图片宽度 */
    public int outImageWidth = 295;
    /** 输出图片高度 */
    public int outImageHeight = 413;
}

