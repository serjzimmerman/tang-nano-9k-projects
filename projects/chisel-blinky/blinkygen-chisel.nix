# Take a look at https://github.com/gvolpe/sbt-nix.g8/blob/7e853b4e464cb80c45283bccfd899a4361bc7053/wrapper.nix#L19
# for further reference.
{
  mkSbtDerivation,
  lib,
  yosys,
  nextpnr,
  python3Packages,
  jdk21_headless,
  circt,
  makeWrapper,
}:
let
  java = jdk21_headless;
in
mkSbtDerivation rec {
  pname = "blinkygen-chisel";
  version = "0";

  src = lib.cleanSource ./.;

  nativeBuildInputs = [
    yosys
    nextpnr
    python3Packages.apycula
    makeWrapper
  ];

  buildInputs = [ circt ];

  buildPhase = ''
    runHook preBuild
    sbt assembly
    runHook postBuild
  '';

  depsSha256 = "sha256-jeLxdCXKVIrWjLtXGyu80halnMckL92AR9BumR2Lsew=";

  installPhase = ''
    mkdir -p $out/{bin,lib}
    install -Dm 755 -T target/scala-*/*-assembly-*.jar $out/lib/${pname}
    makeWrapper ${java}/bin/java $out/bin/${pname} \
      --add-flags "-jar '$out/lib/${pname}'" \
      --set CHISEL_FIRTOOL_PATH "${circt}/bin"
  '';
}
