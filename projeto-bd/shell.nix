with (import <nixpkgs> {});
let
  my-python = pkgs.python3;
  python-with-my-packages = my-python.withPackages (p: with p; [
    flask
    psycopg2
  ]);
in
mkShell {
  buildInputs = [
    postgresql
    python3
  ];
  shellHook = ''
      PYTHONPATH=${python-with-my-packages}/${python-with-my-packages.sitePackages}
      # maybe set more env-vars
    '';
}
