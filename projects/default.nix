{ pkgs, ... }:
{
  packages = {
    blinky = pkgs.callPackage ./blinky { };
  };
}
