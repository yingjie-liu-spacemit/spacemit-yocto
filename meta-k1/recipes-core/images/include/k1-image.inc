# K1 Image Common Functions
# This file contains shared functions for K1 image recipes

# Ensure dependencies are built before WIC
DEPENDS += "u-boot-k1 opensbi-k1 linux-k1-dev"

# Install overlay files BEFORE usrmerge to preserve symlinks
ROOTFS_PREPROCESS_COMMAND += "install_overlay_files; "

install_overlay_files() {
    OVERLAY_DIR="${THISDIR}/files/config-overlay"
    TARGET_DIR="${IMAGE_ROOTFS}"
    
    if [ ! -d "${OVERLAY_DIR}" ]; then
        return
    fi
    
    # Copy specified directories: etc, root, usr
    # Use tar to preserve symlinks and permissions
    for subdir in etc root usr; do
        src="${OVERLAY_DIR}/${subdir}"
        if [ -d "${src}" ]; then
            cd "${OVERLAY_DIR}" && tar cf - "${subdir}" | tar xf - -C "${TARGET_DIR}"
        fi
    done
    
    # Set executable permissions for init scripts
    initd_dir="${TARGET_DIR}/etc/init.d"
    if [ -d "${initd_dir}" ]; then
        chmod 755 "${initd_dir}"/S* 2>/dev/null || true
    fi
}

# Create bootfs partition with kernel, initramfs, env, and dtbs
do_image_wic[prefuncs] += "do_create_bootfs"

python do_create_bootfs() {
    import subprocess, os, shutil

    deploydir = d.getVar('DEPLOY_DIR_IMAGE')
    bootfs_dir = os.path.join(deploydir, 'bootfs')
    bootfs_img = os.path.join(deploydir, 'bootfs.ext4')
    machine = d.getVar('MACHINE')

    # 1. Reset and prepare bootfs directory
    bb.utils.remove(bootfs_dir, recurse=True)
    bb.utils.mkdirhier(bootfs_dir)

    # Define helper function for copy logic
    def copy_file(src_name, dst_name=None):
        src = os.path.join(deploydir, src_name)
        if os.path.exists(src):
            shutil.copy2(src, os.path.join(bootfs_dir, dst_name or src_name))
            return True
        return False

    # 2. Copy kernel, Initramfs, Env and DTB
    # Kernel priority order
    if not any(copy_file(f) for f in [f'fitImage-{machine}.bin', 'Image']):
        bb.warn("Kernel Image not found")

    # Initramfs (take the first match)
    initramfs = next((f for f in os.listdir(deploydir) if f.startswith('initramfs-') and f.endswith('.cpio.gz')), None)
    if initramfs: copy_file(initramfs, 'initramfs-generic.img')

    # U-Boot Env & DTBs
    copy_file('env_k1-x.txt')
    dtbs = [f for f in os.listdir(deploydir) if f.endswith('.dtb')]
    for dtb in dtbs: copy_file(dtb)

    # 3. Calculate size and generate image (add 15% margin for simplicity)
    import math
    total_size_kb = sum(os.path.getsize(os.path.join(bootfs_dir, f)) for f in os.listdir(bootfs_dir)) / 1024
    calc_mb = math.ceil((total_size_kb * 1.15) / 1024) + 1
    final_mb = max(int(d.getVar('SDIMG_BOOTFS_SIZE') or 64), calc_mb)

    # 4. Call mke2fs
    try:
        cmd = ['mke2fs', '-F', '-L', 'boot', '-t', 'ext4', '-d', bootfs_dir, bootfs_img, f'{final_mb}M']
        subprocess.run(cmd, check=True, capture_output=True, text=True)
        bb.note(f"bootfs.ext4 ({final_mb}MB) created successfully")
    except subprocess.CalledProcessError as e:
        bb.fatal(f"mke2fs failed: {e.stderr}")
}

# Link function to the wic generation task
do_image_wic[postfuncs] += "write_bootinfo_to_wic"

python write_bootinfo_to_wic() {
    import subprocess
    import os

    # 1. Retrieve Yocto path variables
    imgdeploydir = d.getVar('IMGDEPLOYDIR')
    image_name = d.getVar('IMAGE_NAME')
    deploy_dir = d.getVar('DEPLOY_DIR_IMAGE')
    bootinfo_path = os.path.join(deploy_dir, 'bootinfo_sd.bin')

    # Define the physical path of the generated .wic file
    wic_path = os.path.join(imgdeploydir, image_name + '.wic')

    # 2. Safety checks
    if not os.path.exists(bootinfo_path):
        bb.fatal(f"[Post-WIC] bootinfo file not found: {bootinfo_path}")

    if not os.path.exists(wic_path):
        bb.error(f"[Post-WIC] Target WIC file not found: {wic_path}")
        return

    # 3. Perform the dd injection
    try:
        # Overwrite first 80 bytes without truncating the file
        cmd = ['dd', f'if={bootinfo_path}', f'of={wic_path}', 'bs=1', 'count=80', 'conv=notrunc']
        subprocess.run(cmd, check=True, capture_output=True)
        bb.note(f"[Post-WIC] Successfully injected bootinfo into {os.path.basename(wic_path)}")
    except subprocess.CalledProcessError as e:
        bb.error(f"[Post-WIC] dd command failed: {e.stderr.decode()}")
}

