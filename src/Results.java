import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Results {

    private String pathExp;
    private String pathCatalog;
    private String folderFiles;
    private String time;
    private String totalTime;
    private String totalCategorization;
    private String catBad;
    private String catGood;
    private List<String> tempTime;
    private List<String> tempCat;

    public static void main(String[] args) throws Exception {
        Results res = new Results();
        res.start();
    }

    public Results() {
        this.pathExp = System.getProperty("user.dir");
        this.pathExp = this.pathExp.replaceAll("/src", "");
        this.pathCatalog = this.pathExp.concat("_catalog");
        this.folderFiles = "/Files";
        this.time = "/time.csv";
        this.catBad = "categorization_bad.log";
        this.catGood = "categorization_good.log";
        this.totalTime = "/totalTime.csv";
        this.totalCategorization = "/totalCategorization.csv";
        tempTime = new ArrayList();
        tempCat = new ArrayList();
    }

    private void start() throws Exception {

        List<String> resultFiles = new ArrayList();
        resultFiles = readFiles(pathCatalog);

        for (String file : resultFiles) {
            loadResultsCat(pathCatalog, file);
            loadResultsTime(pathCatalog, file);
        }

        loadTotalResults();

    }

    // Get the file list in the path catalog/temp
    public List readFiles(String pathCatalog) throws FileNotFoundException, IOException {

        List<String> files = new ArrayList<>();

        File file = new File(pathCatalog + "/temp");

        File vFiles[] = file.listFiles();

        for (int i = 0; i < vFiles.length; i++) {
            files.add(vFiles[i].getName());
            System.out.println(vFiles[i].getName());
        }
        return files;
    }

    // Get the content of files cat in catalog/temp
    public void loadResultsCat(String pathCatalog, String resultFile) throws FileNotFoundException, IOException {

        File fileCatBad = new File(pathCatalog + "/temp/" + resultFile + "/" + this.catBad);
        File fileCatGood = new File(pathCatalog + "/temp/" + resultFile + "/" + this.catGood);

        if (fileCatBad.isFile() && fileCatGood.isFile()) {
            BufferedReader readerCB = new BufferedReader(new FileReader(fileCatBad));
            BufferedReader readerCG = new BufferedReader(new FileReader(fileCatGood));

            String str = "";
            int i = 0;
            while (true) {
                ++i;
                if (str != null) {
                    if (str.startsWith("CWE")) {
                        if (!tempCat.contains(str)) {
                            tempCat.add(str);
                        }
                    }
                } else {
                    break;
                }
                str = readerCB.readLine();
            }

            str = "";
            i = 0;
            while (true) {
                ++i;
                if (str != null) {
                    if (str.startsWith("CWE")) {
                        if (!tempCat.contains(str)) {
                            tempCat.add(str);
                        }
                    }
                } else {
                    break;
                }
                str = readerCG.readLine();
            }
            readerCB.close();
            readerCG.close();
        } else {
            tempCat.add(resultFile + "," + "ERROR!");
        }
    }

    // Get the content of files time in catalog/temp
    public void loadResultsTime(String pathCatalog, String resultFile) throws FileNotFoundException, IOException {

        File fileCatBad = new File(pathCatalog + "/temp/" + resultFile + "/" + this.catBad);
        File fileCatGood = new File(pathCatalog + "/temp/" + resultFile + "/" + this.catGood);

        File fileTime = new File(pathCatalog + "/temp/" + resultFile + this.time);

        if (fileCatBad.isFile() && fileCatGood.isFile()) {
            BufferedReader reader = new BufferedReader(new FileReader(fileTime));

            String str = "";
            int i = 0;
            while (true) {
                ++i;
                if (str != null) {
                    if (str.startsWith("CWE")) {
                        if (!tempTime.contains(str)) {
                            tempTime.add(str);
                        }
                    }
                } else {
                    break;
                }
                str = reader.readLine();
            }
            reader.close();
        } else {
            tempTime.add(resultFile + "," + "ERROR!");
        }
    }

    // Get the atual total cat and time; And update;
    public void loadTotalResults() throws FileNotFoundException, IOException{

        File fileTime = new File(pathExp + "/results" + totalTime);
        File fileCat = new File(pathExp + "/results" + totalCategorization);
        
        List<String> resTotalTime = new ArrayList<>();
        List<String> resTotalCat = new ArrayList<>();

        if (fileTime.isFile() && fileCat.isFile()) {

            BufferedReader readerTime = new BufferedReader(new FileReader(fileTime));
            BufferedReader readerCat = new BufferedReader(new FileReader(fileCat));


            String str = "";
            int i = 0;
            while (true) {
                ++i;
                if (str != null) {
                    if (!str.startsWith("filename,")) {
                        resTotalTime.add(str);
                    }
                } else {
                    break;
                }
                str = readerTime.readLine();
            }
            readerTime.close();
            
            str = "";
            i = 0;
            while (true) {
                ++i;
                if (str != null) {
                    if (!str.startsWith("cwe,file")) {
                        resTotalCat.add(str);
                    }
                } else {
                    break;
                }
                str = readerCat.readLine();
            }
            readerCat.close();

        } else {
            System.out.println("ERRO LOG: totaltime e/ou totalCategorization não existem!");
        }

        for (String string : tempTime) {
            if(!resTotalTime.contains(string)){
                resTotalTime.add(string);
            }
        }

        for (String string : tempCat) {
            if(!resTotalCat.contains(string)){
                resTotalCat.add(string);
            }
        }

        try {
            
            PrintWriter pw = new PrintWriter(new FileOutputStream(fileTime, false));
            PrintWriter pwCat = new PrintWriter(new FileOutputStream(fileCat, false));

            pw.write("filename,instrumentation(s),rtc_bad(s),rtc_good(s),ft_bad(s),ft_good(s),afl_input_bad(ms),afl_input_good(ms),cat_bad(ms),cat_good(ms)");
            for (String string : resTotalTime) {
                pw.write(string+"\n");
            }
            pw.close();

            pwCat.write("cwe,file,kind,sloc,saAll,saFilter,rtc,ftCrashes,aflrtc_pass,aflrtc_fail,aflrtc_crash");
            for (String string : resTotalCat) {
                pwCat.write(string+"\n");
            }
            pwCat.close();
        } catch (IOException e) {
            System.out.println("error: e.printStackTrace()");
        }

    }

}
