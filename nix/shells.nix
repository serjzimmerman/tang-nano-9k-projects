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
      ]
      ++ (with pkgs.python3Packages; [ apycula ]);
  };
}
