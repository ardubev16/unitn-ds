{ ... }:
{
  cachix.enable = false;

  languages.java = {
    enable = true;
    gradle.enable = true;
  };
}
