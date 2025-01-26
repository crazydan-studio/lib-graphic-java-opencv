package org.crazydan.studio.library.graphic.hivision;

import com.sun.jna.Library;

/**
 * 注意，该接口将与本地动态库做函数同名映射，因此，不能改变函数的名称和参数顺序
 *
 * @author <a href="mailto:flytreeleft@crazydan.org">flytreeleft</a>
 * @date 2024-12-10
 */
public interface HivisionJNA extends Library {
    /** 动态库的名称：文件名去掉开头的 <code>lib</code> 和结尾的 <code>.so</code> 便是其库名称 */
    String NATIVE_LIBRARY_NAME = "HivisionIDphotos";

    /** 输出匹配到人像的图片 */
    void human_mating(HivisionConfig config);

    /**
     * 输出制作好的证件照图片
     *
     * @param outImageSizeInKB
     *         输出照片大小，仅大于 0 时有效，单位 <code>KB</code>。最终将同时生成该指定大小的照片
     * @param layoutPhotos
     *         是否输出多张排版照片
     * @return 若成功处理，则返回 <code>1</code>，否则，返回 <code>0</code>
     */
    int ID_photo(HivisionConfig config, int outImageSizeInKB, boolean layoutPhotos);
}
