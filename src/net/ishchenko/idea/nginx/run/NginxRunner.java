package net.ishchenko.idea.nginx.run;

import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.DefaultProgramRunner;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 17.10.2009
 * Time: 19:44:31
 */
public class NginxRunner extends DefaultProgramRunner {

    @NotNull
    public String getRunnerId() {
        return "NginxRunner";
    }

    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return DefaultRunExecutor.EXECUTOR_ID.equals(executorId) && profile instanceof NginxRunConfiguration;
    }

}
