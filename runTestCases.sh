res1=$(date +%s.%N)

## VARIABLES ##
folder="$1"
tester_bad="$2"
tester_good="$3"
include="$4"
testers="$5"

## Mocked functions
#mock1="$4"
#mock2="$5"

if [ -d "$folder/output" ]; then
echo "Diretorio output já existe" ;
else
`mkdir -p $folder/output`;
echo "Diretorio output criado"
fi

cd "$folder"

#touch time.txt
echo "$tester_bad $tester_good $include"

gcc $testers$tester_bad -I$include -lcmocka -o bad_a.out 2> output/rtc_bad_err.txt
gcc $testers$tester_good -I$include -lcmocka -o good_a.out 2> output/rtc_good_err.txt

./good_a.out  2> output/rtc_good.txt
./bad_a.out  2> output/rtc_bad.txt

res2=$(date +%s.%N)
dt=$(echo "$res2 - $res1" | bc)
dd=$(echo "$dt/86400" | bc)
dt2=$(echo "$dt-86400*$dd" | bc)
dh=$(echo "$dt2/3600" | bc)
dt3=$(echo "$dt2-3600*$dh" | bc)
dm=$(echo "$dt3/60" | bc)
ds=$(echo "$dt3-60*$dm" | bc)

LC_NUMERIC=C printf "Runtime RTC: %d:%02d:%02d:%02.4f\n" $dd $dh $dm $ds >> "./time.txt"