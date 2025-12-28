inherit kernel

DESCRIPTION = "Linux Kernel 6.6 for K1 Board (Ported from Buildroot BSP)"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

# Force version number
PV = "6.6"

# Source code location and version control
# Point to Spacemit official repository
SRC_URI = "git://github.com/spacemit-com/linux-6.6;protocol=https;branch=k1-bl-v2.2.y;name=k1-kernel \
           file://0001-bcmdhd-fix-include-path.patch \
           file://defconfig"

# Use AUTOREV for development, replace with a stable commit SHA for reproducible builds
SRCREV_k1-kernel = "${AUTOREV}"

# Source directory after extraction
S = "${WORKDIR}/git"

# Machine compatibility
COMPATIBLE_MACHINE = "k1"

# Deploy Image file in addition to FIT Image
do_deploy:append() {
    if [ -f ${B}/arch/${ARCH}/boot/Image ]; then
        install -m 0644 ${B}/arch/${ARCH}/boot/Image ${DEPLOYDIR}/Image
    fi
}
