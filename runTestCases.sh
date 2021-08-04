## VARIABLES ##
folder="$1"
tester_bad="$2"
tester_good="$3"
include="$4"
testers="$5"

## Mocked functions
mock1="$6"
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

#Get time
res1=$(date +%s.%N)
echo "$testers$tester_bad -I$include -Wl,--wrap=$mock1 -lcmocka -o bad_a.out"
gcc $testers$tester_bad -I$include -Wl,--wrap=$mock1 -lcmocka -o bad_a.out 2> output/rtc_bad_err.txt
./bad_a.out  2> output/rtc_bad.txt

res2=$(date +%s.%N)
dt=$(echo "$res2 - $res1" | bc)
dd=$(echo "$dt/86400" | bc)
dt2=$(echo "$dt-86400*$dd" | bc)
dh=$(echo "$dt2/3600" | bc)
dt3=$(echo "$dt2-3600*$dh" | bc)
dm=$(echo "$dt3/60" | bc)
ds=$(echo "$dt3-60*$dm" | bc)

LC_NUMERIC=C printf "%02.4f," $ds >> "./time.csv"

#Get time
res3=$(date +%s.%N)

echo "$testers$tester_good -I$include -Wl,--wrap=$mock1 -lcmocka -o good_a.out"
gcc $testers$tester_good -I$include -Wl,--wrap=$mock1 -lcmocka -o good_a.out 2> output/rtc_good_err.txt
./good_a.out  2> output/rtc_good.txt


res4=$(date +%s.%N)
dt=$(echo "$res4 - $res3" | bc)
dd=$(echo "$dt/86400" | bc)
dt2=$(echo "$dt-86400*$dd" | bc)
dh=$(echo "$dt2/3600" | bc)
dt3=$(echo "$dt2-3600*$dh" | bc)
dm=$(echo "$dt3/60" | bc)
ds=$(echo "$dt3-60*$dm" | bc)

LC_NUMERIC=C printf "%02.4f," $ds >> "./time.csv"