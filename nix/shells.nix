{ pkgs, ... }:
{
  devShells.default = pkgs.mkShell {
    nativeBuildInputs =
      with pkgs;
      [
        yosys
        nextpnr
        openfpgaloader
        usbutils
        just
        metals
        scalafmt
        sbt
        scala_2_12
      ]
      ++ (with pkgs.python3Packages; [ apycula ]);
  };
}
