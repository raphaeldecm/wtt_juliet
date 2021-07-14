#### EXPERIMENT CONFIGURATION ####
BIN_PATH=$(readlink -f "$0")
dir="$(dirname $BIN_PATH)"  

exp_folder="$dir/Files"
results_folder="results/"
testcases_folder="testcases/"
testers_folder="testers/"

# echo $exp_folder 
# echo $results_folder 
# echo $testcases_folder
# echo $dir

#### EXPERIMENT CONFIGURATION ####
tester_file_ft_bad="tester_wtt_CWE134_bad_ft.c"
tester_file_ft_good="tester_wtt_CWE134_good_ft.c"

tester_file_rtc_bad="tester_wtt_CWE134_bad_rtc.c"
tester_file_rtc_good="tester_wtt_CWE134_good_rtc.c"

# tested_file="format_string.c"
# mocked_function1="apr_vformatter"
# mocked_function2="buffer_output"
include_folder="/home/raphael/DOCFILES/DoctoralFiles/Juliet/C/testcasesupport"

testcases="/home/raphael/DOCFILES/DoctoralFiles/Juliet/C/testcases/CWE134_Uncontrolled_Format_String/s02/"
filename="CWE134_Uncontrolled_Format_String__char_file_fprintf_02.c"

#### SCRIPTS CALL'S ####
echo "Construindo casos de teste"
./createTestCases.sh $testcases $filename

echo "Executando casos de teste"
./runTestCases.sh $exp_folder $tester_file_rtc_bad $tester_file_rtc_good $include_folder $testers_folder

echo "Executando fuzzing teste"
./fuzzingTest.sh $exp_folder $tester_file_ft_bad $tester_file_ft_good $include_folder $testers_folder

echo "Executando casos de teste com inputs do AFL"
#./rtcInputAFL.sh

echo "Criando categorização"
#./categorization.sh $cwe

echo "Processo Finalizado"