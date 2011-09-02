package net.ishchenko.idea.nginx;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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


    public static void main(String[] args) throws IOException {

        File wikiDir = new File("wiki.nginx.org");

        File directivesOutputDir = new File("docs/directives");
        FileUtils.deleteDirectory(directivesOutputDir);
        directivesOutputDir.mkdirs();

        File variablesOutputDir = new File("docs/variables");
        FileUtils.deleteDirectory(variablesOutputDir);
        variablesOutputDir.mkdirs();

        Set<File> ambiguousDirectiveFiles = new TreeSet<File>();
        for (File o : FileUtils.listFiles(wikiDir, new RegexFileFilter(".*Module"), FalseFileFilter.FALSE)) {
            processDirectives(o, directivesOutputDir, ambiguousDirectiveFiles);
            processVariables(o, variablesOutputDir);
        }

        for (File ambiguousFile : ambiguousDirectiveFiles) {

            StringBuilder sb = new StringBuilder("<h3>Directive can have multiple meanings. Each variant is separated with horizontal line</h3><hr>");
            sb.append(FileUtils.readFileToString(ambiguousFile));

            FileUtils.writeStringToFile(ambiguousFile, sb.toString());
        }

        System.out.println("All done!");

    }

    private static void processDirectives(File moduleFile, File outputDirectory, Set<File> ambiguousFiles) throws IOException {

        Document doc = Jsoup.parse(moduleFile, "UTF-8");
        for (Element element : doc.select("h2 > span.mw-headline:not([id^=.24])")) {

            String name = element.attr("id");

            StringBuilder description = new StringBuilder();
            Element descriptionPart = element.parent();
            do {
                description.append(descriptionPart.outerHtml());
            } while ((descriptionPart = descriptionPart.nextElementSibling()) != null && !descriptionPart.tagName().equals("h2"));

            File docFile = new File(outputDirectory, name + ".html");
            if (!docFile.exists()) {
                FileUtils.writeStringToFile(docFile, description + "<br><i>Module: " + moduleFile.getName() + "</i>");
            } else {
                ambiguousFiles.add(docFile);
                StringBuilder sb = new StringBuilder(FileUtils.readFileToString(docFile)).append("<hr>");
                sb.append(description);
                sb.append("<br><i>Module: ").append(moduleFile.getName()).append("</i>");
                FileUtils.writeStringToFile(docFile, sb.toString());
            }

        }

    }

    private static void processVariables(File moduleFile, File variablesOutputDir) throws IOException {

        Document doc = Jsoup.parse(moduleFile, "UTF-8");
        for (Element element : doc.select("h2 > span.mw-headline[id^=.24]")) {
            String name = element.attr("id").replace(".24", "");
            String description = element.parent().nextElementSibling().outerHtml();
            FileUtils.writeStringToFile(new File(variablesOutputDir, name + ".html"), description);
        }

    }

}






