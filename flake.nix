{
  description = "Projects using Gowin FPGAs (Tang Nano 9K board)";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs/nixos-unstable";
    flake-parts.url = "github:hercules-ci/flake-parts";
    treefmt-nix = {
      url = "github:numtide/treefmt-nix";
      inputs.nixpkgs.follows = "nixpkgs";
    };
    sbt = {
      url = "github:zaninime/sbt-derivation";
      inputs.nixpkgs.follows = "nixpkgs";
    };
  };

  outputs =
    {
      nixpkgs,
      flake-parts,
      treefmt-nix,
      sbt,
      ...
    }@inputs:
    flake-parts.lib.mkFlake { inherit inputs; } {
      imports = [ treefmt-nix.flakeModule ];

      systems = [
        "x86_64-linux"
        "aarch64-linux"
      ];

      perSystem =
        { system, self', ... }:
        {
          imports = [
            ./nix/treefmt.nix
            ./nix/shells.nix
            ./projects
          ];

          checks = {
            inherit (self'.packages) blinky chisel-practice chisel-blinky;
          };

          _module.args.pkgs = import nixpkgs {
            inherit system;
            overlays = [ sbt.overlays.default ];
          };
        };
    };
}
