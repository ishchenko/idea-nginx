package net.ishchenko.idea.nginx.psi.impl;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.util.IncorrectOperationException;
import net.ishchenko.idea.nginx.configurator.NginxServersConfiguration;
import net.ishchenko.idea.nginx.psi.NginxDirectiveValue;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 24.08.2009
 * Time: 14:48:06
 */
public class NginxDirectiveValueManipulator extends AbstractElementManipulator<NginxDirectiveValue> {

    /**
     * Some included file name has been changed. Changing value text and rebuilding configuration file types mapping
     */
    public NginxDirectiveValue handleContentChange(NginxDirectiveValue element, TextRange range, String newContent) throws IncorrectOperationException {

        String oldText = element.getText();
        String newText = oldText.substring(0, range.getStartOffset()) + newContent + oldText.substring(range.getEndOffset());
        Document document = FileDocumentManager.getInstance().getDocument(element.getContainingFile().getVirtualFile());
        document.replaceString(element.getTextRange().getStartOffset(), element.getTextRange().getEndOffset(), newText);
        PsiDocumentManager.getInstance(element.getProject()).commitDocument(document);

        NginxServersConfiguration nginxServersConfiguration = ApplicationManager.getApplication().getComponent(NginxServersConfiguration.class);
        nginxServersConfiguration.rebuildFilepaths();

        return element;

    }
}
