SUMMARY = "RISC-V Open Source Supervisor Binary Interface (OpenSBI) for Spacemit K1"
DESCRIPTION = "OpenSBI implementation for Spacemit K1 RISC-V processor"
HOMEPAGE = "https://github.com/riscv/opensbi"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING.BSD;md5=42dd9555eb177f35150cf9aa240b61e5"

require recipes-bsp/opensbi/opensbi-payloads.inc

inherit autotools-brokensep deploy

DEPENDS += "u-boot-tools-native dtc-native"

SRCREV = "${AUTOREV}"
SRC_URI = "git://github.com/spacemit-com/opensbi;protocol=https;branch=k1-bl-v2.2.y"

S = "${WORKDIR}/git"

RISCV_SBI_PLAT = "generic"
OPENSBI_DEFCONFIG = "k1_defconfig"

EXTRA_OEMAKE += "PLATFORM=${RISCV_SBI_PLAT} I=${D} FW_PIC=y"
EXTRA_OEMAKE += "PLATFORM_DEFCONFIG=${OPENSBI_DEFCONFIG}"

do_install(){
	:
}

do_deploy() {
	install -d ${DEPLOYDIR}

	BUILD_DIR="${S}/build/platform/${RISCV_SBI_PLAT}/firmware"

	for file in fw_dynamic.bin fw_dynamic.elf fw_dynamic.itb fw_jump.bin fw_jump.elf fw_payload.bin fw_payload.elf; do
		if [ -f "${BUILD_DIR}/${file}" ]; then
			install -m 0644 "${BUILD_DIR}/${file}" "${DEPLOYDIR}/${file}"
		fi
	done
}

addtask deploy before do_build after do_install

COMPATIBLE_HOST = "(riscv64|riscv32).*"
INHIBIT_PACKAGE_STRIP = "1"

PROVIDES += "opensbi"
