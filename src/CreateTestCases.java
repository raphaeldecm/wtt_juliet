import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class CreateTestCases {

    private static String pathDataSet;
    private static String fileName;
    private static String include;
    private String pathExp;
    private String badFunction;
    private String goodFunction;
    private String folderFiles;
    private String templateDirectory;
    private String cweKind;
    private String testerName;
    private String mockedFunctionsString;
    private Pattern findFunction;
    private List<String> mockedFunctionList;

    public static void main(String[] args) throws Exception {
        pathDataSet = args[0];
        fileName = args[1];
        include = args[2];
        CreateTestCases crt = new CreateTestCases();
        crt.start();
    }

    public CreateTestCases() {
        this.pathExp = System.getProperty("user.dir");
        this.pathExp = this.pathExp.replaceAll("/src", "");
        this.folderFiles = "/Files";
        this.templateDirectory = pathExp + "/templates/";
        this.cweKind = fileName.split("_")[0];
        this.testerName = "tester_wtt_" + cweKind;
        this.findFunction = Pattern.compile("void " + cweKind);
    }

    private void start() throws Exception {
        findMockedFunctions();
        buildMockedFunctions();
        findBadFunction();
        findGoodFunction();
        createGoodRTC();
        createBadRTC();
        createBadFT();
        createGoodFT();
    }

    private void compileTester(File tester, int phase, String kind) {
        String mockedList = "";
        String command = "";
        String phaseStr = "";
        for (String string : mockedFunctionList) {
            mockedList += ",--wrap=" + string.replaceAll(" ", "").replaceAll("void", "").split("\\(")[0];
        }

        //gcc $testers$tester_bad -I$include -Wl,--wrap=$mock1 -lcmocka -o bad_a.out 2> output/rtc_bad_err.txt
        if (phase == 1){
            phaseStr = "rtc";
            if (mockedFunctionList.size() > 0) {
                command = "gcc " + tester.getAbsolutePath() + " -I" + include + " -Wl,--wrap=fgets" + mockedList
                        + " -lcmocka -o "+kind+"_a.out";
    
            } else {
                command = "gcc " + tester.getAbsolutePath() + " -I" + include + " -lcmocka -o "+kind+"_a.out";
            }

        } else if (phase == 2){
            phaseStr = "ft";
            if (mockedFunctionList.size() > 0) {
                command = "afl-gcc " + tester.getAbsolutePath() + " -I" + include + " -Wl,--wrap=fgets" + mockedList
                        + " -lcmocka -o fuzz_"+kind;
    
            } else {
                command = "afl-gcc " + tester.getAbsolutePath() + " -I" + include + " -lcmocka -o fuzz_"+kind;
            }
        }
        System.out.println("DEBUG ****************\n" + command);
        try {
            Process p = Runtime.getRuntime().exec(command, null, new File(pathExp + folderFiles));
            p.waitFor();
            BufferedReader buf = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String line = "";
            File file = new File(pathExp + folderFiles + "/output/"+phaseStr+"_"+kind+"_err.txt");
            PrintWriter pw = new PrintWriter(new FileOutputStream(file, true));
            while ((line = buf.readLine()) != null) {
                pw.write(line + "\n");
            }
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void findMockedFunctions() {
        Path path = Paths.get(pathDataSet + fileName);
        Pattern findFunction = Pattern.compile(".float data");
        List<String> linhas = new ArrayList<>();
        mockedFunctionList = new ArrayList<>();
        Matcher m;

        try {
            linhas = Files.readAllLines(path);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        for (String l : linhas) {
            m = findFunction.matcher(l);

            if (m.find()) {
                if (l.startsWith("void"))
                    mockedFunctionList.add(l.replaceAll(";", ""));
            }
        }
    }

    private void buildMockedFunctions() throws IOException {
        mockedFunctionsString = "";
        for (String string : mockedFunctionList) {
            mockedFunctionsString += string + "{int result = (int)(100.0 / data);printIntLine(result);}\n";
        }
    }

    public void findBadFunction() throws FileNotFoundException {

        Path path = Paths.get(pathDataSet + fileName);
        List<String> linhas = new ArrayList<>();
        Matcher m;

        try {
            linhas = Files.readAllLines(path);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println(fileName + "********BAD*********");
        for (String l : linhas) {
            m = findFunction.matcher(l);
            if (m.find()) {
                if (!l.contains("(char")) {
                    if (l.endsWith("_bad") || l.endsWith("_badSink")) {
                        badFunction = l.split(" ")[1].split("\\(")[0];
                        System.out.println("******" + badFunction);
                        break;
                    }
                }
            }
        }

    }

    // O nome das funções good varia entre os arquivos testados
    // Nesta versão, criamos casos de teste para uma função good e bad
    public void findGoodFunction() throws FileNotFoundException {

        Path path = Paths.get(pathDataSet + fileName);
        List<String> linhas = new ArrayList<>();
        Matcher m;

        try {
            linhas = Files.readAllLines(path);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println(fileName + "********GOOD*********");
        for (String l : linhas) {
            m = findFunction.matcher(l);
            if (m.find()) {
                if (!l.contains("(char")) {
                    if (l.endsWith("_good()")) {
                        goodFunction = l.split(" ")[1].replace("()", "");
                        System.out.println("******" + goodFunction);
                        break;
                    }
                }
            }
        }
    }

    private void createBadFT() throws IOException, TemplateException {

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);

        cfg.setDefaultEncoding("UTF-8");
        cfg.setDirectoryForTemplateLoading(new File(templateDirectory + cweKind));
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setFallbackOnNullLoopVariable(false);

        Map<String, Object> root = new HashMap<>();

        // Put string "user" into the root
        root.put("pathDataSet", pathDataSet);
        root.put("fileName", fileName);
        root.put("type", "bad");
        root.put("testedFunction", badFunction);
        if (mockedFunctionList.size() > 0) {
            root.put("mockedFunctions", mockedFunctionsString);
        } else {
            root.put("mockedFunctions", "//");
        }

        Template tmpl = cfg.getTemplate(cweKind + "_file_ft.ftl");

        File file = new File(pathExp + folderFiles + "/testers/" + testerName + "_bad_ft.c");
        Writer fileWriter = new FileWriter(file);

        try {
            tmpl.process(root, fileWriter);
        } finally {
            fileWriter.close();
        }

        compileTester(file, 2, "bad");

    }

    private void createGoodFT() throws IOException, TemplateException {

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);

        cfg.setDefaultEncoding("UTF-8");
        cfg.setDirectoryForTemplateLoading(new File(templateDirectory + cweKind));
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setFallbackOnNullLoopVariable(false);

        Map<String, Object> root = new HashMap<>();

        // Put string "user" into the root
        root.put("pathDataSet", pathDataSet);
        root.put("fileName", fileName);
        root.put("type", "good");
        root.put("testedFunction", goodFunction);
        if (mockedFunctionList.size() > 0) {
            root.put("mockedFunctions", mockedFunctionsString);
        } else {
            root.put("mockedFunctions", "//");
        }

        Template tmpl = cfg.getTemplate(cweKind + "_file_ft.ftl");

        File file = new File(pathExp + folderFiles + "/testers/" + testerName + "_good_ft.c");
        Writer fileWriter = new FileWriter(file);
        try {
            tmpl.process(root, fileWriter);
        } finally {
            fileWriter.close();
        }
        
        compileTester(file, 2, "good");

    }

    private void createGoodRTC() throws IOException, TemplateException {

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);

        cfg.setDefaultEncoding("UTF-8");
        cfg.setDirectoryForTemplateLoading(new File(templateDirectory + cweKind));
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setFallbackOnNullLoopVariable(false);

        Map<String, Object> root = new HashMap<>();

        // Put string "user" into the root
        root.put("pathDataSet", pathDataSet);
        root.put("fileName", fileName);
        root.put("testedFunction", goodFunction);
        if (mockedFunctionList.size() > 0) {
            root.put("mockedFunctions", mockedFunctionsString);
        } else {
            root.put("mockedFunctions", "//");
        }

        Template tmpl = cfg.getTemplate(cweKind + "_file_rtc.ftl");

        File file = new File(pathExp + folderFiles + "/testers/" + testerName + "_good_rtc.c");
        Writer fileWriter = new FileWriter(file);

        try {
            tmpl.process(root, fileWriter);
        } finally {
            fileWriter.close();
        }

        compileTester(file, 1, "good");

    }

    private void createBadRTC() throws IOException, TemplateException {

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);

        cfg.setDefaultEncoding("UTF-8");
        cfg.setDirectoryForTemplateLoading(new File(templateDirectory + cweKind));
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setFallbackOnNullLoopVariable(false);

        Template tmpl = cfg.getTemplate(cweKind + "_file_rtc.ftl");
        Map<String, Object> root = new HashMap<>();

        // Put string "user" into the root
        root.put("pathDataSet", pathDataSet);
        root.put("fileName", fileName);
        root.put("testedFunction", badFunction);
        if (mockedFunctionList.size() > 0) {
            root.put("mockedFunctions", mockedFunctionsString);
        } else {
            root.put("mockedFunctions", "//");
        }
        File file = new File(pathExp + folderFiles + "/testers/" + testerName + "_bad_rtc.c");
        Writer fileWriter = new FileWriter(file);

        try {
            tmpl.process(root, fileWriter);
        } finally {
            fileWriter.close();
        }

        compileTester(file, 1, "bad");

    }

}
