testcases="$1"
filename="$2"

BIN_PATH=$(readlink -f "$0")
dir="$(dirname $BIN_PATH)"  

exp_folder="$dir/Files"

cd "$exp_folder"

flawfinder "$testcases$filename" > flawfinder_all.txt

flawfinder -F -m 3 "$testcases$filename" > flawfinder_filter.txt
