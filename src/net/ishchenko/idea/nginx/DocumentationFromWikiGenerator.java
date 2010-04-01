package net.ishchenko.idea.nginx;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 01.04.2010
 * Time: 19:05:13
 */

/**
 * quick and dirty wiki documentation extractor <br>
 * <pre>
 * input directory      ./wiki.nginx.org
 * output files:        ./docs/directives/&lt;directive1 name&gt;.html
 *                      ./docs/directives/&lt;directive2 name&gt;.html
 *                      ...
 *                      ./docs/variables/&lt;variable1 name&gt;.html
 *                      ./docs/variables/&lt;variable2 name&gt;.html
 *                      ...
 * </pre>
 * Tested for nginx 0.8.35
 */
public class DocumentationFromWikiGenerator {


    private static final Pattern DIRECTIVE_NAME_PATTERN = Pattern.compile("<a name=\"([a-z_]+)\" id=\"([a-z_]+)\"></a><h2>.*");
    private static final Pattern VARIABLE_NAME_PATTERN = Pattern.compile("<a name=\"\\.24([a-z_]+)\" id=\"\\.24([a-z_]+)\"></a><h2>.*");

    public static void main(String[] args) throws IOException {

        File wikiDir = new File("wiki.nginx.org");

        File directivesOutputDir = new File("docs/directives");
        FileUtils.deleteDirectory(directivesOutputDir);
        directivesOutputDir.mkdirs();

        File variablesOutputDir = new File("docs/variables");
        FileUtils.deleteDirectory(variablesOutputDir);
        variablesOutputDir.mkdirs();

        Set<File> ambiguousDirectiveFiles = new TreeSet<File>();
        for (Object o : FileUtils.listFiles(wikiDir, new RegexFileFilter("Nginx.*Module"), FalseFileFilter.FALSE)) {
            processDirectives((File) o, directivesOutputDir, ambiguousDirectiveFiles);
            processVariables((File) o, variablesOutputDir);
        }

        for (File ambiguousFile : ambiguousDirectiveFiles) {

            StringBuilder sb = new StringBuilder("<h3>Directive can have multiple meanings. Each variant is separated with horizontal line</h3><hr>");
            sb.append(FileUtils.readFileToString(ambiguousFile));

            FileUtils.writeStringToFile(ambiguousFile, sb.toString());
        }

        System.out.println("All done!");


    }

    private static void processDirectives(File moduleFile, File outputDirectory, Set<File> ambiguousFiles) throws IOException {

        String fileContents = FileUtils.readFileToString(moduleFile);

        Matcher nameMatcher = DIRECTIVE_NAME_PATTERN.matcher(fileContents);
        while (nameMatcher.find()) {

            String filename = nameMatcher.group(1);

            String contents = fileContents.substring(nameMatcher.end());

            String doc = contents.substring(0, contents.indexOf("<a name"));
            doc = doc.replaceAll("<style>.*\n", "").replaceAll(".code .*\n", "");
            doc = doc.replaceAll("<a href=[^>]*>", "").replaceAll("</a>", "").replaceAll("</style>", "");

            File docFile = new File(outputDirectory, filename + ".html");
            if (!docFile.exists()) {
                FileUtils.writeStringToFile(docFile, doc + "<br><i>Module: " + moduleFile.getName() + "</i>");
            } else {
                ambiguousFiles.add(docFile);

                StringBuilder sb = new StringBuilder(FileUtils.readFileToString(docFile)).append("<hr>");
                sb.append(doc);
                sb.append("<br><i>Module: ").append(moduleFile.getName()).append("</i>");

                FileUtils.writeStringToFile(docFile, sb.toString());

            }

        }

    }

    private static void processVariables(File moduleFile, File variablesOutputDir) throws IOException {

        String fileContents = FileUtils.readFileToString(moduleFile);

        Matcher nameMatcher = VARIABLE_NAME_PATTERN.matcher(fileContents);
        while (nameMatcher.find()) {

            String contents = fileContents.substring(nameMatcher.end());

            int index = contents.indexOf("<a name");
            if (index == -1) {
                index = contents.indexOf("<!--");
            }
            String doc = contents.substring(0, index);
            doc = doc.replaceAll("<style>.*\n", "").replaceAll(".code .*\n", "");
            doc = doc.replaceAll("<a href=[^>]*>", "").replaceAll("</a>", "").replaceAll("</style>", "");

            FileUtils.writeStringToFile(new File(variablesOutputDir, nameMatcher.group(2) + ".html"), doc);
        }

    }


}






