SUMMARY = "Minimal image with Weston compositor for K1"
LICENSE = "MIT"
PR = "r0"

inherit core-image

require recipes-core/images/include/k1-image.inc

SYSTEMD_DEFAULT_TARGET = "graphical.target"

# Install Weston and common Wayland stack components
IMAGE_INSTALL:append = " \
    weston \
    weston-init \
    wayland \
    libinput \
    libdrm \
    libxkbcommon \
    xwayland \
    mesa3d \
    img-gpu-powervr \
    kmscube \
    glmark2 \
"
