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

package net.ishchenko.idea.nginx.configurator;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 22.07.2009
 * Time: 15:29:24
 */

import java.util.Objects;

/**
 * Server descriptor that is created in "nginx Configuration files" menu.
 * All paths are absolute.
 */
public class NginxServerDescriptor implements Cloneable {

    private String id;
    private String name = "nginx server";
    private String executablePath = "";
    private String configPath = "";
    private String pidPath = "";
    private String globals = "";
    private String httpLogPath = "";
    private String errorLogPath = "";

    public NginxServerDescriptor() {
        id = "nginx.descriptor." + System.currentTimeMillis();
    }

    public String getConfigPath() {
        return configPath;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    public String getExecutablePath() {
        return executablePath;
    }

    public void setExecutablePath(String executablePath) {
        this.executablePath = executablePath;
    }

    public String getGlobals() {
        return globals;
    }

    public void setGlobals(String globals) {
        this.globals = globals;
    }

    public String getId() {
        return id;
    }

    /**
     * Do not remove! Used for deserializing!
     */
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPidPath() {
        return pidPath;
    }

    public void setPidPath(String pidPath) {
        this.pidPath = pidPath;
    }

    public String getHttpLogPath() {
        return httpLogPath;
    }

    public void setHttpLogPath(String httpLogPath) {
        this.httpLogPath = httpLogPath;
    }

    public String getErrorLogPath() {
        return errorLogPath;
    }

    public void setErrorLogPath(String errorLogPath) {
        this.errorLogPath = errorLogPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NginxServerDescriptor that = (NginxServerDescriptor) o;

        if (!Objects.equals(configPath, that.configPath)) return false;
        if (!Objects.equals(errorLogPath, that.errorLogPath)) return false;
        if (!Objects.equals(executablePath, that.executablePath))
            return false;
        if (!Objects.equals(globals, that.globals)) return false;
        if (!Objects.equals(httpLogPath, that.httpLogPath)) return false;
        if (!Objects.equals(id, that.id)) return false;
        if (!Objects.equals(name, that.name)) return false;
        if (!Objects.equals(pidPath, that.pidPath)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (executablePath != null ? executablePath.hashCode() : 0);
        result = 31 * result + (configPath != null ? configPath.hashCode() : 0);
        result = 31 * result + (pidPath != null ? pidPath.hashCode() : 0);
        result = 31 * result + (globals != null ? globals.hashCode() : 0);
        result = 31 * result + (httpLogPath != null ? httpLogPath.hashCode() : 0);
        result = 31 * result + (errorLogPath != null ? errorLogPath.hashCode() : 0);
        return result;
    }

    @Override
    public NginxServerDescriptor clone() {
        try {
            return (NginxServerDescriptor) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}