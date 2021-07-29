path_catalog="$1"
file_name="$2"

#### EXPERIMENT CONFIGURATION ####
BIN_PATH=$(readlink -f "$0")
dir="$(dirname $BIN_PATH)"  

exp_folder="$dir/Files"

cd $dir

cp -r $exp_folder "$path_catalog/temp"

cd "$path_catalog/temp"

mv "Files" "Files_"$file_name 