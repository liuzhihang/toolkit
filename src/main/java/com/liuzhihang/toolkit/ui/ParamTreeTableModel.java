package com.liuzhihang.toolkit.ui;


import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;

import java.util.Arrays;

/**
 * @author liuzhihang
 * @date 2021/5/28 14:11
 */
public class ParamTreeTableModel extends DefaultTreeTableModel {


    public ParamTreeTableModel(DefaultMutableTreeTableNode node) {

        super(node, Arrays.asList("Key", "Value", "字段名", "类型"));

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
    public void setValueAt(Object o, Object o1, int i) {

    }

    @Override
    public boolean isCellEditable(Object node, int column) {
        return false;
    }


}
