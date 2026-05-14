SUMMARY = "K3 Board Firmware Files (Realtek WiFi/BT)"
DESCRIPTION = "Common firmware and image-specific configuration files for K3 boards."
LICENSE = "CLOSED"

SRC_URI = "file://firmware/ \
        file://weston \
        file://network"

PACKAGES =+ "${PN}-firmware ${PN}-weston ${PN}-network"

S = "${UNPACKDIR}"

do_install() {
    if [ -d "${S}/firmware/usr/lib/firmware" ]; then
        install -d ${D}${nonarch_base_libdir}/firmware
        cp -dr ${S}/firmware/usr/lib/firmware/* ${D}${nonarch_base_libdir}/firmware/
    else
        bbfatal "Firmware directory not found in ${S}/firmware/usr/lib/firmware."
    fi

    if [ -d "${S}/weston" ]; then
        if [ -d "${S}/weston/etc" ]; then
            install -d ${D}${sysconfdir}
            cp -dr ${S}/weston/etc/* ${D}${sysconfdir}/
        fi

        if [ -d "${S}/weston/usr" ]; then
            install -d ${D}${prefix}
            cp -dr ${S}/weston/usr/* ${D}${prefix}/
        fi
    fi

    if [ -d "${S}/network/etc" ]; then
        install -d ${D}${sysconfdir}
        cp -dr ${S}/network/etc/* ${D}${sysconfdir}/
    fi
}

FILES:${PN}-firmware = "/usr/lib/firmware"

FILES:${PN}-weston = " \
    ${sysconfdir}/xdg \
    ${sysconfdir}/systemd/system/weston.service.d \
    /usr/local/bin/weston-autostart.sh \
    /root \
    /usr/share \
"

FILES:${PN}-network = " \
    ${sysconfdir}/systemd/network \
"

RDEPENDS:${PN}-network = "systemd"

COMPATIBLE_MACHINE = "(k3)"
