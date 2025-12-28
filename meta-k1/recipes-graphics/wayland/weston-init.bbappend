FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# Inherit systemd class for proper service handling
inherit systemd

# Specify systemd service included in this package
SYSTEMD_SERVICE:${PN} = "weston.service"

# Enable service auto-start
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

# Install custom weston.ini
do_install:append() {
    if [ -f ${WORKDIR}/weston.ini ]; then
        install -d ${D}${sysconfdir}/xdg/weston
        install -m 0644 ${WORKDIR}/weston.ini ${D}${sysconfdir}/xdg/weston/weston.ini
    fi
}