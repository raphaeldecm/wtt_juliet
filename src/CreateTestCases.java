import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
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
    private String externVarString;
    private String mockedFunctionsString;
    private Pattern findFunction;
    private List<String> mockedFunctionList;
    private List<String> externVarList;

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
        findBadFunction();
        findGoodFunction();
        findMockedFunctions();
        buildMockedFunctions();
        findExternVar();
        buildExternVar();
        createGoodRTC();
        createBadRTC();
        createBadFT();
        createGoodFT();
        recFunctions();// To use on categorization
    }

    public void recFunctions() {
        File fw = new File(pathExp + folderFiles + "/testedfunctions.log");
        PrintWriter pw;
        try {
            pw = new PrintWriter(new FileOutputStream(fw, false));
            pw.write(badFunction + "\n");
            pw.write(goodFunction + "\n");
            pw.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void compileTester(File tester, int phase, String kind) {
        String mockedList = "";
        String command = "";
        String phaseStr = "";
        for (String string : mockedFunctionList) {
            mockedList += ",--wrap=" + string.replaceAll(" ", "").replaceAll("void", "").split("\\(")[0];
        }

        // gcc $testers$tester_bad -I$include -Wl,--wrap=$mock1 -lcmocka -o bad_a.out 2>
        // output/rtc_bad_err.txt
        if (phase == 1) {
            phaseStr = "rtc";
            if (mockedFunctionList.size() > 0) {
                command = "gcc " + tester.getAbsolutePath() + " -I" + include + " -Wl,--wrap=fgets" + mockedList
                        + " -lcmocka -o " + kind + "_a.out";

            } else {
                command = "gcc " + tester.getAbsolutePath() + " -I" + include + " -Wl,--wrap=fgets -lcmocka -o " + kind
                        + "_a.out";
            }

        } else if (phase == 2) {
            phaseStr = "ft";
            if (mockedFunctionList.size() > 0) {
                command = "afl-gcc " + tester.getAbsolutePath() + " -I" + include + " -Wl,--wrap=fgets" + mockedList
                        + " -lcmocka -o fuzz_" + kind;

            } else {
                command = "afl-gcc " + tester.getAbsolutePath() + " -I" + include
                        + " -Wl,--wrap=fgets -lcmocka -o fuzz_" + kind;
            }
        }
        System.out.println("**** Compile command: " + command);
        try {
            Process p = Runtime.getRuntime().exec(command, null, new File(pathExp + folderFiles));
            p.waitFor();
            BufferedReader buf = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String line = "";
            File file = new File(pathExp + folderFiles + "/output/" + phaseStr + "_" + kind + "_err.txt");
            PrintWriter pw = new PrintWriter(new FileOutputStream(file, true));
            while ((line = buf.readLine()) != null) {
                pw.write(line + "\n");
            }
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void findExternVar() {
        Path path = Paths.get(pathDataSet + fileName);
        Pattern findExtern = Pattern.compile("extern");
        List<String> linhas = new ArrayList<>();
        externVarList = new ArrayList<>();
        Matcher m;

        try {
            linhas = Files.readAllLines(path);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        for (String l : linhas) {
            m = findExtern.matcher(l);
            if (m.find()) {
                externVarList.add(l.replaceAll(";", " = 1;").replaceAll("extern ", ""));
            }
        }
    }

    private void buildExternVar() throws IOException {
        externVarString = "";
        for (String string : externVarList) {
            externVarString += string + "\n";
        }
        System.out.println("################ " + externVarString);
    }

    private void findMockedFunctions() {
        Path path = Paths.get(pathDataSet + fileName);
        // Pattern findFunction = Pattern.compile(".float data");
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
            if (fileName.contains("a.c")) {
                if (m.find()) {
                    // if (!l.startsWith("void " + badFunction + "(") && !l.startsWith("void " +
                    // goodFunction + "(")) {
                    // mockedFunctionList.add(l.replaceAll(";", ""));
                    // }
                    if (l.endsWith(";") && !l.startsWith("void " + badFunction.split("\\(")[0] + "(")
                            && !l.startsWith("void " + goodFunction.split("\\(")[0] + "(")) {
                        mockedFunctionList.add(l.replaceAll(";", ""));
                    }
                }
            } else {
                if (m.find()) {
                    if (l.endsWith(";") && !l.startsWith("void " + badFunction.split("\\(")[0] + "(")
                            && !l.startsWith("void " + goodFunction.split("\\(")[0] + "(")) {
                        mockedFunctionList.add(l.replaceAll(";", ""));
                    }
                }
            }
        }
    }

    private void buildMockedFunctions() throws IOException {
        mockedFunctionsString = "";
        for (String string : mockedFunctionList) {
            if (string.endsWith("()")) {
                mockedFunctionsString += string.split(" ")[0] + " __wrap_" + string.replaceAll("void ", "")
                        + "{int result = (int)(100.0 / data);printIntLine(result);}\n";
            } else {
                mockedFunctionsString += string.split(" ")[0] + " __wrap_" + string.replaceAll("void ", "")
                        + "{int result = (int)(100.0 / data);printIntLine(result);}\n";
            }
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
                // void CWE369_Divide_by_Zero__float_fgets_22_good()
                // void CWE134_Uncontrolled_Format_String__char_file_fprintf_12_bad()

                if (fileName.contains("b.c") || fileName.contains("c.c") || fileName.contains("d.c") || fileName.contains("e.c")) {

                    if (l.contains("_bad") && !l.endsWith(";")) {
                        badFunction = l.split(" ")[1].split("\\(")[0];
                        badFunction += "(input);//";
                        System.out.println("******" + badFunction);
                        break;
                    }
                } else {
                    if (l.endsWith("()") && l.contains("_bad")) {
                        badFunction = l.split(" ")[1].replace("()", "");
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
        Pattern findGoodFunction = Pattern.compile("void");
        Matcher m;

        try {
            linhas = Files.readAllLines(path);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println(fileName + "********GOOD*********");

        for (String l : linhas) {
            m = findGoodFunction.matcher(l);
            if (m.find()) {

                if (fileName.contains("b.c") || fileName.contains("c.c") || fileName.contains("d.c") || fileName.contains("e.c")) {
                    if (l.contains("B2G") && !l.endsWith(";")) {
                        if (!l.endsWith("()")) {
                            goodFunction = l.split(" ")[1].split("\\(")[0];
                            goodFunction += "(input);//";
                        } else {
                            goodFunction = l.split(" ")[1].split("\\(")[0];
                        }
                        System.out.println("******" + goodFunction);
                        break;
                    }
                } else {
                    if (l.contains("B2G") && !l.endsWith(";")) {
                        if (!l.endsWith("()")) {
                            goodFunction = l.split(" ")[2].split("\\(")[0];
                            goodFunction += "(input);//";
                        } else {
                            goodFunction = l.split(" ")[2].split("\\(")[0];
                        }
                        System.out.println("****** a " + goodFunction);
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

        if (externVarList.size() > 0) {
            root.put("externVar", externVarString);
        } else {
            root.put("externVar", "//");
        }

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

        if (externVarList.size() > 0) {
            root.put("externVar", externVarString);
        } else {
            root.put("externVar", "//");
        }

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

        if (externVarList.size() > 0) {
            root.put("externVar", externVarString);
        } else {
            root.put("externVar", "//");
        }

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

        if (externVarList.size() > 0) {
            root.put("externVar", externVarString);
        } else {
            root.put("externVar", "//");
        }

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
