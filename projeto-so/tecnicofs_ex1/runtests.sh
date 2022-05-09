let total=0;
let correct=0;

for x in $(find tests/ -mindepth 1 -executable); do
    echo $x 
    $x
done
