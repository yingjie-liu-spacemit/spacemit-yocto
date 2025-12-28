DESCRIPTION = "U-Boot bootloader for K1 Board"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=2ca5f2c35c8cc335f0a19756634782f1"

require recipes-bsp/u-boot/u-boot.inc

DEPENDS += "bc-native dtc-native bison-native flex-native u-boot-tools-native"

# Force version number
PV = "2022.10"

# Point to Spacemit official repository
SRC_URI = "git://github.com/spacemit-com/uboot-2022.10;protocol=https;branch=k1-bl-v2.2.y \
           file://0001-Disable-source-tree-clean-check.patch \
           file://env_k1-x.txt"

SRCREV = "${AUTOREV}"

S = "${WORKDIR}/git"

COMPATIBLE_MACHINE = "k1"
UBOOT_MAKE_TARGET = "all"

# Do not try to install boot.scr.uimg if it doesn't exist
# K1 U-Boot may not generate this file
SPL_BINARY = ""
UBOOT_ENV = ""

# Remove boot.scr.uimg from boot files since K1 uses custom bootinfo
IMAGE_BOOT_FILES:remove = "boot.scr.uimg"

do_deploy:append() {
    # Deploy all bootinfo variants for different storage types
    for target in sd emmc spinand spinor; do
        if [ -f "${S}/bootinfo_${target}.bin" ]; then
            install -m 644 "${S}/bootinfo_${target}.bin" "${DEPLOYDIR}/bootinfo_${target}.bin"
        fi
    done
    
    [ -f "${S}/FSBL.bin" ] && install -m 644 "${S}/FSBL.bin" "${DEPLOYDIR}/FSBL.bin"
    [ -f "${B}/u-boot.itb" ] && install -m 644 "${B}/u-boot.itb" "${DEPLOYDIR}/u-boot.itb"
    [ -f "${S}/u-boot-env-default.bin" ] && install -m 644 "${S}/u-boot-env-default.bin" "${DEPLOYDIR}/u-boot-env-default.bin"
        
    # Deploy custom environment file for K1
    install -m 644 ${WORKDIR}/env_k1-x.txt ${DEPLOYDIR}/env_k1-x.txt
}

