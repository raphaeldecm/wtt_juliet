import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

public class TestingCWE {
    private String pathExp;
    private String folderFiles;
    private String aflLog_bad;
    private String aflLog_good;
    private String rtcFile_bad;
    private String rtcFile_good;
    private String logInput_bad;
    private String logInput_good;
    private String failedLog_bad;
    private String failedLog_good;
    private String time;

    public static void main(String[] args) throws Exception {
        TestingCWE tc = new TestingCWE();
        tc.start();
    }

    public TestingCWE() {
        this.pathExp = System.getProperty("user.dir");
        this.pathExp = this.pathExp.replaceAll("/src", "");
        this.folderFiles = "/Files";
        this.aflLog_bad = "/log_afl_bad.txt";
        this.aflLog_good = "/log_afl_good.txt";
        this.rtcFile_bad = "./fuzz_bad";
        this.rtcFile_good = "./fuzz_good";
        this.logInput_bad = "/log_bad.txt";
        this.logInput_good = "/log_good.txt";
        this.failedLog_bad = "/result_bad.json";
        this.failedLog_good = "/result_good.json";
        this.time = "/time.csv";
    }

    private void start() throws Exception {
        File file = new File(this.pathExp + this.folderFiles + this.time);
        PrintWriter pw = new PrintWriter(new FileOutputStream(file, true));

        long startTime = System.currentTimeMillis();
        exeBad();
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        pw.write(totalTime+",");

        startTime = System.currentTimeMillis();
        exeGood();
        endTime = System.currentTimeMillis();
        totalTime = endTime - startTime;
        pw.write(totalTime+",");

        pw.close();

    }

    public void exeGood() throws Exception {
        List<String> log = new ArrayList();
        log = readLog(pathExp, folderFiles, aflLog_good);

        int id = 0;

        for (String s : log) {
            ++id;
            System.out.println("Processo: " + id + "de " + log.size());
            System.out.println("Input: " + s);
            String input = s;
            execTestCases(rtcFile_good, input, pathExp, folderFiles, id, log.size(), logInput_good);
        }
        filterFailed(pathExp, folderFiles, failedLog_good, logInput_good);

        // try {
        // File fw = new File(pathExp + folderFiles + "/timeExec.txt");
        // PrintWriter pw = new PrintWriter(new FileOutputStream(fw, true));
        // pw.close();
        // } catch (IOException e) {
        // System.out.println("error: e.printStackTrace()");
        // }
    }

    public void exeBad() throws Exception {
        List<String> log = new ArrayList();
        log = readLog(pathExp, folderFiles, aflLog_bad);

        int id = 0;

        for (String s : log) {
            ++id;
            System.out.println("Processo: " + id + "de " + log.size());
            System.out.println("Input: " + s);
            String input = s;
            execTestCases(rtcFile_bad, input, pathExp, folderFiles, id, log.size(), logInput_bad);
        }
        filterFailed(pathExp, folderFiles, failedLog_bad, logInput_bad);

        // try {
        // File fw = new File(pathExp + folderFiles + "/timeExec.txt");
        // PrintWriter pw = new PrintWriter(new FileOutputStream(fw, true));
        // pw.close();
        // } catch (IOException e) {
        // System.out.println("error: e.printStackTrace()");
        // }
    }

    public void filterFailed(String pathExp, String folderFiles, String failedLog, String logInput)
            throws FileNotFoundException, IOException {

        String str = "";

        File fileRead = new File(pathExp + folderFiles + logInput);
        BufferedReader reader = new BufferedReader(new FileReader(fileRead));

        List<String> lines = new ArrayList<>();
        List<String> results = new ArrayList<>();

        while ((str = reader.readLine()) != null) {
            lines.add(str);
        }

        File fileWrite = new File(pathExp + folderFiles + failedLog);

        PrintWriter pw = new PrintWriter(new FileOutputStream(fileWrite, true));

        String id = "";
        String input = "";
        String result = "";

        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).trim().replace("#", "").split(":")[0].equals("id")) {
                id = lines.get(i).trim().replace("#", "").split(":")[1];
            } else if (lines.get(i).trim().replace("#", "").split(":")[0].equals("input")) {
                input = lines.get(i).trim().replace("#", " ").split(":")[1];
            } else if (lines.get(i).trim().contains("##")) {
                if (lines.get(i - 1).contains("PASSED")) {
                    result = "PASSED";
                } else if (lines.get(i - 1).contains("FAILED")) {
                    result = "FAILED";
                } else {
                    result = "CRASH";
                }
            }
            if (!result.equals("")) {
                results.add("{\"id\":\"" + id + "\",\"input\":\"" + input + "\",\"result\":\"" + result + "\"}");
                // pw.write("{\"id\":\"" + id + "\",\"input\":\"" + input + "\",\"result\":\"" +
                // result + "\"}");

                id = "";
                input = "";
                result = "";
            }
        }

        for (int i = 0; i < results.size(); i++) {
            System.out.println("i:" + i + "result: " + results.size());
            if ((i + 1) == results.size()) {
                pw.write(results.get(i));
            } else {
                pw.write(results.get(i) + ",\n");
            }
        }

        System.out.println("Size Lines: " + lines.size());
        pw.close();
        reader.close();
    }

    public void execTestCases(String fileFuzz, String input, String path, String folder, int id, int size,
            String logInput) throws IOException {
        try {
            Process process = Runtime.getRuntime().exec(fileFuzz, null, new File(path + folder));

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            bw.write(input);
            bw.close();

            BufferedReader buf = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            File file = new File(path + folder + logInput);
            PrintWriter pw = new PrintWriter(new FileOutputStream(file, true));

            String line = "";

            pw.write("#id:" + id + "#\n");
            pw.write("#input:" + input + "#\n");

            while (true) {
                System.out.println("Readlog Execution: " + id + " from " + size);
                if (line != null) {
                    pw.write(line + "\n");
                } else {
                    break;
                }
                line = buf.readLine();
            }
            pw.write("############################\n\n");
            pw.close();
        } catch (IOException exception) {
            System.out.println("exception.printStackTrace()");
        }
    }

    public List readLog(String pathExp, String folderFiles, String aflLog) throws FileNotFoundException, IOException {

        List<String> lines = new ArrayList<>();
        String str = "";

        File file = new File(pathExp + folderFiles + aflLog);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        int i = 0;

        while (true) {
            ++i;
            System.out.println("Readlog Execution: " + i);
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