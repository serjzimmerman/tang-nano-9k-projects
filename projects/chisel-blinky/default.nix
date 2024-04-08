{
  stdenvNoCC,
  lib,
  yosys,
  nextpnr,
  python3Packages,
  callPackage,
  blinkygen-chisel ? callPackage ./blinkygen-chisel.nix { },
}:
stdenvNoCC.mkDerivation {
  pname = "chisel-blinky";
  version = "0";

  src = lib.cleanSource ./.;

  nativeBuildInputs = [
    yosys
    nextpnr
    python3Packages.apycula
    blinkygen-chisel
  ];

  buildPhase = ''
    runHook preBuild
    blinkygen-chisel > src/blinky.v
    make
    runHook postBuild
  '';

  installPhase = ''
    install -Dm 444 -T blinky.fs $out/blinky.fs
  '';
}
