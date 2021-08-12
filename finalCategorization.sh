#VAR
# path arquivos
BIN_PATH=$(readlink -f "$0")
dir="$(dirname $BIN_PATH)"

path_catalog="/home/raphael/DOCFILES/DoctoralFiles/WeaknessesTestingTool/wtt_juliet_catalog/temp"
pathResults="$dir/results"
fileTime="totalTime.csv"
fileTotalCat="totalCategorization.csv"

cd $pathResults

#1 Criar os arquivos se não existirem. - DONE
# Build TotalTime File
if [ -f "$pathResults/$fileTime" ]; then
    echo "$fileTime existe"
else
    echo "$fileTime não existe"
    touch $fileTime
    echo "filename,instrumentation(s),rtc_bad(s),rtc_good(s),ft_bad(s),ft_good(s),afl_input_bad(ms),afl_input_good(ms),cat_bad(ms),cat_good(ms)" >> $fileTime
    echo "$fileTime criado"
fi

# Build TotalCat File
if [ -f "$pathResults/$fileTotalCat" ]; then
    echo "$fileTotalCat existe"
else
    echo "$fileTotalCat não existe"
    touch $fileTotalCat
    echo "cwe,file,kind,sloc,saAll,insideAll,saFilter,insideFilter,rtc,ftCrashes,aflrtc_pass,aflrtc_fail,aflrtc_crash" >> $fileTotalCat
    echo "$fileTotalCat criado"
fi


cd ../src
javac Results.java
java Results "$cwe"
cd ../