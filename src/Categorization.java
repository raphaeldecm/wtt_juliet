import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Categorization {

    private static String cwe;
    private static String fileName;
    private String pathExp;
    private String folderFiles;
    private String time;
    private String failedLog_bad;
    private String failedLog_good;
    private String categorization_bad;
    private String categorization_good;
    private int numberPass;
    private int numberFail;
    private int numberCrash;

    public static void main(String[] args) throws Exception {
        cwe = args[0].split("_")[0]; //CWE134_Uncontrolled_Format_String__char_file_fprintf_02.c
        fileName = args[0];
        // System.out.println(cwe);
        Categorization cat = new Categorization();
        cat.start();
    }

    public Categorization() {
        this.pathExp = System.getProperty("user.dir");
        this.pathExp = this.pathExp.replaceAll("/src", "");
        this.folderFiles = "/Files";
        this.time = "/time.csv";
        this.failedLog_bad = "/result_bad.json";
        this.failedLog_good = "/result_good.json";
        this.categorization_bad = "/categorization_bad.log";
        this.categorization_good = "/categorization_good.log";
        this.numberPass = 0;
        this.numberFail = 0;
        this.numberCrash = 0;
    }

    private void start() throws Exception {

        File file = new File(this.pathExp + this.folderFiles + this.time);
        PrintWriter pw = new PrintWriter(new FileOutputStream(file, true));

        long startTime = System.currentTimeMillis();
        execBad();
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        pw.write(totalTime+",");

        startTime = System.currentTimeMillis();
        execGood();
        endTime = System.currentTimeMillis();
        totalTime = endTime - startTime;
        pw.write(totalTime+"");

        pw.close();
        
    }
    
    
    public void execGood() throws Exception{
        List<String> result = new ArrayList();
        result = readResult(pathExp, folderFiles, failedLog_good);
        recCategorization(result, categorization_good, "good");
    }
    
    public void execBad() throws Exception{
        List<String> result = new ArrayList();
        result = readResult(pathExp, folderFiles, failedLog_bad);
        recCategorization(result, categorization_bad, "bad");
    }

    public void recCategorization(List<String> result, String categorization, String kind){
        
        for (String line : result) {
            if(line.contains("FAILED")){
                numberFail++;
            }
            if(line.contains("PASSED")){
                numberPass++;
            }
            if(line.contains("CRASH")){
                numberCrash++;
            }
        }

        try {
            File fw = new File(pathExp + folderFiles + categorization);
            PrintWriter pw = new PrintWriter(new FileOutputStream(fw, true));
            
            pw.write("cwe,file,kind,pass,fail,crash\n");
            pw.write(cwe + "," + fileName + "," + kind + "," + numberPass + "," + numberFail + "," + numberCrash + "\n");

            pw.close();
        } catch (IOException e) {
            System.out.println("error: e.printStackTrace()");
        }
        System.out.println("File: " + fileName);
        System.out.println("Crash: " + kind);
        System.out.println("Crash: " + numberCrash);
        System.out.println("Passed: " + numberPass);
        System.out.println("Fail: " + numberFail);

        // TEAR DOWN
        numberCrash = 0;
        numberFail = 0;
        numberPass = 0;

    }

    public List readResult(String pathExp, String folderFiles, String failedLog)
            throws FileNotFoundException, IOException {

        List<String> lines = new ArrayList<>();
        String str = "";

        File file = new File(pathExp + folderFiles + failedLog);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        int i = 0;

        while (true) {
            ++i;
            if (str != null) {
                if (!lines.contains(str)) {
                    lines.add(str);
                }
            } else {
                break;
            }
            str = reader.readLine();
        }

        reader.close();
        return lines;
    }
}
