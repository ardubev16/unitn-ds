{ pkgs, ... }:
{
  cachix.enable = false;

  languages.java = {
    enable = true;
    jdk.package = pkgs.jdk8;
    gradle.enable = true;
  };
}
