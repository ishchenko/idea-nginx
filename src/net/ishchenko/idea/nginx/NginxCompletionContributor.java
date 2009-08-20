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

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupItem;
import com.intellij.psi.PsiElement;
import net.ishchenko.idea.nginx.psi.*;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 15.07.2009
 * Time: 0:37:39
 */
public class NginxCompletionContributor extends CompletionContributor {

    private List<LookupItem> allLookupElements = new ArrayList<LookupItem>();
    private Map<String, Set<LookupItem>> contextToDirectiveNameElements = new HashMap<String, Set<LookupItem>>();
    private List<LookupItem> mainContextDirectiveNameElements = new ArrayList<LookupItem>();

    private static List<LookupItem> booleanVariants = new ArrayList<LookupItem>();
    {
        booleanVariants.add(new LookupItem<String>("on", "on"));
        booleanVariants.add(new LookupItem<String>("off", "off"));
    }

    private NginxKeywordsManager keywords;

    public NginxCompletionContributor(NginxKeywordsManager keywords) {

        this.keywords = keywords;

        for (String keyword : keywords.getKeywords()) {
            allLookupElements.add(new LookupItem<String>(keyword, keyword));
        }

        Map<String, Set<String>> contextToDirectives = keywords.getContextToDirectiveListMappings();
        for (Map.Entry<String, Set<String>> entry : contextToDirectives.entrySet()) {
            Set<LookupItem> directives = new HashSet<LookupItem>();
            for (String directive : entry.getValue()) {
                directives.add(new LookupItem<String>(directive, directive));
            }
            contextToDirectiveNameElements.put(entry.getKey(), directives);
        }

        for (String directive : keywords.getDirectivesThatCanResideInMainContext()) {
            mainContextDirectiveNameElements.add(new LookupItem<String>(directive, directive));
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
            for (LookupItem mainContextElement : mainContextDirectiveNameElements) {
                result.addElement(mainContextElement);
            }
        } else {
            String contextName = context.getDirective().getNameString();
            Set<LookupItem> elementsForContext = contextToDirectiveNameElements.get(contextName);
            if (elementsForContext == null) {
                //parent directive might be unknown. suggest all.
                for (LookupItem LookupItem : allLookupElements) {
                    result.addElement(LookupItem);
                }
            } else {
                for (LookupItem LookupItem : elementsForContext) {
                    result.addElement(LookupItem);
                }
            }
        }

    }

    private void suggestValue(CompletionResultSet result, NginxDirectiveValue where) {
        if (keywords.checkBooleanKeyword(where.getDirective().getNameString())) {
            for (LookupItem booleanVariant : booleanVariants) {
                result.addElement(booleanVariant);
            }
        }
    }

    private void suggestVariable(CompletionResultSet result) {

        for (String variable : keywords.getVariables()) {
            result.addElement(new LookupItem<String>(variable, variable));
        }

    }


}
