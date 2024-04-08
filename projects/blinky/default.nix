{
  stdenvNoCC,
  lib,
  yosys,
  nextpnr,
  python3Packages,
}:
stdenvNoCC.mkDerivation {
  name = "tangnano9k-blinky";
  version = "0";

  src = lib.cleanSource ./.;

  nativeBuildInputs = [
    yosys
    nextpnr
    python3Packages.apycula
  ];

  installPhase = ''
    install -Dm 444 -T blinky.fs $out/blinky.fs
  '';
}
