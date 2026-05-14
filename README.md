# meta-riscv

RISC-V Architecture Layer for OpenEmbedded/Yocto

## Description

This is the general hardware-specific BSP overlay for RISC-V based devices. The core BSP portion should work with different OpenEmbedded/Yocto distributions and layer stacks.

**K3 Platform Support:**
This layer has been extended from [meta-riscv](https://github.com/riscv/meta-riscv) (commit: `41a010a`) to include full support for the **K3 platform**. It supports multiple K3 boards and provides two image output formats:
- **SD Card (WIC)**: Directly bootable `.wic` image for SD card flashing
- **Titan**: Partition images for use with the SpacemiT Titan flashing tool

**Yocto Compatibility:**
This layer is developed against the **master** branch of `openembedded-core` and `bitbake` (post-Scarthgap 5.0, towards Styhead 5.1). It has not been tested against stable release branches.

## Available Machines

| MACHINE | SoC | Supported Boards |
|---------|-----|-----------------|
| `k3` | SpacemiT K3 | COM260, Pico-ITX... |

All board variants are built from a single `MACHINE=k3` target. The corresponding device trees are included in the bootfs automatically (see `conf/machine/k3.conf` for the full DTB list).

## Firmware Download

Pre-built images are available on the [Releases page](https://github.com/yingjie-liu-spacemit/spacemit-yocto/releases/tag/K3_support_v1.0).

## Build Environment

- Recommended: Ubuntu 22.04/24.04, WSL2, or equivalent Linux environment.
- Required tools: `git`, `bash`, `python3`, and Yocto host dependencies (see [Yocto Project host packages](https://docs.yoctoproject.org/ref-manual/system-requirements.html)).

Builds are performed inside a Docker container based on **Ubuntu 24.04 LTS** (x86_64).

The following packages are installed on top of the base `ubuntu:24.04` image:

```bash
sudo apt update
sudo apt install gawk wget git git-lfs diffstat unzip texinfo gcc g++ build-essential \
  chrpath cpio python3 python3-pip python3-pexpect python3-git python3-jinja2 \
  python3-subunit xz-utils zstd liblz4-tool lz4 lzop file locales curl make \
  binutils cpp dosfstools iproute2 iputils-ping mesa-common-dev rpcsvc-proto \
  subversion ssh sudo vim p7zip-full netcat-openbsd
sudo locale-gen en_US.UTF-8
```

## Quick Start (K3 Platform)

### 1. Initialize Workspace

Use the `repo` tool to initialize the source code:

```bash
mkdir riscv-yocto && cd riscv-yocto
repo init -u https://github.com/yingjie-liu-spacemit/spacemit-yocto.git -b k3 -m tools/manifests/riscv-yocto.xml
repo sync
repo start work --all
```

### 2. Set Up Build Environment

Run the following command to configure your environment:

```bash
. layers/meta-riscv/tools/envsetup.sh
```

### 3. Build Images

Both targets produce `.wic` (SD card) and `.ext4` + Titan partition images simultaneously.

**Minimal Linux, suitable for quick boot, serial debugging, and basic network testing:**

```bash
MACHINE=k3 bitbake core-image-minimal
```

![minimal](https://github.com/user-attachments/assets/25c1ffb9-86ae-421d-9168-54a4a11c2da1)


**Graphical environment with Weston (Wayland) compositor, supporting simple graphical applications on K3 (GPU acceleration available):**

```bash
MACHINE=k3 bitbake core-image-weston
```

![weston](https://github.com/user-attachments/assets/019f4cd9-4d2a-4da3-b9d8-de7a0a51de86)

### 4. Default Credentials

| User | Password |
|------|----------|
| `root` | `bianbu` |

### 5. Flashing / Usage

After a successful build, the image files are located in `build/tmp/deploy/images/k3/`.

#### SD Card

Use the `dd` command to write the `.wic` image to your SD card (replace `/dev/sdX` with your actual device identifier):

```bash
sudo dd if=core-image-minimal-k3.rootfs.wic of=/dev/sdX bs=4M conv=fsync status=progress
sudo sync
```

*(Replace the filename accordingly if you built `core-image-weston`.)*

#### Titan Flashing Tool

Titan is SpacemiT's proprietary flashing tool for writing images via USB. The build also generates partition images compatible with Titan (bootfs.ext4, rootfs.ext4, and firmware binaries).

- [Download Titan download and usage guide ](https://spacemit.com/community/document/info?lang=zh&nodepath=tools/user_guide/titan_user_guide.md)

---

## Dependencies

* [openembedded-core](https://github.com/openembedded/openembedded-core)
* [bitbake](https://github.com/openembedded/bitbake)

## Contributing

Submit patches via GitHub pull requests. Use GitHub issues to report problems or provide feedback.

## Maintainer(s)

* liuyingjie `<yingjie.liu@spacemit.com>`
