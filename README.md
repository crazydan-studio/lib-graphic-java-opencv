Java OpenCV 功能库
=======================================

## 本地构建 OpenCV 动态库

### 构建基础镜像

```bash
bash scripts/0.build-image.sh
```

该脚本将构建出名称为 `opencv-builder-base:latest`
的 Docker 镜像，其包含 OpenCV 构建所需的 JDK 环境。

### 构建动态库

```bash
bash scripts/6.build-lib.sh
```

该脚本将默认下载并构建以下项目的源码：

- [OpenCV 4.8.1](https://github.com/opencv/opencv/releases/tag/4.8.1):
  图像识别和处理库
- [HivisionIDPhotos-cpp](https://github.com/crazydan-studio/HivisionIDPhotos-cpp):
  [HivisionIDPhotos](https://github.com/Zeyi-Lin/HivisionIDPhotos) 的 C++ 版本实现，
  该仓库克隆自 https://github.com/zjkhahah/HivisionIDPhotos-cpp ，并在原仓库基础上为
  JNA 支持做了接口参数方面的调整，并改进了 CMake 构建配置
- [MNN 3.0.0](https://github.com/alibaba/MNN/releases/tag/3.0.0):
  HivisionIDPhotos-cpp 依赖该项目的接口做人脸识别

> Note: 在项目中可根据需要调整脚本 `scripts/6.build-lib.sh` 的构建参数和代码版本。

最终构建的 `.so` 动态库将被复制到当前工程根目录下的 `lib/` 子目录中：

```
./lib/
├── libHivisionIDphotos.so
├── libMNN_Express.so
├── libMNN.so
├── libopencv_core.so
├── libopencv_core.so.408
├── libopencv_core.so.4.8.1
├── libopencv_imgcodecs.so
├── libopencv_imgcodecs.so.408
├── libopencv_imgcodecs.so.4.8.1
├── libopencv_imgproc.so
├── libopencv_imgproc.so.408
├── libopencv_imgproc.so.4.8.1
├── libopencv_java481.so
├── libopenjp2.so
├── libopenjp2.so.2.3.0
├── libopenjp2.so.7
└── opencv-481.jar
```

> Note: 其中的软链接均为相对于所在目录的路径，不需要调整。

### 导入 OpenCV 的 Java 包

```bash
bash maven/install-file.sh \
    -g org.opencv.java \
    -a opencv-java \
    -v 4.8.1 \
    -f lib/opencv-481.jar
```

> Note: 若调整了 OpenCV 的版本，则需要注意修改版本号。

该脚本会将 `lib/opencv-481.jar` 部署到当前工程根目录下的 `maven/`
子目录中，作为项目专属的依赖仓库，在依赖该包的 Maven 模块的 `pom.xml`
中单独引入仓库配置即可：

```xml
    <repositories>
        <repository>
            <id>local-repo</id>
            <name>Local Repository</name>
            <url>file:///${project.basedir}/maven/repo</url>
        </repository>
    </repositories>
    <dependencies>
        <dependency>
            <groupId>org.opencv.java</groupId>
            <artifactId>opencv-java</artifactId>
            <version>4.8.1</version>
        </dependency>
    </dependencies>
```

## 准备 AI 模型文件

按照 [model/README.md](./model/README.md) 的说明，将相关模型下载到 `model/` 目录。

模型文件都会很大，因此，也需单独管理，再通过配置参数或环境变量向应用配置模型文件的读取路径。

## 本地开发

`lib/` 目录中的动态库需要单独管理和部署，因此，在运行单元测试和启动项目前，
需要为其执行环境设置环境变量 `LD_LIBRARY_PATH`，并指向 OpenCV 等动态库的位置：

```bash
export LD_LIBRARY_PATH=`pwd`/lib:$LD_LIBRARY_PATH
```

> Note: 在 IDE 中，可在启动配置中设置。
