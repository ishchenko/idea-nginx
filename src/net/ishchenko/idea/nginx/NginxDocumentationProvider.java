package net.ishchenko.idea.nginx;

import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import net.ishchenko.idea.nginx.psi.NginxDirectiveName;
import net.ishchenko.idea.nginx.psi.NginxInnerVariable;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 19.08.2009
 * Time: 0:49:41
 */
public class NginxDocumentationProvider implements DocumentationProvider {

    public static final Logger LOG = Logger.getInstance("#com.intellij.lang.documentation.QuickDocumentationProvider");

    @Nullable
    @Override
    public List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
        return null;
    }

    @Nullable
    @Override
    public PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object object, PsiElement element) {
        return null;
    }

    @Nullable
    @Override
    public PsiElement getDocumentationElementForLink(PsiManager psiManager, String link, PsiElement context) {
        return null;
    }

    @Override
    public String generateDoc(PsiElement element, PsiElement originalElement) {

        if (element instanceof NginxDirectiveName) {
            return generateDocForDirectiveName((NginxDirectiveName) element);
        } else if (element instanceof NginxInnerVariable) {
            return generateDocForInnerVariable((NginxInnerVariable) element);
        }
        return null;
    }

    private String generateDocForDirectiveName(NginxDirectiveName element) {

        StringBuilder result = new StringBuilder();
        InputStream docStream = getClass().getResourceAsStream("/docs/directives/" + element.getText() + ".html");
        if (docStream == null) {
            result.append(NginxBundle.message("docs.directive.notfound", element.getText()));
        } else {
            BufferedReader keywordsReader = new BufferedReader(new InputStreamReader(docStream));
            try {
                String line;
                while ((line = keywordsReader.readLine()) != null) {
                    result.append(line).append("\n");
                }
            } catch (IOException e) {
                LOG.error(e);
                return null;
            } finally {
                try {
                    keywordsReader.close();
                } catch (IOException e) {
                    LOG.error(e);
                }
            }
        }

        return result.toString();
    }

    private String generateDocForInnerVariable(NginxInnerVariable element) {

        StringBuilder result = new StringBuilder();
        InputStream docStream = getClass().getResourceAsStream("/docs/variables/" + element.getName() + ".html");
        if (docStream == null) {
            result.append(NginxBundle.message("docs.variable.notfound", element.getName()));
        } else {
            result.append("<b>").append(element.getText()).append("</b><br>");
            BufferedReader keywordsReader = new BufferedReader(new InputStreamReader(docStream));
            try {
                String line;
                while ((line = keywordsReader.readLine()) != null) {
                    result.append(line).append("\n");
                }
            } catch (IOException e) {
                LOG.error(e);
                return null;
            } finally {
                try {
                    keywordsReader.close();
                } catch (IOException e) {
                    LOG.error(e);
                }
            }
        }

        return result.toString();

    }

    @Override
    public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        return null;
    }
}
