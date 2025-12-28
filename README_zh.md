English README: [README.md](README.md)

# 进迭时空K1 Yocto SDK

本项目为 SpacemiT K1 平台提供一套 Yocto 层与构建脚本（通过 `meta-k1/setup.sh` 初始化），用于生成可刷写到 SDCard 的镜像。

## 功能简介

### minimal镜像

极简 Linux，适用于快速启动、串口调试与基本网络测试。

https://github.com/user-attachments/assets/cf9a4058-5692-448b-b522-c6c7db1c3306

### weston镜像

带 Weston（Wayland）合成器的图形环境，支持在 K1 上运行简单图形应用（可使用K1上的GPU进行加速）。

https://github.com/user-attachments/assets/ea4555da-246d-40fd-a94d-cfec4cb29faf

## 固件下载与刷机

- **下载**：

  - [最新镜像](https://github.com/yingjie-liu-spacemit/spacemit-yocto/releases/)

- **刷入示例（写入 SD 卡，操作前请确认目标设备）**：

  ```bash
  sudo dd if=core-image-minimal.wic of=/dev/sdX bs=4M conv=fsync status=progress
  sudo sync
  ```

- **固件 root 用户密码**：空（输入密码时直接按下回车）。

## 从本项目编译固件

### 运行环境与版本说明

- 建议：Ubuntu 22.04/24.04、WSL2 或等效 Linux 环境。
- 必要工具：`git`、`bash`、`python3` 及 Yocto 宿主依赖（参考 Yocto 官方文档）。

### 步骤

```bash
git clone https://github.com/yingjie-liu-spacemit/spacemit-yocto.git/'
cd spacemit-yocto
git submodule update --init --recursive
source meta-k1/setup.sh
bitbake core-image-minimal # 或 bitbake core-image-weston
```

### 构建产物

- 存放在`build/tmp/deploy/images/k1/`（包含 `.wic`、bootfs等）。

## 反馈与贡献

- 请在 Issues 中提交问题，附上开发板型号、复现步骤与日志。
- 欢迎提交 PR，使用 conventional commits 并包含测试用例以提高通过率。
