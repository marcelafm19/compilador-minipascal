program pratica;
var 
    x : integer;
begin
  x := 0;
  repeat
    x := x + 1;
    writeln(x);
  until x = 5;
end.