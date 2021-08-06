## VARIABLES ##
folder="$1"
tester_bad="$2"
tester_good="$3"
include="$4"
testers="$5"

## Mocked functions
#mock1="$6"
#mock2="$8"

echo "$folder $results $testcases $tested $tester_bad $tester_good $include"

cd "$folder"

if [ -d "$folder/results_bad" ]; then
echo "Diretorio output já existe" ;
else
`mkdir -p $folder/results_bad`;
echo "Diretorio results_bad criado"
fi

if [ -d "$folder/results_good" ]; then
echo "Diretorio output já existe" ;
else
`mkdir -p $folder/results_good`;
echo "Diretorio results_good criado"
fi

#Debug
#afl-gcc -fno-stack-protector -z execstack $tester -I. -lcmocka -o fuzz
echo "afl-gcc $tester_bad -I$include -lcmocka -o fuzz_bad"
echo "afl-gcc $tester_good -I$include -lcmocka -o fuzz_good"

# System core dumps must be disabled as with AFL.
echo core|sudo tee /proc/sys/kernel/core_pattern
echo performance|sudo tee /sys/devices/system/cpu/cpu*/cpufreq/scaling_governor

#Get time
res1=$(date +%s.%N)

{
    echo "Fuzz Bad: Start"
    #afl-gcc $testers$tester_bad -I$include -Wl,--wrap=$mock1 -lcmocka -o fuzz_bad
    afl-fuzz -i ./testcases/ -o ./results_bad/ ./fuzz_bad
    echo "Fuzz Bad: done"
}&
echo "Process Listen: start"
sleep 20
PID=`ps -eaf | grep 'fuzz_' | grep -v grep | awk '{print $2}'`
if [[ "" !=  "$PID" ]]; then
  echo "killing $PID"
  kill -9 $PID
fi
echo "Process Listen: Done"

res2=$(date +%s.%N)
dt=$(echo "$res2 - $res1" | bc)
dd=$(echo "$dt/86400" | bc)
dt2=$(echo "$dt-86400*$dd" | bc)
dh=$(echo "$dt2/3600" | bc)
dt3=$(echo "$dt2-3600*$dh" | bc)
dm=$(echo "$dt3/60" | bc)
ds=$(echo "$dt3-60*$dm" | bc)

LC_NUMERIC=C printf "%02.4f," $ds >> "./time.csv"

#Get Time
res3=$(date +%s.%N)

{
    echo "Fuzz Bad: Start"
    #afl-gcc $testers$tester_good -I$include -Wl,--wrap=$mock1 -lcmocka -o fuzz_good
    afl-fuzz -i ./testcases/ -o ./results_good/ ./fuzz_good
    echo "Fuzz Bad: done"
}&
echo "Process Listen: start"
sleep 20
PID=`ps -eaf | grep 'fuzz_' | grep -v grep | awk '{print $2}'`
if [[ "" !=  "$PID" ]]; then
  echo "killing $PID"
  kill -9 $PID
fi
echo "Process Listen: Done"


res4=$(date +%s.%N)
dt=$(echo "$res4 - $res3" | bc)
dd=$(echo "$dt/86400" | bc)
dt2=$(echo "$dt-86400*$dd" | bc)
dh=$(echo "$dt2/3600" | bc)
dt3=$(echo "$dt2-3600*$dh" | bc)
dm=$(echo "$dt3/60" | bc)
ds=$(echo "$dt3-60*$dm" | bc)

LC_NUMERIC=C printf "%02.4f," $ds >> "./time.csv"