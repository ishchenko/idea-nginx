package net.ishchenko.idea.nginx;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 01.04.2010
 * Time: 16:04:32
 */

/**
 * quick and dirty keywords extractor
 * <pre>
 * input directory   ./c_src
 * output files  ./keywords.txt
 *               ./variables.txt
 * </pre>
 *
 * Tested for nginx 0.8.35
 */
public class KeywordsFromSourcesGenerator {

    private static final Pattern PREPROCESSOR_DIRECTIVE_PATTERN = Pattern.compile("#.+");

    private static final Pattern DIRECTIVE_BLOCK_PATTERN = Pattern.compile("static\\s+ngx_command_t\\s+[^\\[]+\\[\\]\\s*=\\s*(\\{[^;]+);");
    private static final Pattern DIRECTIVE_PATTERN = Pattern.compile("(\\{[^}]*},)+");
    private static final Pattern DIRECTIVE_NAME_PATTERN = Pattern.compile("ngx_string\\(\"([\\w_]+)\"\\)");

    private static final Pattern VARIABLE_BLOCK_PATTERN = Pattern.compile("static\\s+ngx_\\w+_variable_t\\s+[^\\[]+\\[\\]\\s*=\\s*(\\{[^;]+);");
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("(\\{[^}]*},)+");
    private static final Pattern VARIABLE_NAME_PATTERN = Pattern.compile("ngx_string\\(\"([\\w_]+)\"\\)");

    public static void main(String[] args) throws IOException {
        final Set<String> directives = new TreeSet<>();
        final Set<String> variables = new TreeSet<>();

        try (Stream<Path> stream = Files.find(Paths.get(System.getProperty("user.home") + "/git/nginx/src"), 100, (path, attr) -> String.valueOf(path).endsWith(".c"))) {
            stream.forEach(path -> {
                try {
                    getDirectives(path, directives);
                    getVariables(path, variables);
                } catch (IOException e) {
                    // TODO determine proper clean up
                    throw new RuntimeException(e);
                }
            });
        }

        Files.write(Paths.get("keywords.txt"), String.join("\n", directives).getBytes());
        Files.write(Paths.get("variables.txt"), String.join("\n", variables).getBytes());

        try (Stream<Path> stream = Files.find(Paths.get(System.getProperty("user.home") + "/git/lua-nginx-module/src"), 100, (path, attr) -> String.valueOf(path).endsWith(".c"))) {
            stream.forEach(path -> {
                try {
                    getDirectives(path, directives);
                    getVariables(path, variables);
                } catch (IOException e) {
                    // TODO determine proper clean up
                    throw new RuntimeException(e);
                }
            });
        }

        Files.write(Paths.get("openrestykeywords.txt"), String.join("\n", directives).getBytes());
        Files.write(Paths.get("openrestyvariables.txt"), String.join("\n", variables).getBytes());

        System.out.println("All done!");
    }

    private static void getDirectives(Path path, Set<String> directives) throws IOException {
        Matcher directiveBlockMatcher = DIRECTIVE_BLOCK_PATTERN.matcher(new String(Files.readAllBytes(path)));
        while (directiveBlockMatcher.find()) {
            String directiveBlock = directiveBlockMatcher.group(1);

            directiveBlock = directiveBlock.substring(directiveBlock.indexOf('{') + 1, directiveBlock.lastIndexOf('}') - 1);
            directiveBlock = directiveBlock.substring(0, directiveBlock.lastIndexOf("ngx_null_command") - 1).trim();

            //some ad hoc magic
            directiveBlock = directiveBlock.replace("#ifdef SSL_OP_CIPHER_SERVER_PREFERENCE\n", "");
            directiveBlock = directiveBlock.replace("ngx_mail_ssl_nosupported, 0, 0, ngx_mail_ssl_openssl097 },", "");
            directiveBlock = PREPROCESSOR_DIRECTIVE_PATTERN.matcher(directiveBlock).replaceAll("").trim();

            Matcher directiveMatcher = DIRECTIVE_PATTERN.matcher(directiveBlock);
            while (directiveMatcher.find()) {

                String directive = directiveMatcher.group(1);
                directive = directive.trim();
                directive = directive.substring(1, directive.length() - 2);

                String[] items = directive.split(",\n");
                Matcher directiveNameMatcher = DIRECTIVE_NAME_PATTERN.matcher(items[0]);
                if (directiveNameMatcher.find()) {
                    String directiveName = directiveNameMatcher.group(1);
                    for (String flag : items[1].split("\\|")) {
                        directiveName += " " + flag.trim();
                    }
                    directives.add(directiveName);
                } else {
                    System.out.println("Oops! " + items[0] + " won't match a directive name pattern");
                }
            }
        }
    }

    private static void getVariables(Path path, Set<String> variables) throws IOException {
        Matcher variableBlockMatcher = VARIABLE_BLOCK_PATTERN.matcher(new String(Files.readAllBytes(path)));

        while (variableBlockMatcher.find()) {

            String variableBlock = variableBlockMatcher.group(1);

            variableBlock = variableBlock.substring(variableBlock.indexOf('{') + 1, variableBlock.lastIndexOf('}') - 1);
            variableBlock = variableBlock.substring(0, variableBlock.lastIndexOf("ngx_null_string") - 1).trim();

            variableBlock = PREPROCESSOR_DIRECTIVE_PATTERN.matcher(variableBlock).replaceAll("").trim();

            Matcher variableMatcher = VARIABLE_PATTERN.matcher(variableBlock);
            while (variableMatcher.find()) {
                String variable = variableMatcher.group(1);

                variable = variable.trim();
                variable = variable.substring(1, variable.length() - 2);

                String[] items = variable.split(",\n");
                Matcher variableNameMatcher = VARIABLE_NAME_PATTERN.matcher(items[0]);
                if (variableNameMatcher.find()) {
                    variables.add(variableNameMatcher.group(1));
                } else {
                    System.out.println("Opps! " + items[0] + " won't match a variable name pattern");
                }
            }
        }
    }

}
