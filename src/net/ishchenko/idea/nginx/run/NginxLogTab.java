package net.ishchenko.idea.nginx.run;

import com.intellij.diagnostic.logging.AdditionalTabComponent;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.io.FileUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 13.08.2009
 * Time: 12:51:36
 */

/**
 * Inspired by LogConsoleImpl
 */
public class NginxLogTab extends AdditionalTabComponent {

    private static final Logger logger = Logger.getInstance("net.ishchenko.idea.nginx.run.NginxLogTab");

    private final LightProcessHandler processHandler = new LightProcessHandler();
    private ConsoleView console;
    private boolean stuffAdded;
    private ReaderThread readerThread;
    private File file;
    private long skip;

    public NginxLogTab(Project project, File file) {

        super(new BorderLayout());

        this.file = file;

        if (file.exists()) {
            skip = file.length();
        }

        console = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
        console.addMessageFilter(new NginxConsoleFilter(project));
        console.attachToProcess(processHandler);
        readerThread = new ReaderThread(file);

    }

    public String getTabTitle() {
        return file.getName();
    }

    @NotNull
    @Override
    public JComponent getComponent() {
        if (!stuffAdded) {
            stuffAdded = true;
            add(console.getComponent(), BorderLayout.CENTER);
        }

        return this;
    }

    public void poke() {

        if (readerThread == null) {
            return;
        }
        if (!readerThread.isRunning) {
            readerThread.startRunning();
            ApplicationManager.getApplication().executeOnPooledThread(readerThread);
        } else if (readerThread.isRunning) {
            readerThread.stopRunning();
        }

    }

    public JComponent getPreferredFocusableComponent() {
        return console.getPreferredFocusableComponent();
    }

    public void dispose() {

        if (readerThread != null && readerThread.reader != null) {
            readerThread.stopRunning();
            try {
                readerThread.reader.close();
            } catch (IOException ioexception) {
                logger.warn(ioexception);
            }
            readerThread.reader = null;
            readerThread = null;
        }
        if (console != null) {
            console.dispose();
            console = null;
        }

    }

    public ActionGroup getToolbarActions() {
        return new DefaultActionGroup();
    }

    public JComponent getSearchComponent() {
        return null;
    }

    public String getToolbarPlace() {
        return ActionPlaces.UNKNOWN;
    }

    public JComponent getToolbarContextComponent() {
        return console.getComponent();
    }

    public boolean isContentBuiltIn() {
        return false;
    }


    private static class LightProcessHandler extends ProcessHandler {

        protected void destroyProcessImpl() {
            throw new UnsupportedOperationException();
        }

        protected void detachProcessImpl() {
            throw new UnsupportedOperationException();
        }

        public boolean detachIsDefault() {
            return false;
        }

        public OutputStream getProcessInput() {
            return null;
        }

        private LightProcessHandler() {
        }

    }

    private class ReaderThread implements Runnable {

        private BufferedReader reader;
        private boolean isRunning;

        public void run() {
            if (reader == null) {
                return;
            }
            while (isRunning) {
                try {
                    for (int i = 0; i++ < 100 && isRunning && reader != null && reader.ready(); processLine(reader.readLine())) {
                    }
                    synchronized (this) {
                        wait(100L);
                    }
                }
                catch (IOException ioexception) {
                    logger.error(ioexception);
                }
                catch (InterruptedException interruptedexception) {
                    dispose();
                }
            }
        }

        public void startRunning() {
            isRunning = true;
        }

        public void stopRunning() {
            isRunning = false;
            synchronized (this) {
                notifyAll();
            }
        }


        public ReaderThread(File file) {

            isRunning = false;
            try {
                try {
                    reader = new BufferedReader(new FileReader(file));
                    if (file.length() >= skip) {
                        reader.skip(skip);
                    }
                } catch (FileNotFoundException filenotfoundexception) {

                    FileUtil.createParentDirs(file);
                    if (!file.createNewFile()) {
                        throw new RuntimeException("could not create file");
                    }

                }

            } catch (Throwable throwable) {
                reader = null;
            }
        }
    }

    private void processLine(String line) {
        if (line == null) {
            return;
        }

        //that check should be more complicated
        Key outputType = ProcessOutputTypes.STDOUT;
        if (line.contains("[emerg]")) {
            outputType = ProcessOutputTypes.STDERR;
        }
        processHandler.notifyTextAvailable((new StringBuilder()).append(line).append("\n").toString(), outputType);

    }
}



