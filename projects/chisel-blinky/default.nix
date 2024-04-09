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
mkSbtDerivation rec {
  pname = "chisel-blinky";
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

  depsSha256 = "sha256-jeLxdCXKVIrWjLtXGyu80halnMckL92AR9BumR2Lsew=";

  installPhase = ''
    mkdir -p $out/{bin,lib,share}
    install -Dm 755 -T target/scala-*/*-assembly-*.jar $out/lib/${pname}
    makeWrapper ${java}/bin/java $out/bin/${pname} \
      --add-flags "-jar '$out/lib/${pname}'" \
      --set CHISEL_FIRTOOL_PATH "${circt}/bin"
    install -Dm 444 -T blinky.fs $out/share/blinky.fs
  '';
}
