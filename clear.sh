#### EXPERIMENT CONFIGURATION ####
BIN_PATH=$(readlink -f "$0")
dir="$(dirname $BIN_PATH)"  

exp_folder="$dir/Files"

# Remove Output
if [ -d "$exp_folder/output" ]; then
    cd $exp_folder;
    rm -R output
else
    echo "output não existe"
fi

# Remove Testers
cd "$exp_folder/testers"
rm *.c

# Remove Execs
cd ..
rm bad_a.out good_a.out fuzz_bad fuzz_good 

# Remove Logs
rm log_afl_bad.txt log_afl_good.txt time.txt