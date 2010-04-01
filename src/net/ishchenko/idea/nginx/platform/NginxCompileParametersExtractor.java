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
import net.ishchenko.idea.nginx.NginxBundle;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 01.08.2009
 * Time: 0:22:46
 */
public class NginxCompileParametersExtractor {

    private static final Pattern OUTPUT_PATTERN = Pattern.compile("nginx version: nginx/([\\d\\.]+)\r?\n(?:.*\r?\n)*configure arguments: (.*)");

    /**
     * Runs file with -V argument and matches the output against OUTPUT_PATTERN.
     * @param from executable to be run with -V command line argument
     * @return Parameters parsed out from process output
     * @throws PlatformDependentTools.ThisIsNotNginxExecutableException if file could not be run, or
     * output would not match against expected pattern
     */
    public static NginxCompileParameters extract(VirtualFile from) throws PlatformDependentTools.ThisIsNotNginxExecutableException {

        NginxCompileParameters result = new NginxCompileParameters();

        Executor executor = new DefaultExecutor();
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        try {
            executor.setStreamHandler(new PumpStreamHandler(os, os));
            executor.execute(CommandLine.parse(from.getPath() + " -V"));
        } catch (IOException e) {
            throw new PlatformDependentTools.ThisIsNotNginxExecutableException(e);
        }

        String output = os.toString();
        Matcher matcher = OUTPUT_PATTERN.matcher(output);
        if (matcher.find()) {
            String version = matcher.group(1);
            String params = matcher.group(2);

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

        if (name.equals("--conf-path")) {
            result.setConfigurationPath(value);
        } else if (name.equals("--pid-path")) {
            result.setPidPath(value);
        } else if (name.equals("--prefix")) {
            result.setPrefix(value);
        } else if (name.equals("--http-log-path")) {
            result.setHttpLogPath(value);
        } else if (name.equals("--error-log-path")) {
            result.setErrorLogPath(value);
        }

    }

    private static void handleFlag(NginxCompileParameters result, String flag) {

        //no flags to handle at the moment

    }
}
