{ pkgs, ... }:
{
  packages = {
    blinky = pkgs.callPackage ./blinky { };
    blinkygen-chisel = pkgs.callPackage ./chisel-blinky { };
    chisel-blinky = pkgs.callPackage ./chisel-blinky { };
  };
}
