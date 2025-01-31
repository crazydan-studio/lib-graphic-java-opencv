#!/bin/bash
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd -P)"

BUILDER_DCR_NAME="opencv-builder"
BUILDER_DCR_IMAGE="opencv-builder-base"

SOURCE_DIR="${DIR}/../build/source"
LIB_DIR="${DIR}/../lib"

TARGET_PLATFORM="linux-x86-64"


mkdir -p "${SOURCE_DIR}" "${LIB_DIR}/${TARGET_PLATFORM}"


echo "================================================"
echo "          Run OpenCV Builder"
echo "------------------------------------------------"
echo
echo

docker rm -f ${BUILDER_DCR_NAME} || echo "Ignore"
docker run -d --name ${BUILDER_DCR_NAME} \
    -u $(id -u):$(id -g) \
    -v "${LIB_DIR}":/target \
    -v "${SOURCE_DIR}":/workspace/source \
    ${BUILDER_DCR_IMAGE} \
    sleep 100000h


echo "================================================"
echo "          Build OpenCV from Source"
echo "------------------------------------------------"
echo
echo

# https://github.com/opencv/opencv/releases
# https://github.com/opencv/opencv/archive/refs/tags/4.8.1.tar.gz
if [ ! -f "${SOURCE_DIR}/opencv-4.8.1.tar.gz" ]; then
    wget https://github.com/opencv/opencv/archive/refs/tags/4.8.1.tar.gz \
        -O "${SOURCE_DIR}/opencv-4.8.1.tar.gz"
fi

# https://delabassee.com/OpenCVJava/
docker exec -it ${BUILDER_DCR_NAME} \
    bash -c " \
        tar xzf source/opencv-4.8.1.tar.gz \
        && cmake -S opencv-4.8.1 -B build/opencv \
            -DBUILD_opencv_world=OFF -DBUILD_DOCS=OFF -DWITH_CUDA=OFF -DBUILD_EXAMPLES=OFF \
            -DBUILD_PERF_TESTS=OFF -DBUILD_TESTS=OFF \
            -DBUILD_IPP_IW=OFF -DBUILD_ITT=OFF -DBUILD_OPENEXR=OFF \
            -DBUILD_TIFF=OFF -DBUILD_WEBP=OFF -DBUILD_OPENJPEG=OFF \
            -DBUILD_JPEG=ON -DBUILD_PNG=ON \
            -DBUILD_opencv_calib3d=OFF \
            -DBUILD_opencv_dnn=OFF -DBUILD_opencv_features2d=OFF -DBUILD_opencv_flann=OFF \
            -DBUILD_opencv_gapi=OFF -DBUILD_opencv_highgui=OFF -DBUILD_opencv_ml=OFF \
            -DBUILD_opencv_objdetect=OFF -DBUILD_opencv_photo=OFF \
            -DBUILD_opencv_python_bindings_generator=OFF -DBUILD_opencv_python_tests=OFF \
            -DBUILD_opencv_stitching=OFF -DBUILD_opencv_ts=OFF -DBUILD_opencv_video=OFF \
            -DBUILD_opencv_videoio=OFF -DVIDEOIO_ENABLE_PLUGINS=OFF \
            -DVIDEOIO_ENABLE_STRICT_PLUGIN_CHECK=OFF \
            -DBUILD_SHARED_LIBS=ON -DCMAKE_BUILD_TYPE=Release \
            -DBUILD_opencv_core=ON -DBUILD_opencv_imgcodecs=ON -DBUILD_opencv_imgproc=ON \
            -DBUILD_JAVA=ON -DBUILD_opencv_java=ON -DBUILD_opencv_java_bindings_gen=ON \
            -DCMAKE_INSTALL_PREFIX=/opt/opencv \
        && make --directory=build/opencv -j4 \
        && make --directory=build/opencv install \
    " || exit 1


echo "================================================"
echo "          Build MNN from Source"
echo "------------------------------------------------"
echo
echo

# https://github.com/alibaba/MNN/releases
# https://github.com/alibaba/MNN/archive/refs/tags/3.0.0.tar.gz
if [ ! -f "${SOURCE_DIR}/MNN-3.0.0.tar.gz" ]; then
    wget https://github.com/alibaba/MNN/archive/refs/tags/3.0.0.tar.gz \
        -O "${SOURCE_DIR}/MNN-3.0.0.tar.gz"
