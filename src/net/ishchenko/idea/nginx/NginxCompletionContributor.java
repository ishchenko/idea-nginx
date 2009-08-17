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

package net.ishchenko.idea.nginx;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.codeInsight.lookup.LookupElementRenderer;
import com.intellij.psi.PsiElement;
import net.ishchenko.idea.nginx.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 15.07.2009
 * Time: 0:37:39
 */
public class NginxCompletionContributor extends CompletionContributor {

    private List<NginxLookupElement> allLookupElements = new ArrayList<NginxLookupElement>();
    private Map<String, Set<NginxLookupElement>> contextToDirectiveNameElements = new HashMap<String, Set<NginxLookupElement>>();
    private List<NginxLookupElement> mainContextDirectiveNameElements = new ArrayList<NginxLookupElement>();

    private static List<NginxLookupElement> booleanVariants = new ArrayList<NginxLookupElement>();
    {
        booleanVariants.add(new NginxLookupElement("on"));
        booleanVariants.add(new NginxLookupElement("off"));
    }

    private NginxKeywordsManager keywords;

    public NginxCompletionContributor(NginxKeywordsManager keywords) {

        this.keywords = keywords;

        for (String keyword : keywords.getKeywords()) {
            allLookupElements.add(new NginxLookupElement(keyword));
        }

        Map<String, Set<String>> contextToDirectives = keywords.getContextToDirectiveListMappings();
        for (Map.Entry<String, Set<String>> entry : contextToDirectives.entrySet()) {
            Set<NginxLookupElement> directives = new HashSet<NginxLookupElement>();
            for (String directive : entry.getValue()) {
                directives.add(new NginxLookupElement(directive));
            }
            contextToDirectiveNameElements.put(entry.getKey(), directives);
        }

        for (String directive : keywords.getDirectivesThatCanResideInMainContext()) {
            mainContextDirectiveNameElements.add(new NginxLookupElement(directive));
        }

    }

    @Override
    public void fillCompletionVariants(CompletionParameters parameters, CompletionResultSet result) {

        if (parameters.getOriginalFile() instanceof NginxPsiFile) {

            PsiElement parent = parameters.getPosition().getParent();

            if (parent instanceof NginxDirectiveName && !((NginxDirectiveName) parent).getDirective().isInChaosContext()) {
                suggestName(result, (NginxDirectiveName) parent);

            } else if (parent instanceof NginxDirectiveValue) {

                PsiElement variable = parent.getPrevSibling();
                if (variable != null) {

                    if (variable instanceof NginxInnerVariable) {
                        result = result.withPrefixMatcher(variable.getText().substring(1)); //cutting first $
                    } else if (variable instanceof NginxDirectiveValue) {
                        if (variable.getText().endsWith("$")) {
                            //this is ctrl+space at the end of asdqwe$ (asdqwe$ is treated as simple value)
                            result = result.withPrefixMatcher("");
                        }
                    } else {
                        throw new AssertionError("got some weird type when autocompleting"); //hmm...
                    }
                    suggestVariable(result);

                } else {
                    suggestValue(result, (NginxDirectiveValue) parent);
                }
            }

        }

    }

    private void suggestName(CompletionResultSet result, NginxDirectiveName where) {

        NginxContext context = where.getDirective().getParentContext();
        if (context == null) {
            for (NginxLookupElement mainContextElement : mainContextDirectiveNameElements) {
                result.addElement(mainContextElement);
            }
        } else {
            String contextName = context.getDirective().getNameString();
            Set<NginxLookupElement> elementsForContext = contextToDirectiveNameElements.get(contextName);
            if (elementsForContext == null) {
                //parent directive might be unknown. suggest all.
                for (NginxLookupElement nginxLookupElement : allLookupElements) {
                    result.addElement(nginxLookupElement);
                }
            } else {
                for (NginxLookupElement nginxLookupElement : elementsForContext) {
                    result.addElement(nginxLookupElement);
                }
            }
        }

    }

    private void suggestValue(CompletionResultSet result, NginxDirectiveValue where) {
        if (keywords.checkBooleanKeyword(where.getDirective().getNameString())) {
            for (NginxLookupElement booleanVariant : booleanVariants) {
                result.addElement(booleanVariant);
            }
        }
    }

    private void suggestVariable(CompletionResultSet result) {

        for (String variable : keywords.getVariables()) {
            result.addElement(new NginxLookupElement(variable));
        }

    }

    public static class NginxLookupElement extends LookupElement {

        private String str;

        public NginxLookupElement(String str) {
            this.str = str;
        }

        public InsertHandler<? extends LookupElement> getInsertHandler() {
            return null;
        }

        @NotNull
        public String getLookupString() {
            return str;
        }

        @NotNull
        protected LookupElementRenderer<? extends LookupElement> getRenderer() {
            return new SimpleLookupElementRenderer();
        }

    }

    private static class SimpleLookupElementRenderer extends LookupElementRenderer {
        public void renderElement(LookupElement element, LookupElementPresentation presentation) {
            presentation.setItemText(element.getLookupString());
        }
    }


}
