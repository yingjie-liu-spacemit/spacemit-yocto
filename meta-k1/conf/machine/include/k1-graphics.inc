# Prefer our local mesa3d build over the default mesa
PREFERRED_PROVIDER_virtual/libgl = "mesa3d"
PREFERRED_PROVIDER_virtual/libgles1 = "mesa3d"
PREFERRED_PROVIDER_virtual/libgles2 = "mesa3d"
PREFERRED_PROVIDER_virtual/libgles3 = "mesa3d"
PREFERRED_PROVIDER_virtual/egl = "mesa3d"
PREFERRED_PROVIDER_virtual/mesa = "mesa3d"
PREFERRED_PROVIDER_virtual/libgbm = "mesa3d"