fi

# https://github.com/alibaba/MNN/blob/7c9d9d2/CMakeLists.txt
# https://mnn-docs.readthedocs.io/en/latest/compile/engine.html
docker exec -it ${BUILDER_DCR_NAME} \
    bash -c " \
        tar xzf source/MNN-3.0.0.tar.gz \
        && cmake -S MNN-3.0.0 -B build/mnn \
            -DMNN_ARM82=OFF -DMNN_AVX2=OFF \
            -DMNN_BUILD_SHARED_LIBS=ON -DMNN_BUILD_TOOLS=OFF \
            -DCMAKE_BUILD_TYPE=Release \
            -DCMAKE_INSTALL_PREFIX=/opt/mnn \
        && make --directory=build/mnn -j4 \
        && make --directory=build/mnn install \
    " || exit 1


echo "================================================"
echo "     Build HivisionIDPhotos-cpp from Source"
echo "------------------------------------------------"
echo
echo

# https://github.com/crazydan-studio/HivisionIDPhotos-cpp
# https://github.com/crazydan-studio/HivisionIDPhotos-cpp/archive/refs/heads/master.zip
if [ ! -f "${SOURCE_DIR}/HivisionIDPhotos-cpp-master.zip" ]; then
    wget https://github.com/crazydan-studio/HivisionIDPhotos-cpp/archive/refs/heads/master.zip \
        -O "${SOURCE_DIR}/HivisionIDPhotos-cpp-master.zip"
fi

# Note:
# - 需保证 include 下的头文件与构建的依赖的代码一致
# - 通过 ldd 可以查看 so 的外部链接，其路径无关，只要在动态库的搜索路径内即可
docker exec -it ${BUILDER_DCR_NAME} \
    bash -c " \
        unzip source/HivisionIDPhotos-cpp-master.zip \
        && rm -rf HivisionIDPhotos-cpp-master/include/MNN \
        && rm -rf HivisionIDPhotos-cpp-master/include/opencv2 \
        && cp -r /opt/mnn/include/MNN HivisionIDPhotos-cpp-master/include \
        && cp -r /opt/opencv/include/opencv4/opencv2 HivisionIDPhotos-cpp-master/include \
        && cmake -S HivisionIDPhotos-cpp-master -B build/hivision \
            -DCOMPILE_LIBRARY=ON \
            -DEXTERN_LIBRARY_PATH=\"/opt/opencv/lib;/opt/mnn/lib\" \
            -DCMAKE_INSTALL_PREFIX=/opt/hivision \
        && make --directory=build/hivision -j4 \
    " || exit 1


echo "================================================"
echo "     Check Shared Libraries"
echo "------------------------------------------------"
echo
echo

docker exec -it ${BUILDER_DCR_NAME} \
    bash -c " \
        ldd /opt/opencv/share/java/opencv4/libopencv_java481.so \
        && ldd /opt/mnn/lib/libMNN.so \
        && ldd /opt/mnn/lib/libMNN_Express.so \
        && ldd build/hivision/libHivisionIDphotos.so \
    " || exit 1


echo "================================================"
echo "     Copy Shared Libraries"
echo "------------------------------------------------"
echo
echo

# 从 so 的安装位置直接复制动态库到目标目录
docker exec -it ${BUILDER_DCR_NAME} \
    bash -c " \
        cp /opt/opencv/share/java/opencv4/*.so /target/${TARGET_PLATFORM}/ \
        && cp /opt/opencv/share/java/opencv4/*.jar /target/ \
        \
        && cp /opt/opencv/lib/*.so* /target/${TARGET_PLATFORM}/ \
        && cp /opt/mnn/lib/*.so* /target/${TARGET_PLATFORM}/ \
        && cp /usr/lib/x86_64-linux-gnu/libopenjp2.so* /target/${TARGET_PLATFORM}/ \
        \
        && cp build/hivision/libHivisionIDphotos.so /target/${TARGET_PLATFORM}/ \
        \
        && chmod -x /target/${TARGET_PLATFORM}/* \
    " || exit 1


echo "================================================"
echo "          Clean Building Environment"
echo "------------------------------------------------"
echo
echo

docker rm -f ${BUILDER_DCR_NAME}


echo "================================================"
echo "                   Done!"
echo "================================================"
echo
echo
