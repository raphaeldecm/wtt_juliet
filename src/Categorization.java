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
    private String pathExp;
    private String folderFiles;
    private String aflLog;
    private String failedLog;
    private String categorization;
    private int numberPass;
    private int numberFail;
    private int numberCrash;

    public static void main(String[] args) throws Exception {
        cwe = args[0];
        Categorization cat = new Categorization();
        cat.start();
    }

    public Categorization() {
        this.pathExp = System.getProperty("user.dir");
        this.pathExp = this.pathExp.replaceAll("/src", "");
        this.folderFiles = "/Files";
        this.failedLog = "/result.json";
        this.categorization = "/categorization.log";
        this.numberPass = 0;
        this.numberFail = 0;
        this.numberCrash = 0;
    }

    private void start() throws Exception {
        List<String> result = new ArrayList();
        result = readResult(pathExp, folderFiles, failedLog);
        recCategorization(result);
    }
    
    public void recCategorization(List<String> result){
        
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
            
            pw.write("cwe,pass,fail,crash\n");
            pw.write(cwe + "," + numberPass + "," + numberFail + "," + numberCrash + "\n");

            pw.close();
        } catch (IOException e) {
            System.out.println("error: e.printStackTrace()");
        }
        
        System.out.println("Number of Crash: " + numberCrash);
        System.out.println("Number of Passed: " + numberPass);
        System.out.println("Number of Fail: " + numberFail);

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
