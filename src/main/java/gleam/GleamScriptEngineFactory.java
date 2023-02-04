/*
 * Copyright (c) 2001-2023 Guglielmo Nigri.  All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of version 2 of the GNU General Public License as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it would be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * Further, this software is distributed without any warranty that it is
 * free of the rightful claim of any third person regarding infringement
 * or the like.  Any license provided herein, whether implied or
 * otherwise, applies only to this software file.  Patent licenses, if
 * any, provided herein do not apply to combinations of this program with
 * other software, or any other product whatsoever.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write the Free Software Foundation, Inc., 59
 * Temple Place - Suite 330, Boston MA 02111-1307, USA.
 *
 * Contact information: Guglielmo Nigri <guglielmonigri@yahoo.it>
 *
 */

package gleam;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GleamScriptEngineFactory implements ScriptEngineFactory
{

    public static final List<String> NAMES = Arrays.asList("gleam",
                                                           "Gleam",
                                                           "GleamScheme",
                                                           "GleamSchemeInterpreter",
                                                           "scheme");

    @Override
    public String getEngineName()
    {
        return "Gleam Scheme Script Engine";
    }

    @Override
    public String getEngineVersion()
    {
        String version = gleam.lang.Interpreter.class.getPackage()
                                                     .getImplementationVersion();
        return version != null ? version : "DEVELOPMENT";
    }

    @Override
    public List<String> getExtensions()
    {
        return Arrays.asList("scm", "glm");
    }

    @Override
    public List<String> getMimeTypes()
    {
        return Collections.emptyList();
    }

    @Override
    public List<String> getNames()
    {
        return NAMES;
    }

    @Override
    public String getLanguageName()
    {
        return "Gleam Scheme";
    }

    @Override
    public String getLanguageVersion()
    {
        return "5.0.0-r5rs";
    }

    @Override
    public Object getParameter(String key)
    {
        switch (key) {
            case ScriptEngine.ENGINE:
                return getEngineName();
            case ScriptEngine.ENGINE_VERSION:
                return getEngineVersion();
            case ScriptEngine.LANGUAGE:
                return getLanguageName();
            case ScriptEngine.LANGUAGE_VERSION:
                return getLanguageVersion();
            case ScriptEngine.NAME:
                return NAMES.get(0);
            case "THREADING":
                return "THREAD-ISOLATED";
        }
        return null;
    }

    @Override
    public String getMethodCallSyntax(String obj, String m, String... args)
    {
        StringBuilder ret = new StringBuilder();
        ret.append("(").append(m).append(" ").append(obj).append(" ");
        for (int i = 0; i < args.length; i++) {
            ret.append(args[i]);
            if (i < args.length - 1) {
                ret.append(" ");
            }
        }
        ret.append(")");
        return ret.toString();
    }

    @Override
    public String getOutputStatement(String toDisplay)
    {
        return "(display " + toDisplay + ")";
    }

    @Override
    public String getProgram(String... statements)
    {
        return String.join("\n", statements);
    }

    @Override
    public ScriptEngine getScriptEngine()
    {
        return new GleamScriptEngine();
    }
}
