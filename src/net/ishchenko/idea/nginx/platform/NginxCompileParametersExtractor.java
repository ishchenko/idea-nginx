/*
 * Copyright 2009 Max Ishchenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ishchenko.idea.nginx.platform;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.ishchenko.idea.nginx.NginxBundle;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 01.08.2009
 * Time: 0:22:46
 */
public class NginxCompileParametersExtractor {

    /**
     * Runs file with -V argument and matches the output against OUTPUT_PATTERN.
     *
     * @param from executable to be run with -V command line argument
     * @return Parameters parsed out from process output
     * @throws PlatformDependentTools.ThisIsNotNginxExecutableException if file could not be run, or
     *                                                                  output would not match against expected pattern
     */
    public static NginxCompileParameters extract(VirtualFile from) throws PlatformDependentTools.ThisIsNotNginxExecutableException {

        NginxCompileParameters result = new NginxCompileParameters();

        StringBuilder output = new StringBuilder();

        try {
            ProcessBuilder pb = new ProcessBuilder(from.getPath(), "-V");
            Process process = pb.start();
            BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()));

            process.waitFor();
            String line = "";
            while ((line = errorReader.readLine()) != null) {
                output.append(line).append("\n");
            }
        } catch (IOException | InterruptedException e) {
            throw new PlatformDependentTools.ThisIsNotNginxExecutableException(e);
        }

        Matcher versionMatcher = Pattern.compile("nginx version: (nginx|openresty)/([\\d.]+)").matcher(output.toString());
        Matcher configureArgumentsMatcher = Pattern.compile("configure arguments: (.*)").matcher(output.toString());

        if (versionMatcher.find() && configureArgumentsMatcher.find()) {
            String version = versionMatcher.group(2);
            String params = configureArgumentsMatcher.group(1);

            result.setVersion(version);

            Iterable<String> namevalues = StringUtil.split(params, " ");
            for (String namevalue : namevalues) {
                int eqPosition = namevalue.indexOf('=');
                if (eqPosition == -1) {
                    handleFlag(result, namevalue);
                } else {
                    handleNameValue(result, namevalue.substring(0, eqPosition), namevalue.substring(eqPosition + 1));
                }
            }
        } else {
            throw new PlatformDependentTools.ThisIsNotNginxExecutableException(NginxBundle.message("run.configuration.outputwontmatch"));
        }

        return result;
    }

    private static void handleNameValue(NginxCompileParameters result, String name, String value) {
        switch (name) {
            case "--conf-path":
                result.setConfigurationPath(value);
                break;
            case "--pid-path":
                result.setPidPath(value);
                break;
            case "--prefix":
                result.setPrefix(value);
                break;
            case "--http-log-path":
                result.setHttpLogPath(value);
                break;
            case "--error-log-path":
                result.setErrorLogPath(value);
                break;
        }
    }

    private static void handleFlag(NginxCompileParameters result, String flag) {
        //no flags to handle at the moment
    }
}
