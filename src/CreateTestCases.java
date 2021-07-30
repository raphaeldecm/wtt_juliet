import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class CreateTestCases {

    private static String pathDataSet;
    private static String fileName;
    private String pathExp;
    private String badFunction;
    private String goodFunction;
    private String folderFiles;
    private String templateDirectory;
    private String cweKind;
    private String testerName;

    public static void main(String[] args) throws Exception {
        pathDataSet = args[0];
        fileName = args[1];
        CreateTestCases crt = new CreateTestCases();
        crt.start();
    }

    public CreateTestCases() {
        this.pathExp = System.getProperty("user.dir");
        this.pathExp = this.pathExp.replaceAll("/src", "");
        this.folderFiles = "/Files";
        this.templateDirectory = pathExp + "/templates/";
        this.cweKind = fileName.split("_")[0];
        this.badFunction = fileName.replaceAll("\\.c", "_bad");
        this.goodFunction = "goodB2G";
        this.testerName = "tester_wtt_" + cweKind;
    }

    private void start() throws Exception {
        createGoodRTC();
        createBadRTC();
        createBadFT();
        createGoodFT();
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

        Template tmpl = cfg.getTemplate("CWE134_file_ft.ftl");

        Writer fileWriter = new FileWriter(new File(pathExp+folderFiles+"/testers/"+testerName+"_bad_ft.c"));
        try {
            tmpl.process(root, fileWriter);
        } finally {
            fileWriter.close();
        }

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

        Template tmpl = cfg.getTemplate("CWE134_file_ft.ftl");

        Writer fileWriter = new FileWriter(new File(pathExp+folderFiles+"/testers/"+testerName+"_good_ft.c"));
        try {
            tmpl.process(root, fileWriter);
        } finally {
            fileWriter.close();
        }

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

        Template tmpl = cfg.getTemplate("CWE134_file_rtc.ftl");

        Writer fileWriter = new FileWriter(new File(pathExp+folderFiles+"/testers/"+testerName+"_good_rtc.c"));

        try {
            tmpl.process(root, fileWriter);
        } finally {
            fileWriter.close();
        }

    }

    private void createBadRTC() throws IOException, TemplateException {

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);

        cfg.setDefaultEncoding("UTF-8");
        cfg.setDirectoryForTemplateLoading(new File(templateDirectory + cweKind));
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setFallbackOnNullLoopVariable(false);

        Template tmpl = cfg.getTemplate("CWE134_file_rtc.ftl");
        Map<String, Object> root = new HashMap<>();

        // Put string "user" into the root
        root.put("pathDataSet", pathDataSet);
        root.put("fileName", fileName);
        root.put("testedFunction", badFunction);


        Writer fileWriter = new FileWriter(new File(pathExp+folderFiles+"/testers/"+testerName+"_bad_rtc.c"));
        try {
            tmpl.process(root, fileWriter);
        } finally {
            fileWriter.close();
        }

    }

}
