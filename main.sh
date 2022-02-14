#### EXPERIMENT CONFIGURATION ####
BIN_PATH=$(readlink -f "$0")
dir="$(dirname $BIN_PATH)"  

exp_folder="$dir/Files"
results_folder="results/"
testcases_folder="testcases/"
testers_folder="testers/"

path_catalog="/home/raphael/DOCFILES/DoctoralFiles/WeaknessesTestingTool/wtt_juliet_catalog/"

#### EXPERIMENT CONFIGURATION ####
tester_file_ft_bad="tester_wtt_CWE134_bad_ft.c"
tester_file_ft_good="tester_wtt_CWE134_good_ft.c"
tester_file_rtc_bad="tester_wtt_CWE134_bad_rtc.c"
tester_file_rtc_good="tester_wtt_CWE134_good_rtc.c"

# mocked_function1="fgets"
# mocked_function2="buffer_output"
include_folder="/home/raphael/DOCFILES/DoctoralFiles/Juliet/C1.2/testcasesupport"

#testcases="/home/raphael/DOCFILES/DoctoralFiles/Juliet/C1.2/testcases/CWE121_Stack_Based_Buffer_Overflow/wtt/"
#testcases="/home/raphael/DOCFILES/DoctoralFiles/Juliet/C1.2/testcases/CWE122_Heap_Based_Buffer_Overflow/wtt/"
testcases="/home/raphael/DOCFILES/DoctoralFiles/Juliet/C1.2/testcases/CWE134_Uncontrolled_Format_String/wtt/"
#testcases="/home/raphael/DOCFILES/DoctoralFiles/Juliet/C1.2/testcases/CWE369_Divide_by_Zero/wtt/"
# testcases="/home/raphael/DOCFILES/DoctoralFiles/Juliet/C1.2/testcases/CWE190_Integer_Overflow/wtt/"
#testcases="/home/raphael/DOCFILES/DoctoralFiles/Juliet/C1.2/testcases/CWE191_Integer_Underflow/wtt/"
#testcases="/home/raphael/DOCFILES/DoctoralFiles/Juliet/C1.2/testcases/CWE476_NULL_Pointer_Dereference/wtt/"

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
    
    ### SCRIPTS CALL'S ####
    # Execute Technique Scripts ##
    echo "Proc. 1/8 - Análise estática"
    ./staticAnalysis.sh $testcases $filename

    echo "Proc. 2/8 - Construindo casos de teste"
    ./createTestCases.sh $testcases $filename $include_folder

    echo "Proc. 3/8 - Executando casos de teste"
    ./runTestCases.sh $exp_folder $tester_file_rtc_bad $tester_file_rtc_good $include_folder $testers_folder $mocked_function1

    echo "Proc. 4/8 - Executando fuzzing teste"
    ./fuzzingTest.sh $exp_folder $tester_file_ft_bad $tester_file_ft_good $include_folder $testers_folder $mocked_function1

    echo "Proc. 5/8 - Executando casos de teste com inputs do AFL"
    ./rtcInputAFL.sh

    echo "Proc. 6/8 - Criando categorização"
    ./categorization.sh $filename $testcases

    echo "Proc. 7/8 - Movendo arquivos"
    ./catalog.sh $path_catalog $filename

    echo "Proc. 8/8 - Limpando Projeto"
    ./clear.sh

done

echo "Final Result ..."
./finalCategorization.sh

echo "Processo Finalizado"