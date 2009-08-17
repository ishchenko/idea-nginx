package net.ishchenko.idea.nginx.run;

import com.intellij.execution.filters.RegexpFilter;
import com.intellij.openapi.project.Project;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 13.08.2009
 * Time: 18:27:37
 * To change this template use File | Settings | File Templates.
 */

/**
 * Responsible for clickable configuration file references in console
 */
public class NginxConsoleFilter extends RegexpFilter {

    public NginxConsoleFilter(Project project) {
        super(project, ".+ in $FILE_PATH$:$LINE$");
    }
}
