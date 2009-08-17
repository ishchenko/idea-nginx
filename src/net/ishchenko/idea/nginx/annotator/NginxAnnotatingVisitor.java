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

package net.ishchenko.idea.nginx.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.util.Range;
import net.ishchenko.idea.nginx.NginxBundle;
import net.ishchenko.idea.nginx.NginxKeywordsManager;
import net.ishchenko.idea.nginx.psi.*;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 14.07.2009
 * Time: 16:04:13
 */

public class NginxAnnotatingVisitor extends NginxElementVisitor implements Annotator {

    private AnnotationHolder holder;

    private NginxKeywordsManager keywords;

    public NginxAnnotatingVisitor(NginxKeywordsManager keywords) {
        this.keywords = keywords;
    }

    public synchronized void annotate(PsiElement psiElement, AnnotationHolder holder) {
        this.holder = holder;
        psiElement.accept(this);
        this.holder = null;
    }

    @Override
    public void visitDirective(NginxDirective node) {

        if (node.isInChaosContext()) {
            return; //directive resides in context like charset_map where almost arbitrary contents are possible
        }
        if (!checkNameIsLegal(node.getDirectiveName())) {
            return; //name is not known - no point for further investigation
        }

        //ok, now we know that directive does exist. let's do some more advanced checks.
        checkParentContext(node);
        checkChildContext(node);
        checkValueCount(node);

    }

    @Override
    public void visitComplexValue(NginxComplexValue node) {

        if (node.getDirective().isInChaosContext()) return;

        String directiveName = node.getDirective().getNameString();
        if (keywords.checkBooleanKeyword(directiveName)) {
            checkBooleanValue(node, directiveName);
        }
    }

    @Override
    public void visitInnerVariable(NginxInnerVariable node) {

        //should I cut $ in NginxInnerVariable itself?
        if (!keywords.getVariables().contains(node.getText().substring(1))) {
            holder.createWarningAnnotation(node, NginxBundle.message("annotator.variable.notexists", node.getText()));
        }

    }

    private void checkValueCount(NginxDirective node) {

        int realRange = node.getValues().size();
        Range<Integer> expectedRange = keywords.getValueRange(node.getNameString());

        if (!expectedRange.isWithin(realRange)) {

            String rangeString;
            if (expectedRange.getFrom().equals(expectedRange.getTo())) {
                rangeString = expectedRange.getFrom().toString();
            } else {
                rangeString = "[" + expectedRange.getFrom() + ", " + expectedRange.getTo() + "]";
            }
            String message = NginxBundle.message("annotator.directive.wrongnumberofvalues", node.getNameString(), rangeString, realRange);

            int i = 0;
            for (NginxComplexValue nginxComplexValue : node.getValues()) {
                if (++i > expectedRange.getTo()) {
                    holder.createErrorAnnotation(nginxComplexValue, message);
                }
            }
            if (i == 0) {
                holder.createErrorAnnotation(node.getDirectiveName(), message);
            }

        }

    }

    private void checkChildContext(NginxDirective node) {
        if (node.hasContext() && !keywords.checkCanHaveChildContext(node.getNameString())) {
            holder.createErrorAnnotation(node, NginxBundle.message("annotator.directive.canthavecontext", node.getNameString()));
        }
    }

    private void checkParentContext(NginxDirective node) {
        NginxContext parentContext = node.getParentContext();
        if (parentContext == null) {
            if (!keywords.checkCanResideInMainContext(node.getNameString())) {
                holder.createWarningAnnotation(node, NginxBundle.message("annotator.directive.cantbeinmain", node.getNameString()));
            }
        } else {
            NginxDirective parent = parentContext.getDirective();
            if (!keywords.checkCanHaveParentContext(node.getNameString(), parent.getNameString())) {
                holder.createWarningAnnotation(node, node.getNameString() + " cant reside in " + parent.getNameString());
            }

        }
    }

    private void checkBooleanValue(NginxComplexValue node, String directiveName) {
        if (node.isFirstValue()) {
            if (!("on".equals(node.getText()) || "off".equals(node.getText()))) {
                holder.createErrorAnnotation(node, NginxBundle.message("annotator.expected.boolean"));
            }
        } else {
            holder.createErrorAnnotation(node, NginxBundle.message("annotator.not.boolean", directiveName));
        }
    }

    private boolean checkNameIsLegal(NginxDirectiveName node) {

        if (keywords.getKeywords().contains(node.getText())) {
            return true;
        } else {
            holder.createWarningAnnotation(node, NginxBundle.message("annotator.directive.unknown", node.getText()));
            return false;
        }

    }

}



