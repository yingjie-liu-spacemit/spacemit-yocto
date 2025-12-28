#!/bin/bash
# Bootstrapper for meta-k1 development environment
# Adapted from meta-riscv/setup.sh to initialize a build for the K1 board.

DIR="build"
MACHINE="k1"
DISTRO="poky"
CONFFILE="conf/auto.conf"
# Default image to build (change as needed)
BITBAKEIMAGE="core-image-minimal"

# Reconfigure dash on Debian-like systems (same logic as original script)
which aptitude > /dev/null 2>&1
ret=$?
if [ "$(readlink /bin/sh)" = "dash" -a "$ret" = "0" ]; then
  sudo aptitude install expect -y
  expect -c 'spawn sudo dpkg-reconfigure -freadline dash; send "n\n"; interact;'
elif [ "${0##*/}" = "dash" ]; then
  echo "dash as default shell is not supported"
  return
fi

# Determine repository root (works when the script is sourced)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]:-$0}")" >/dev/null 2>&1 && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." >/dev/null 2>&1 && pwd)"

# Prefer a top-level `bitbake` (sibling to openembedded-core) if present;
# add its `bin` directory to PATH before initializing the OE environment.
BITBAKE_LOCAL="$REPO_ROOT/bitbake"
if [ -x "$BITBAKE_LOCAL/bin/bitbake" ]; then
  export PATH="$BITBAKE_LOCAL/bin:$PATH"
fi

# Compute absolute build path (before oe-init-build-env may change PWD)
BUILD_PATH="$REPO_ROOT/$DIR"

# Bootstrap OE environment
echo "Init OE"
. "$REPO_ROOT/openembedded-core/oe-init-build-env" $DIR

# Add commonly used layers for K1 development
echo "Adding layers"
bitbake-layers add-layer ../meta-openembedded/meta-oe || true
bitbake-layers add-layer ../meta-riscv || true
bitbake-layers add-layer ../meta-yocto/meta-poky || true
bitbake-layers add-layer ../meta-k1 || true
# Optionally add other meta-openembedded sublayers if you need them
# bitbake-layers add-layer ../meta-openembedded/meta-python
# bitbake-layers add-layer ../meta-openembedded/meta-networking

# Create/overwrite auto.conf for this bootstrap
echo "Creating auto.conf"
if [ -e $CONFFILE ]; then
    rm -f $CONFFILE
fi
cat <<EOF > $CONFFILE
MACHINE ?= "${MACHINE}"
DISTRO = "${DISTRO}"

# Package management
PACKAGE_CLASSES = "package_ipk"

# Graphics: Add IMG PowerVR driver to weston images
IMAGE_INSTALL:append = " img-gpu-powervr  dropbear"

# Build history tracking
USER_CLASSES:append = " buildstats buildhistory buildstats-summary"

# ----------------------------------------------------------------------
# Systemd Configuration
# ----------------------------------------------------------------------

# Use systemd instead of sysvinit as the init manager
DISTRO_FEATURES:append = " systemd usrmerge pam"
DISTRO_FEATURES:remove = " sysvinit"
VIRTUAL-RUNTIME_init_manager = "systemd"
VIRTUAL-RUNTIME_initscripts = ""
DISTRO_FEATURES_BACKFILL_CONSIDERED = "sysvinit"

# Ensure kernel supports systemd (cgroups, fanotify, etc.)
KERNEL_FEATURES:append = " features/systemd/systemd.scc"

# Explicitly install systemd in minimal images
IMAGE_INSTALL:append = " systemd systemd-serialgetty"
EOF

# Uncomment common build directory settings in build/conf/local.conf
LOCALCONF="conf/local.conf"
if [ -f "$LOCALCONF" ]; then
  echo "Uncommenting TMPDIR, SSTATE_DIR and DL_DIR in $LOCALCONF"
  sed -i -E 's|^[[:space:]]*#([[:space:]]*(TMPDIR[[:space:]]*=.*))|\1|' "$LOCALCONF"
  sed -i -E 's|^[[:space:]]*#([[:space:]]*(SSTATE_DIR[[:space:]]*\?=[[:space:]]*.*))|\1|' "$LOCALCONF"
  sed -i -E 's|^[[:space:]]*#([[:space:]]*(DL_DIR[[:space:]]*\?=[[:space:]]*.*))|\1|' "$LOCALCONF"
fi


cat <<EOF
Bootstrap complete.
To build an image run:
  bitbake ${BITBAKEIMAGE}

Useful info:
- Build directory: 
  ${PWD}/${DIR}
- Default machine: ${MACHINE}
- Default image: ${BITBAKEIMAGE}

You can edit ${CONFFILE} to change MACHINE/DISTRO or add IMAGE_INSTALL items.
EOF
