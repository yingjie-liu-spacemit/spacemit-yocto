SUMMARY = "K1 Board Custom Initramfs"
DESCRIPTION = "Custom initramfs with init script for mounting rootfs and switching root"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

PACKAGE_INSTALL = " \
    busybox \
    base-passwd \
    udev \
    util-linux \
    util-linux-switch-root \
    e2fsprogs \
    e2fsprogs-e2fsck \
    e2fsprogs-resize2fs \
    mtd-utils \
    kmod \
"

inherit core-image

IMAGE_FSTYPES = "cpio.gz"
IMAGE_ROOTFS_SIZE = "8192"
IMAGE_ROOTFS_EXTRA_SPACE = "0"

ROOTFS_POSTPROCESS_COMMAND += "install_custom_init; "

install_custom_init() {
    OVERLAY_DIR="${THISDIR}/../images/files/config-overlay"
    TARGET_DIR="${IMAGE_ROOTFS}"
    
    # Install complete overlay using tar to preserve symlinks
    if [ -d "${OVERLAY_DIR}" ]; then
        cd "${OVERLAY_DIR}" && tar cf - . | tar xf - -C "${TARGET_DIR}"
    fi
    
    # Create symlink /lib -> /usr/lib (Ensure esos.elf is available in initramfs for early boot firmware loading)
    ln -sf /usr/lib ${TARGET_DIR}/lib
    
    [ -f "${TARGET_DIR}/init" ] && chmod 755 "${TARGET_DIR}/init"
}

PROVIDES = "virtual/initramfs"
