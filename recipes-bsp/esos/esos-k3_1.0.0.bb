SUMMARY = "K3 ESOS firmware (prebuilt)"
DESCRIPTION = "Prebuilt ESOS firmware for K3: rt24 RCPU .elf files and esos.itb boot image. \
The .elf files are loaded by the kernel from /lib/firmware, and esos.itb is loaded by FSBL before U-Boot."
LICENSE = "CLOSED"

inherit deploy

SRC_URI = " \
    file://rt24_os0_rcpu.elf \
    file://rt24_os1_rcpu.elf \
    file://esos.itb \
"

S = "${UNPACKDIR}"

do_install() {
    install -d ${D}/lib/firmware
    install -m 0644 ${UNPACKDIR}/rt24_os0_rcpu.elf ${D}/lib/firmware/
    install -m 0644 ${UNPACKDIR}/rt24_os1_rcpu.elf ${D}/lib/firmware/
}

do_deploy() {
    install -d ${DEPLOYDIR}
    install -m 0644 ${UNPACKDIR}/esos.itb ${DEPLOYDIR}/
}

addtask deploy after do_install

FILES:${PN} = "/lib/firmware"

INSANE_SKIP:${PN} = "usrmerge arch already-stripped buildpaths"

COMPATIBLE_MACHINE = "(k3)"
