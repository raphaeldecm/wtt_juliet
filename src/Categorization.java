import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Categorization {

    private static String cwe;
    private static String fileName;
    private static String pathTestedFiles;
    private String pathExp;
    private String folderFiles;
    private String time;
    private String failedLog_bad;
    private String failedLog_good;
    private String rtcRes_bad;
    private String rtcRes_good;
    private String categorization_bad;
    private String categorization_good;
    private String resRTC;
    private String ftRes_bad;
    private String ftRes_good;
    private String sloc;
    private String saAll;
    private String saFilter;
    private String flawAllFile;
    private String flawFilterFile;
    private int numberCrashFT;
    private int numberPass;
    private int numberFail;
    private int numberCrash;
    private boolean findWeaknessAll;
    private boolean findWeaknessFilter;

    public static void main(String[] args) throws Exception {
        cwe = args[0].split("_")[0]; // CWE134_Uncontrolled_Format_String__char_file_fprintf_02.c
        fileName = args[0];
        pathTestedFiles = args[1];
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
        this.rtcRes_bad = "/output/rtc_bad.txt";
        this.rtcRes_good = "/output/rtc_good.txt";
        this.ftRes_bad = "/results_bad/crashes";
        this.ftRes_good = "/results_good/crashes";
        this.categorization_bad = "/categorization_bad.log";
        this.categorization_good = "/categorization_good.log";
        this.numberCrashFT = 0;
        this.numberPass = 0;
        this.numberFail = 0;
        this.numberCrash = 0;
        this.flawAllFile = "/flawfinder_all.txt";
        this.flawFilterFile = "/flawfinder_filter.txt";
        this.sloc = "";
        this.saAll = "";
        this.saFilter = "";
        this.findWeaknessAll = false;
        this.findWeaknessFilter = false;
    }

    private void start() throws Exception {

        staticAnalysis();

        File file = new File(this.pathExp + this.folderFiles + this.time);
        PrintWriter pw = new PrintWriter(new FileOutputStream(file, true));

        long startTime = System.currentTimeMillis();
        execBad();
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        pw.write(totalTime + ",");

        startTime = System.currentTimeMillis();
        execGood();
        endTime = System.currentTimeMillis();
        totalTime = endTime - startTime;
        pw.write(totalTime + "");

        pw.close();

    }

    public void getFunctionsLine(List<Integer> warningsLinesAll, List<Integer> warningsLinesFilter) throws IOException {

        List<String> fileReaded = new ArrayList<>();
        List<String> functions = new ArrayList<>();
        List<Integer> startLines = new ArrayList<>();
        List<Integer> endLines = new ArrayList<>();
        String str = "";

        File file = new File(pathExp + folderFiles + "/testedfunctions.log");
        BufferedReader reader;

        int i = 0;
        reader = new BufferedReader(new FileReader(file));
        while (true) {
            ++i;
            if (str != null) {
                if (!functions.contains(str) && str.length() > 0) {
                    functions.add(str);
                }
            } else {
                break;
            }
            str = reader.readLine();
        }

        File testes = new File(pathTestedFiles + fileName);
        BufferedReader br;
        str = "";

        br = new BufferedReader(new FileReader(testes));

        while ((str = br.readLine()) != null) {
            fileReaded.add(str);
        }

        for (String string : functions) {
            int ref = 0;
            int key = 0;

            for (int j = 1; j < fileReaded.size(); j++) {
                if (fileReaded.get(j).contains(string) && fileReaded.get(j).endsWith("()")) {
                    if (!startLines.contains(j + 1))
                        startLines.add(j + 1);
                    ref = j + 1;
                    break;
                }
            }

            for (int j = ref; j < fileReaded.size(); j++) {

                if (fileReaded.get(j).equals("{")) {
                    ++key;
                }
                if (fileReaded.get(j).equals("}")) {
                    --key;
                }
                if (key == 0) {
                    endLines.add(j + 1);
                    break;
                }
            }
        }

        reader.close();
        br.close();

        for (Integer integer : warningsLinesAll) {
            for (int k = 0; k < startLines.size(); k++) {
                if (integer > startLines.get(k) && integer < endLines.get(k)) {
                    findWeaknessAll = true;
                }

            }
        }
        for (Integer integer : warningsLinesFilter) {
            for (int k = 0; k < startLines.size(); k++) {
                if (integer > startLines.get(k) && integer < endLines.get(k)) {
                    this.findWeaknessFilter = true;
                }

            }
        }
    }

    public void staticAnalysis() {
        Pattern findSloc = Pattern.compile("Physical Source Lines of Code");
        Pattern findHits = Pattern.compile("Hits =");
        Pattern findWarnings = Pattern.compile(fileName + ":");

        Path pathFlawAll = Paths.get(pathExp + folderFiles + flawAllFile);
        Path pathFlawFilter = Paths.get(pathExp + folderFiles + flawFilterFile);

        List<String> linhasAll = new ArrayList<>();
        List<String> linhasFilter = new ArrayList<>();
        List<Integer> warningsLinesAll = new ArrayList<>();
        List<Integer> warningsLinesFilters = new ArrayList<>();

        Matcher m;

        try {
            linhasAll = Files.readAllLines(pathFlawAll);
            linhasFilter = Files.readAllLines(pathFlawFilter);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        for (String l : linhasAll) {
            m = findWarnings.matcher(l);
            if (m.find()) {
                warningsLinesAll.add(Integer.parseInt(l.trim().split("\\:")[1]));
            }
        }

        for (String l : linhasFilter) {
            m = findWarnings.matcher(l);
            if (m.find()) {
                warningsLinesFilters.add(Integer.parseInt(l.trim().split("\\:")[1]));
            }
        }

        for (String l : linhasAll) {
            m = findSloc.matcher(l);
            if (m.find()) {
                this.sloc = l.trim().split("=")[1].replaceAll(" ", "");
            }
        }

        for (String l : linhasAll) {
            m = findHits.matcher(l);
            if (m.find()) {
                this.saAll = l.trim().split("=")[1].replaceAll(" ", "");
            }
        }

        for (String l : linhasFilter) {
            m = findHits.matcher(l);
            if (m.find()) {
                this.saFilter = l.trim().split("=")[1].replaceAll(" ", "");
            }
        }

        try {
            getFunctionsLine(warningsLinesAll, warningsLinesFilters);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void execGood() throws Exception {
        List<String> result = new ArrayList();
        result = readResultRTCAFL(pathExp, folderFiles, failedLog_good);

        readResultRTC(rtcRes_good);

        readResultFT(ftRes_good);

        recCategorization(result, categorization_good, "good");
    }

    public void execBad() throws Exception {
        List<String> result = new ArrayList();
        result = readResultRTCAFL(pathExp, folderFiles, failedLog_bad);

        readResultRTC(rtcRes_bad);

        readResultFT(ftRes_bad);

        recCategorization(result, categorization_bad, "bad");
    }

    public void recCategorization(List<String> result, String categorization, String kind) {

        for (String line : result) {
            if (line.contains("FAILED")) {
                numberFail++;
            }
            if (line.contains("PASSED")) {
                numberPass++;
            }
            if (line.contains("CRASH")) {
                numberCrash++;
            }
        }

        try {
            File fw = new File(pathExp + folderFiles + categorization);
            PrintWriter pw = new PrintWriter(new FileOutputStream(fw, false));

            pw.write("cwe,file,kind,sloc,saAll,insideAll,saFilter,insideFilter,rtc,ftCrashes,aflrtc_pass,aflrtc_fail,aflrtc_crash\n");
            pw.write(cwe + "," + fileName + "," + kind + "," + this.sloc + "," + this.saAll 
                    + "," + findWeaknessAll + "," + this.saFilter + "," + findWeaknessFilter + ","
                    + resRTC + "," + numberCrashFT + "," + numberPass + "," + numberFail + "," + numberCrash + "\n");

            pw.close();
        } catch (IOException e) {
            System.out.println("error: e.printStackTrace()");
        }

        System.out.println("cwe,file,kind,sloc,saAll,insideAll,saFilter,insideFilter,rtc,ftCrashes,aflrtc_pass,aflrtc_fail,aflrtc_crash\n");
        System.out.println(cwe + "," + fileName + "," + kind + "," + this.sloc + "," + this.saAll 
        + "," + findWeaknessAll + "," + this.saFilter + "," + findWeaknessFilter + ","
        + resRTC + "," + numberCrashFT + "," + numberPass + "," + numberFail + "," + numberCrash + "\n");

        // TEAR DOWN
        numberCrash = 0;
        numberFail = 0;
        numberPass = 0;
        numberCrashFT = 0;

    }

    public List readResultRTCAFL(String pathExp, String folderFiles, String failedLog)
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

    public void readResultRTC(String failedLog) throws FileNotFoundException, IOException {

        File file = new File(pathExp + folderFiles + failedLog);
        BufferedReader reader = new BufferedReader(new FileReader(file));

        String str = "";
        str = reader.readLine();
        if (str.contains("PASSED")) {
            resRTC = "PASSED";
        } else {
            resRTC = "FAILED";
        }
        reader.close();
    }

    public void readResultFT(String failedLog) {

        File file = new File(pathExp + folderFiles + failedLog);
        File afile[] = file.listFiles();
        int i = 0;

        if (afile.length > 0)
            numberCrashFT = afile.length - 1;

        for (int j = afile.length; i < j; i++) {
            File arquivos = afile[i];
            System.out.println(arquivos.getName());
        }
    }
}
