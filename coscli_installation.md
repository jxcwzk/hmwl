# COSCLI 工具安装解决方案

## 问题描述
- 尝试使用 `pip install coscli` 安装腾讯云 COSCLI 工具失败
- 错误信息：`UnicodeDecodeError: 'gbk' codec can't decode byte 0x80 in position 26: illegal multibyte sequence`
- 原因：Windows 系统默认编码为 GBK，与 coscli 安装包中的 UTF-8 编码文件冲突

## 解决方案
### 方案一：直接下载可执行文件（推荐）
1. **下载地址**：
   - 腾讯云官方文档：https://cloud.tencent.com/document/product/436/63144#56513f26-4a13-4835-b8d1-c2038e70c1b5
   - GitHub 发布页面：https://github.com/tencentyun/coscli/releases

2. **安装步骤**：
   - 下载对应操作系统的 coscli 可执行文件
   - 将下载的文件添加到系统环境变量中
   - 运行 `coscli config set -a default -i <SecretId> -k <SecretKey> -r <region>` 配置密钥和地域
   - 运行 `coscli ls <bucket-name-appid>` 测试列出存储桶

### 方案二：使用 Python SDK
- 已成功安装 `cos-python-sdk-v5`：`python -m pip install cos-python-sdk-v5`
- 可以使用 Python SDK 进行 COS 相关操作

## 下一步计划
1. 从官方网站下载 coscli 可执行文件
2. 配置系统环境变量
3. 测试 coscli 工具的基本功能
4. 验证与后端服务的集成

## 备注
- 目前后端服务已成功运行，能够处理 COS 上传失败的情况
- 数据库连接问题已修复，添加了 `allowPublicKeyRetrieval=true` 参数
- 测试脚本已修改，能够正确处理后端服务的响应格式