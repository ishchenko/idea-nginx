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

package net.ishchenko.idea.nginx.psi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 09.07.2009
 * Time: 21:03:39
 */
public interface NginxDirective extends NginxPsiElement {

    @NotNull
    String getNameString();

    @NotNull
    NginxDirectiveName getDirectiveName();

    @Nullable
    NginxContext getDirectiveContext();

    @Nullable
    NginxContext getParentContext();

    @NotNull
    List<NginxComplexValue> getValues();

    /**
     * @return the directive is inside a context where any directive name is possible (e.g. types, charset_map etc)
     */
    boolean isInChaosContext();

    boolean hasContext();


}