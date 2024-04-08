{ pkgs, ... }:
{
  packages = {
    blinky = pkgs.callPackage ./blinky { };
    blinkygen-chisel = pkgs.callPackage ./chisel-blinky/blinkygen-chisel.nix { };
    chisel-blinky = pkgs.callPackage ./chisel-blinky { };
  };
}
