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

path_catalog="/home/raphael/DOCFILES/DoctoralFiles/WeaknessesTestingTool/wtt_juliet_catalog/"

# tested_file="format_string.c"
# mocked_function1="apr_vformatter"
# mocked_function2="buffer_output"
include_folder="/home/raphael/DOCFILES/DoctoralFiles/Juliet/C/testcasesupport"

# testcases="/home/raphael/DOCFILES/DoctoralFiles/Juliet/C/testcases/CWE134_Uncontrolled_Format_String/s02/"
# filename="CWE134_Uncontrolled_Format_String__char_file_fprintf_02.c"

testcases="/home/raphael/DOCFILES/DoctoralFiles/Juliet/C/testcases/CWE134_Uncontrolled_Format_String/wtt/"

#ls $testcases > fileList.txt
echo -e '\nLendo diretório [@] -----------------------------------'
i=0
while read line
do
    array[ $i ]="$line"
    (( i++ ))
done < <(ls $testcases )

# Using [@]
echo -e '\nArray -----------------------------------'
for filename in "${array[@]}"
do
    echo -e '\nExecutando -----------------------------------'
    echo "$filename"
    
    #### SCRIPTS CALL'S ####
    ## Execute Technique Scripts ##
    echo "Proc. 1/7 - Construindo casos de teste"
    ./createTestCases.sh $testcases $filename

    echo "Proc. 2/7 - Executando casos de teste"
    ./runTestCases.sh $exp_folder $tester_file_rtc_bad $tester_file_rtc_good $include_folder $testers_folder

    echo "Proc. 3/7 - Executando fuzzing teste"
    ./fuzzingTest.sh $exp_folder $tester_file_ft_bad $tester_file_ft_good $include_folder $testers_folder

    echo "Proc. 4/7 - Executando casos de teste com inputs do AFL"
    ./rtcInputAFL.sh

    echo "Proc. 5/7 - Criando categorização"
    ./categorization.sh $filename

    echo "Proc. 6/7 - Movendo arquivos"
    ./catalog.sh $path_catalog $filename

    echo "Proc. 7/7 - Limpando Projeto"
    ./clear.sh

done

echo "Final Result ..."

echo "Processo Finalizado"