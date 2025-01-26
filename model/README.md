人像抠图和人脸识别模型文件的存放目录
===================================

> - 实际使用时，可通过配置参数或环境变量向应用配置模型文件的读取路径
> - 本地开发时，可通过软链接将模型文件链接到该目录中的同名文件上

## 准备人像抠图模型文件

下载人像抠图模型文件：
- [modnet_photographic_portrait_matting.onnx](https://github.com/Zeyi-Lin/HivisionIDPhotos/releases/download/pretrained-model/modnet_photographic_portrait_matting.onnx):
  [MODNet](https://github.com/ZHKKKe/MODNet) 官方权重
- [hivision_modnet.onnx](https://github.com/Zeyi-Lin/HivisionIDPhotos/releases/download/pretrained-model/hivision_modnet.onnx):
  对纯色换底适配性更好的抠图模型
- [birefnet-v1-lite](https://github.com/ZhengPeng7/BiRefNet/releases/download/v1/BiRefNet-general-bb_swin_v1_tiny-epoch_232.onnx):
  [ZhengPeng7](https://github.com/ZhengPeng7/BiRefNet) 开源的抠图模型，拥有最好的分割精度

> 注意，`MNN` 似乎还不支持 `birefnet-v1-lite`，加载会报错。

## 准备人脸识别模型文件

下载人脸识别模型文件：
- [symbol_10_320_20L_5scales_v2_deploy.mnn](https://github.com/zjkhahah/HivisionIDPhotos-cpp/releases/tag/v1.0/symbol_10_320_20L_5scales_v2_deploy.mnn)
- [symbol_10_320_20L_8scales_v2_deploy.mnn](https://github.com/zjkhahah/HivisionIDPhotos-cpp/releases/tag/v1.0/symbol_10_320_20L_8scales_v2_deploy.mnn)

具体选择哪一个模型，由配置项 `HivisionConfig#faceDetectingModelScale`
的值决定，若其值为 `5`，则将使用 `symbol_10_320_20L_5scales_v2_deploy.mnn`，若其值为
`8`，则会使用 `symbol_10_320_20L_8scales_v2_deploy.mnn`。

> Note: 目前不清楚二者的区别，二者的效果似乎是相同的。
