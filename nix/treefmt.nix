{ pkgs, ... }:
{
  treefmt = {
    projectRootFile = "flake.nix";
    programs = {
      nixfmt.enable = true;
      yamlfmt.enable = true;
      deadnix.enable = true;
      just.enable = true;
      mdformat.enable = true;
      black.enable = true;
      ruff.enable = true;
    };

    settings.formatter = {
      verible-verilog-format = {
        command = "${pkgs.verible}/bin/verible-verilog-format";
        options = [ "--inplace" ];
        includes = [
          "*.v"
          "*.sv"
        ];
      };
    };
  };
}
