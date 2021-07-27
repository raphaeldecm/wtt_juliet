testcases="$1"
filename="$2"

res1=$(date +%s.%N)

BIN_PATH=$(readlink -f "$0")
dir="$(dirname $BIN_PATH)"  

exp_folder="$dir/Files"

cd src
javac -cp ../lib/freemarker.jar CreateTestCases.java
java -cp .:../lib/freemarker.jar CreateTestCases "$testcases" "$filename"
cd ../

res2=$(date +%s.%N)
dt=$(echo "$res2 - $res1" | bc)
dd=$(echo "$dt/86400" | bc)
dt2=$(echo "$dt-86400*$dd" | bc)
dh=$(echo "$dt2/3600" | bc)
dt3=$(echo "$dt2-3600*$dh" | bc)
dm=$(echo "$dt3/60" | bc)
ds=$(echo "$dt3-60*$dm" | bc)

LC_NUMERIC=C printf "filename,instrumentation(s),rtc_bad(s),rtc_good(s),ft_bad(s),ft_good(s),afl_input_bad(ms),afl_input_good(ms),cat_bad(ms),cat_good(ms)\n$filename,%02.4f," $ds >> "$exp_folder/time.csv"