program fatorial;

var
  n, fat: integer;

begin
  n := 5;
  fat := 1;

  while n > 0 do
  begin
    fat := fat * n;
    n := n - 1;
  end;

  writeln(fat);
end.