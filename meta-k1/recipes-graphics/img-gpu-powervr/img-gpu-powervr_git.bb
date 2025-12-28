SUMMARY = "IMG PowerVR GPU drivers for K1"
DESCRIPTION = "PowerVR GPU userspace drivers and firmware for Spacemit K1 SoC"
LICENSE = "CLOSED"

# Use Spacemit official repository
SRC_URI = "git://github.com/spacemit-com/img-gpu-powervr;protocol=https;branch=k1-bl-v2.2.y"
SRCREV = "${AUTOREV}"

S = "${WORKDIR}/git"

# Don't provide virtual packages - Mesa3D will provide those
# This package only provides the proprietary PowerVR runtime libraries and firmware

DEPENDS = "wayland libdrm"
RDEPENDS:${PN} = "libdrm wayland udev libxcb libx11 libxshmfence"

COMPATIBLE_MACHINE = "k1"

# This is a binary package, no compilation needed
do_configure[noexec] = "1"
do_compile[noexec] = "1"

CONFLICT_HEADERS = "GLES* GL* EGL* vulkan drm gbm KHR wayland* X11"

do_install() {
    # 1. Create basic directory structure
    install -d ${D}${sysconfdir} \
               ${D}${libdir} \
               ${D}${nonarch_libdir}/firmware

    # 2. Install configuration files (etc)
    if [ -d ${S}/target/etc ]; then
        cp -rf ${S}/target/etc/* ${D}${sysconfdir}/
    fi

    # 3. Install firmware
    if [ -d ${S}/target/lib/firmware ]; then
        cp -rf ${S}/target/lib/firmware/* ${D}${nonarch_libdir}/firmware/
    fi

    # 4. Install main library files (usr/lib)
    if [ -d ${S}/target/usr/lib ]; then
        for lib in ${S}/target/usr/lib/lib*.so*; do
            [ -f "$lib" ] || continue
            libname=$(basename "$lib")
            case "$libname" in
                libGLESv1.so*|libGLESv1.so.*|libGLESv2.so*|libEGL.so*|libvulkan.so*)
                    continue 
                    ;;
                *) 
                    install -m 0755 "$lib" ${D}${libdir}/ 
                    ;;
            esac
        done
        
        if [ -d ${S}/target/usr/lib/riscv64-linux-gnu ]; then
            install -d ${D}${libdir}/riscv64-linux-gnu
            cp -r ${S}/target/usr/lib/riscv64-linux-gnu/* ${D}${libdir}/riscv64-linux-gnu/
        fi
    fi

    # 5. Handle /usr/local
    if [ -d ${S}/target/usr/local ]; then
        install -d ${D}${prefix}/local
        cp -rf ${S}/target/usr/local/* ${D}${prefix}/local/ 2>/dev/null || true
    fi

    # 6. Handle header files (staging/include)
    if [ -d ${S}/staging/include ]; then
        install -d ${D}${includedir}
        for h in ${S}/staging/include/*; do
            [ -e "$h" ] || continue
            hname=$(basename "$h")
            case "$hname" in
                GLES*|GL*|EGL*|vulkan|drm|gbm|KHR|wayland*|X11)
                    continue 
                    ;;
                *) 
                    cp -rf "$h" ${D}${includedir}/ 
                    ;;
            esac
        done
    fi

    # Key fix: Force all installed files to be owned by root to completely resolve Host Contamination issue
    chown -R root:root ${D}
}

# Skip QA checks for pre-built binaries
INSANE_SKIP:${PN} = "already-stripped ldflags dev-so textrel arch file-rdeps libdir"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

# Allow empty directories
ALLOW_EMPTY:${PN} = "1"

FILES:${PN} += " \
    ${sysconfdir} \
    ${nonarch_libdir}/firmware \
    ${libdir}/* \
    ${prefix}/local \
    ${datadir}/X11/xorg.conf.d/* \
"

FILES:${PN}-dev = "${includedir}"


