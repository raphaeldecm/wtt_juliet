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

# Remove results_bad
if [ -d "$exp_folder/results_bad" ]; then
    cd $exp_folder;
    rm -R results_bad
else
    echo "results_bad não existe"
fi

# Remove results_good
if [ -d "$exp_folder/results_good" ]; then
    cd $exp_folder;
    rm -R results_good
else
    echo "results_good não existe"
fi

# Remove Testers
cd "$exp_folder/testers"
rm *.c

# Remove Execs
cd ..
rm bad_a.out good_a.out fuzz_bad fuzz_good 

# Remove Logs
rm log_afl_bad.txt log_afl_good.txt log_good.txt log_bad.txt time.txt categorization_bad.log categorization_good.log

rm result_good.json result_bad.json