package com.liuzhihang.toolkit.constants;

import java.util.*;

/**
 * @author liuzhihang
 * @date 2020/3/4 20:35
 */
public class Constants {

    public static final Map<String, Object> FIELD_TYPE = new HashMap<>(32) {{
        // 包装数据类型
        put("Byte", 0);
        put("Short", 0);
        put("Integer", 0);
        put("Long", 0L);
        put("Float", 0.0F);
        put("Double", 0.0D);
        put("Boolean", false);
        // 其他
        put("String", "");
        put("BigDecimal", null);
        put("Date", null);
        put("LocalDate", null);
        put("LocalTime", null);
        put("LocalDateTime", null);
    }};

    public static final Set<String> ANNOTATION_TYPES = new HashSet<>(16) {{
        add("javax.annotation.Resource");
        add("org.springframework.beans.factory.annotation.Autowired");
    }};

    public static final List<String> KEY_WORD_LIST = new ArrayList<>(100) {{
        add("abstract");
        add("assert");
        add("boolean");
        add("break");
        add("byte");
        add("case");
        add("catch");
        add("char");
        add("class");
        add("const");
        add("continue");
        add("default");
        add("do");
        add("double");
        add("else");
        add("enum");
        add("extends");
        add("final");
        add("finally");
        add("float");
        add("for");
        add("goto");
        add("if");
        add("implements");
        add("import");
        add("instanceof");
        add("int");
        add("interface");
        add("long");
        add("native");
        add("new");
        add("package");
        add("private");
        add("protected");
        add("public");
        add("return");
        add("strictfp");
        add("short");
        add("static");
        add("super");
        add("switch");
        add("synchronized");
        add("this");
        add("throw");
        add("throws");
        add("transient");
        add("try");
        add("abstract");
        add("void");
        add("volatile");
        add("while");
    }};

}
