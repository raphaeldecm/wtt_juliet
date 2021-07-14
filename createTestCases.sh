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

LC_NUMERIC=C printf "Runtime Build Test Cases : %d:%02d:%02d:%02.4f\n" $dd $dh $dm $ds >> "$exp_folder/time.txt"