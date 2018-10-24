package com.sun.tools.jdeprscan.resources;

public final class jdeprscan_zh_CN extends java.util.ListResourceBundle {
    protected final Object[][] getContents() {
        return new Object[][] {
            { "main.help", "\u626B\u63CF\u6BCF\u4E2A\u53C2\u6570\u4EE5\u4E86\u89E3\u662F\u5426\u4F7F\u7528\u4E86\u8FC7\u65F6\u7684 API\u3002\n\u53C2\u6570\u53EF\u4EE5\u662F\u6307\u5B9A\u7A0B\u5E8F\u5305\u5206\u5C42\u7ED3\u6784, JAR \u6587\u4EF6, \n\u7C7B\u6587\u4EF6\u6216\u7C7B\u540D\u7684\u6839\u7684\u76EE\u5F55\u3002\u7C7B\u540D\u5FC5\u987B\n\u4F7F\u7528\u5168\u9650\u5B9A\u7C7B\u540D\u6307\u5B9A, \u5E76\u4F7F\u7528 $ \u5206\u9694\u7B26\n\u6307\u5B9A\u5D4C\u5957\u7C7B, \u4F8B\u5982,\n\n    java.lang.Thread$State\n\n--class-path \u9009\u9879\u63D0\u4F9B\u4E86\u7528\u4E8E\u89E3\u6790\u4ECE\u5C5E\u7C7B\u7684\n\u641C\u7D22\u8DEF\u5F84\u3002\n\n--for-removal \u9009\u9879\u9650\u5236\u626B\u63CF\u6216\u5217\u51FA\u5DF2\u8FC7\u65F6\u5E76\u5F85\u5220\u9664\n\u7684 API\u3002\u4E0D\u80FD\u4E0E\u53D1\u884C\u7248\u503C 6, 7 \u6216 8 \u4E00\u8D77\u4F7F\u7528\u3002\n\n--full-version \u9009\u9879\u8F93\u51FA\u5DE5\u5177\u7684\u5B8C\u6574\u7248\u672C\u5B57\u7B26\u4E32\u3002\n\n--help \u9009\u9879\u8F93\u51FA\u5B8C\u6574\u7684\u5E2E\u52A9\u6D88\u606F\u3002\n\n--list (-l) \u9009\u9879\u8F93\u51FA\u4E00\u7EC4\u5DF2\u8FC7\u65F6\u7684 API\u3002\u4E0D\u6267\u884C\u626B\u63CF, \n\u56E0\u6B64\u4E0D\u5E94\u63D0\u4F9B\u4EFB\u4F55\u76EE\u5F55, jar \u6216\u7C7B\u53C2\u6570\u3002\n\n--release \u9009\u9879\u6307\u5B9A\u63D0\u4F9B\u8981\u626B\u63CF\u7684\u5DF2\u8FC7\u65F6 API \u96C6\n\u7684 Java SE \u53D1\u884C\u7248\u3002\n\n--verbose (-v) \u9009\u9879\u5728\u5904\u7406\u671F\u95F4\u542F\u7528\u9644\u52A0\u6D88\u606F\u8F93\u51FA\u3002\n\n--version \u9009\u9879\u8F93\u51FA\u5DE5\u5177\u7684\u7F29\u5199\u7248\u672C\u5B57\u7B26\u4E32\u3002" },
            { "main.usage", "\u7528\u6CD5: jdeprscan [\u9009\u9879] '{dir|jar|class}' ...\n\n\u9009\u9879:\n       --class-path PATH\n       --for-removal\n       --full-version\n  -h   --help\n  -l   --list\n       --release 6|7|8|9|10\n  -v   --verbose\n       --version" },
            { "main.xhelp", "\u4E0D\u652F\u6301\u7684\u9009\u9879:\n\n  --Xload-class CLASS\n      \u4ECE\u5DF2\u547D\u540D\u7C7B\u52A0\u8F7D\u8FC7\u65F6\u4FE1\u606F\u3002\n  --Xload-csv CSVFILE\n      \u4ECE\u5DF2\u547D\u540D CSV \u6587\u4EF6\u52A0\u8F7D\u8FC7\u65F6\u4FE1\u606F\u3002\n  --Xload-dir DIR\n      \u4ECE\u5DF2\u547D\u540D\u76EE\u5F55\u4E2D\u7684\u7C7B\u5206\u5C42\u7ED3\u6784\u52A0\u8F7D\n      \u8FC7\u65F6\u4FE1\u606F\u3002\n  --Xload-jar JARFILE\n      \u4ECE\u5DF2\u547D\u540D JAR \u6587\u4EF6\u52A0\u8F7D\u8FC7\u65F6\u4FE1\u606F\u3002\n  --Xload-jdk9 JAVA_HOME\n      \u4ECE\u4F4D\u4E8E JAVA_HOME \u7684 JDK \u4E2D\u52A0\u8F7D\u8FC7\u65F6\u4FE1\u606F, \n      \u8BE5 JDK \u5FC5\u987B\u662F\u4E00\u4E2A\u6A21\u5757\u5316 JDK\u3002\n  --Xload-old-jdk JAVA_HOME\n      \u4ECE\u4F4D\u4E8E JAVA_HOME \u7684 JDK \u4E2D\u52A0\u8F7D\u8FC7\u65F6\u4FE1\u606F, \n      \u8BE5 JDK \u4E0D\u80FD\u662F\u4E00\u4E2A\u6A21\u5757\u5316 JDK\u3002\u76F8\u53CD, \n      \u5DF2\u547D\u540D JDK \u5FC5\u987B\u662F\u5E26\u6709 rt.jar \u6587\u4EF6\u7684 \"\u7ECF\u5178\" JDK\u3002\n  --Xload-self\n      \u901A\u8FC7\u904D\u5386\u6B63\u5728\u8FD0\u884C\u7684 JDK \u6620\u50CF\u7684 jrt: \u6587\u4EF6\u7CFB\u7EDF:\n      \u52A0\u8F7D\u8FC7\u65F6\u4FE1\u606F\u3002\n  --Xcompiler-arg ARG\n      \u5C06 ARG \u6DFB\u52A0\u5230\u7F16\u8BD1\u5668\u53C2\u6570\u5217\u8868\u4E2D\u3002\n  --Xcsv-comment COMMENT\n      \u5C06 COMMENT \u4F5C\u4E3A\u6CE8\u91CA\u884C\u6DFB\u52A0\u5230\u8F93\u51FA CSV \u6587\u4EF6\u3002\n      \u4EC5\u5F53\u540C\u65F6\u63D0\u4F9B\u4E86 -Xprint-csv \u624D\u6709\u6548\u3002\n  --Xhelp\n      \u8F93\u51FA\u6B64\u6D88\u606F\u3002\n  --Xprint-csv\n      \u8F93\u51FA\u5305\u542B\u5DF2\u52A0\u8F7D\u8FC7\u65F6\u4FE1\u606F\u7684 CSV \u6587\u4EF6\n      \u800C\u4E0D\u626B\u63CF\u4EFB\u4F55\u7C7B\u6216 JAR \u6587\u4EF6\u3002" },
            { "scan.dep.normal", "" },
            { "scan.dep.removal", "(forRemoval=true)" },
            { "scan.err.exception", "\u9519\u8BEF: \u51FA\u73B0\u610F\u5916\u7684\u5F02\u5E38\u9519\u8BEF {0}" },
            { "scan.err.noclass", "\u9519\u8BEF: \u627E\u4E0D\u5230\u7C7B {0}" },
            { "scan.err.nofile", "\u9519\u8BEF: \u627E\u4E0D\u5230\u6587\u4EF6 {0}" },
            { "scan.err.nomethod", "\u9519\u8BEF: \u65E0\u6CD5\u89E3\u6790 Methodref {0}.{1}:{2}" },
            { "scan.head.dir", "\u76EE\u5F55 {0}:" },
            { "scan.head.jar", "Jar \u6587\u4EF6 {0}:" },
            { "scan.out.extends", "{0} {1} \u6269\u5C55\u5DF2\u8FC7\u65F6\u7684\u7C7B {2} {3}" },
            { "scan.out.hasfield", "{0} {1} \u5177\u6709\u540D\u4E3A {2} \u7684\u5B57\u6BB5, \u5176\u7C7B\u578B\u4E3A\u5DF2\u8FC7\u65F6\u7684 {3} {4}" },
            { "scan.out.implements", "{0} {1} \u5B9E\u73B0\u5DF2\u8FC7\u65F6\u7684\u63A5\u53E3 {2} {3}" },
            { "scan.out.methodoverride", "{0} {1} \u8986\u76D6\u5DF2\u8FC7\u65F6\u7684\u65B9\u6CD5 {2}::{3}{4} {5}" },
            { "scan.out.methodparmtype", "{0} {1} \u5177\u6709\u540D\u4E3A {2} \u7684\u65B9\u6CD5, \u5176\u53C2\u6570\u7C7B\u578B\u4E3A\u5DF2\u8FC7\u65F6\u7684 {3} {4}" },
            { "scan.out.methodrettype", "{0} {1} \u5177\u6709\u540D\u4E3A {2} \u7684\u65B9\u6CD5, \u5176\u8FD4\u56DE\u7C7B\u578B\u4E3A\u5DF2\u8FC7\u65F6\u7684 {3} {4}" },
            { "scan.out.usesclass", "{0} {1} \u4F7F\u7528\u5DF2\u8FC7\u65F6\u7684\u7C7B {2} {3}" },
            { "scan.out.usesfield", "{0} {1} \u4F7F\u7528\u5DF2\u8FC7\u65F6\u7684\u5B57\u6BB5 {2}::{3} {4}" },
            { "scan.out.usesintfmethod", "{0} {1} \u4F7F\u7528\u5DF2\u8FC7\u65F6\u7684\u65B9\u6CD5 {2}::{3}{4} {5}" },
            { "scan.out.usesmethod", "{0} {1} \u4F7F\u7528\u5DF2\u8FC7\u65F6\u7684\u65B9\u6CD5 {2}::{3}{4} {5}" },
            { "scan.process.class", "\u6B63\u5728\u5904\u7406\u7C7B {0}..." },
        };
    }
}
