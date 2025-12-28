# meta-k1 — Overview

This layer provides BSP, kernel, bootloader, firmware, and image build support for the K1 platform. The main files/directories and their purposes are listed below to help quickly locate development entry points.

- `COPYING.MIT`
  - License file (MIT).

- `setup.sh`
  - Script to initialize the build environment. Run `source meta-k1/setup.sh` to set up environment variables and paths.

- `conf/`
  - `layer.conf`: Layer metadata (priority, dependencies, etc.).
  - `machine/k1.conf`: K1 machine configuration (MACHINE-related variables, kernel/bootloader settings).
  - `machine/include/k1-graphics.inc`: Graphics-related configuration snippets (GPU/Wayland, etc.).

- `recipes-bsp/`
  - Recipes and patches related to platform boot and firmware:
    - `opensbi/opensbi-k1_1.3.bb`: Customized OpenSBI recipe.
    - `u-boot/`: U-Boot recipes, patches, and default environment files (e.g. `env_k1-x.txt`).

- `recipes-core/`
  - `images/`: Image recipes (`core-image-minimal.bb`, `core-image-weston.bb`) and `files/config-overlay/`
    (init scripts, `etc` configs, udev rules, xdg configs, etc.) used to customize the rootfs and boot behavior.
  - `initramfs/`: e.g. `initramfs-k1.bb`, used to generate an initramfs.

- `recipes-graphics/`
  - GPU/graphics-related recipes (PowerVR images, Mesa patches, Weston/systemd services and configuration files, etc.).

- `recipes-kernel/`
  - Kernel recipes and patches (e.g. `linux-k1-dev_6.6.bb`, along with `defconfig` and patch files).

- `wic/`
  - WKS files describing SD card partitioning and image layout (e.g. `k1.wks`).
