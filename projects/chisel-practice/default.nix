{
  mkSbtDerivation,
  lib,
  yosys,
  nextpnr,
  python3Packages,
  circt,
  makeWrapper,
  jdk21_headless,
  verible,
}:
let
  java = jdk21_headless;
in
mkSbtDerivation {
  pname = "chisel-practice";
  version = "0";

  src = lib.cleanSource ./.;

  nativeBuildInputs = [
    yosys
    nextpnr
    python3Packages.apycula
    makeWrapper
    java
    verible
  ];

  buildInputs = [ circt ];
  CHISEL_FIRTOOL_PATH = "${circt}/bin";

  buildPhase = ''
    runHook preBuild
    make
    runHook postBuild
  '';

  depsSha256 = "sha256-zenDa/z5edMIRRwwsbHh99IezYQSQFtYRwtqlDfAYwA=";

  doCheck = true;
  checkPhase = ''
    sbt test
  '';

  installPhase = ''
    mkdir -p $out/share
    install -Dm 444 *.fs -t $out/share
  '';
}
