# Skip the default mesa recipe on k1 machine - we use mesa3d instead
# This prevents the default mesa from being parsed or built for k1

COMPATIBLE_MACHINE:k1 = "(!.*)"

