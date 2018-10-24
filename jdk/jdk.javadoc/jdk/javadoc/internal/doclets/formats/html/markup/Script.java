/*
 * Copyright (c) 2003, 2017, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package jdk.javadoc.internal.doclets.formats.html.markup;

import java.io.IOException;
import java.io.Writer;

import jdk.javadoc.internal.doclets.toolkit.Content;
import jdk.javadoc.internal.doclets.toolkit.util.DocletConstants;

/**
 * A builder for HTML script elements.
 *
 *  <p><b>This is NOT part of any supported API.
 *  If you write code that depends on this, you do so at your own risk.
 *  This code and its internal interfaces are subject to change or
 *  deletion without notice.</b>
 */
public class Script  {
    private final StringBuilder sb;

    /**
     * Creates an empty script.
     */
    public Script() {
        sb = new StringBuilder();
    }

    /**
     * Creates a new script containing the specified code.
     *
     * @param code the code
     */
    public Script(String code) {
        this();
        append(code);
    }

    /**
     * Appends the given code to the script.
     *
     * @param code the code
     * @return this object
     */
    public Script append(CharSequence code) {
        sb.append(code);
        return this;
    }

    /**
     * Appends the given text as a string constant to the string.
     * Characters within the string will be escaped as needed.
     *
     * @param text the text
     * @return this object
     */
    public Script appendStringLiteral(CharSequence text) {
        sb.append(stringLiteral(text, '"'));
        return this;
    }

    /**
     * Appends the given text as a string constant to the string.
     * Characters within the string will be escaped as needed.
     *
     * @param text the text
     * @param quoteChar the quote character to use
     * @return this object
     */
    // The ability to specify the quote character is for backwards
    // compatibility. Ideally, we should simplify the code so that
    // the same quote character is always used.
    public Script appendStringLiteral(CharSequence text, char quoteChar) {
        sb.append(stringLiteral(text, quoteChar));
        return this;
    }

    /**
     * Returns a "live" view of the script as a {@code Content} object.
     * Any later modifications to the script will be reflected in the
     * object that is returned.
     * @return the script, as a {@code Content} object.
     */
    public Content asContent() {
        ScriptContent scriptContent = new ScriptContent(sb);
        HtmlTree tree = new HtmlTree(HtmlTag.SCRIPT) {
            @Override
            public void addContent(CharSequence s) {
                throw new UnsupportedOperationException();
            }
            @Override
            public void addContent(Content c) {
                if (c != scriptContent) {
                    throw new IllegalArgumentException();
                }
                super.addContent(scriptContent);
            }
        };
        tree.addAttr(HtmlAttr.TYPE, "text/javascript");
        tree.addContent(scriptContent);
        return tree;
    }

    /**
     * Returns a JavaScript string literal containing a specified string,
     * escaping the characters of that string as needed.
     *
     * @param s the string
     * @return a string literal containing the string
     */
    public static String stringLiteral(CharSequence s) {
        return stringLiteral(s, '"');
    }

    /**
     * Returns a JavaScript string literal containing a specified string,
     * escaping the characters of that string as needed.
     *
     * @param s the string
     * @param quoteChar the quote character to use for the literal
     * @return a string literal containing the string
     */
    // The ability to specify the quote character is for backwards
    // compatibility. Ideally, we should simplify the code so that
    // the same quote character is always used.
    public static String stringLiteral(CharSequence s, char quoteChar) {
        if (quoteChar != '"' && quoteChar != '\'') {
            throw new IllegalArgumentException();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(quoteChar);
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '\b':
                    sb.append("\\b");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '"':
                    sb.append("\\\"");
                    break;
                case '\'':
                    sb.append("\\\'");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                default:
                    if (ch < 32 || ch >= 127) {
                        sb.append(String.format("\\u%04X", (int)ch));
                    } else {
                        sb.append(ch);
                    }
                    break;
            }
        }
        sb.append(quoteChar);
        return sb.toString();
    }

    private static class ScriptContent extends Content {
        private final StringBuilder sb;

        ScriptContent(StringBuilder sb) {
            this.sb = sb;
        }

        @Override
        public void addContent(Content content) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addContent(CharSequence code) {
            sb.append(code);
        }

        @Override
        public boolean write(Writer writer, boolean atNewline) throws IOException {
            String s = sb.toString();
            writer.write(s.replace("\n", DocletConstants.NL));
            return s.endsWith("\n");
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }
}
