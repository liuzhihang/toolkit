package com.liuzhihang.toolkit.ui;


import com.intellij.psi.PsiElementFactory;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;

import java.util.Arrays;
import java.util.Vector;

/**
 * @author liuzhihang
 * @date 2021/5/28 14:11
 */
public class ParamTreeTableModel extends DefaultTreeTableModel {

    public static final Vector<String> PARAM_TYPE_VECTOR = new Vector<>() {{
        add("String");
        add("Integer");
        add("Long");
        add("Double");
        add("Boolean");
        add("Byte");
        add("Short");
        add("Float");
        add("Number");
        add("Character");
    }};

    private static final String TYPE_PREFIX = "java.lang.";


    private PsiElementFactory psiElementFactory;

    public ParamTreeTableModel(DefaultMutableTreeTableNode node, PsiElementFactory psiElementFactory) {

        super(node, Arrays.asList("Key", "Value", "字段名", "类型"));
        this.psiElementFactory = psiElementFactory;

    }

    @Override
    public Object getValueAt(Object node, int column) {
        if (node instanceof DefaultMutableTreeTableNode) {

            Object userObject = ((DefaultMutableTreeTableNode) node).getUserObject();

            if (userObject instanceof ParamData) {
                ParamData paramData = (ParamData) userObject;
                switch (column) {
                    case 0:
                        return paramData.getKey();
                    case 1:
                        return paramData.getValue();
                    case 2:
                        return paramData.getParamName();
                    case 3:
                        return paramData.getParamType();
                }
            }
        }
        return "";
    }

    @Override
    public void setValueAt(Object value, Object node, int column) {

        super.setValueAt(value, node, column);
        if (node instanceof DefaultMutableTreeTableNode) {

            Object userObject = ((DefaultMutableTreeTableNode) node).getUserObject();
            if (userObject instanceof ParamData) {
                // "Key", "Value", "字段名", "类型"
                ParamData data = (ParamData) userObject;

                if (column == 2) {
                    // 字段名
                    data.setParamName(String.valueOf(value));

                } else if (column == 3) {
                    // 字段类型
                    String paramType = String.valueOf(value);

                    if (PARAM_TYPE_VECTOR.contains(paramType)) {

                        data.setParamType(paramType);
                        data.setParamPsiType(psiElementFactory.createTypeFromText(TYPE_PREFIX + paramType, null));
                    } else {
                        // 类型不对 写会原类型
                        super.setValueAt(data.getParamType(), node, column);
                    }

                }
            }
        }

    }


    @Override
    public boolean isCellEditable(Object node, int column) {

        if (column == 2) {
            return true;
        }
        if (column == 3) {

            if (node instanceof DefaultMutableTreeTableNode) {

                Object userObject = ((DefaultMutableTreeTableNode) node).getUserObject();
                if (userObject instanceof ParamData) {
                    // "Key", "Value", "字段名", "类型"
                    ParamData data = (ParamData) userObject;
                    return PARAM_TYPE_VECTOR.contains(data.getParamType());
                }
            }

            return false;
        }

        return false;

    }


}
