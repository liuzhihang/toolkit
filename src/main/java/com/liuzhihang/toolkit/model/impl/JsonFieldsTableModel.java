package com.liuzhihang.toolkit.model.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiType;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * json format 使用的 FieldsTableModel
 * 用来生成列表
 *
 * @author liuzhihang
 * @date 2019/8/26 5:35 下午
 * @see <a href="https://docs.oracle.com/javase/tutorial/uiswing/components/table.html#data">
 * <p>
 * 来创建一个列表
 */
public class JsonFieldsTableModel extends DefaultTableModel {

    private static final String[] COLUMN_NAME_ARRAY = {"Enable", "Scope", "Type", "Field Name", "Comment"};

    private List<RowBean> rowBeanList;

    private int rowCount;

    public JsonFieldsTableModel(JsonObject jsonObject, Project project) {
        this.rowCount = jsonObject.size();
        // 构建每行展示的展示的对象
        this.rowBeanList = buildRowBeanList(jsonObject, project);
    }

    private List<RowBean> buildRowBeanList(JsonObject jsonObject, Project project) {

        // 获取PsiElementFactory来创建Element，包括字段，方法, 注解, 内部类等
        PsiElementFactory psiElementFactory = JavaPsiFacade.getElementFactory(project);
        // 创建String类型
        PsiType stringPsiType = psiElementFactory.createTypeFromText("java.lang.String", null);

        List<RowBean> rowBeanList = new ArrayList<>();
        for (String key : jsonObject.keySet()) {
            JsonElement jsonElement = jsonObject.get(key);

            RowBean rowBean = new RowBean();
            rowBean.setEnable(true);
            rowBean.setPsiModifier(PsiModifier.PRIVATE);
            if (jsonElement.isJsonPrimitive()) {
                JsonPrimitive asJsonPrimitive = jsonElement.getAsJsonPrimitive();
                String asString = asJsonPrimitive.getAsString();
                if (asString.startsWith("@")) {
                    continue;
                }
                if (asJsonPrimitive.isBoolean()) {
                    rowBean.setPsiType(PsiType.BOOLEAN);
                } else if (asJsonPrimitive.isString()) {
                    rowBean.setPsiType(stringPsiType);
                } else if (asJsonPrimitive.isNumber()) {
                    rowBean.setPsiType(asString.contains(".") ? PsiType.DOUBLE : PsiType.LONG);
                }
            } else {
                // rowBean.typeName = jsonElement.getClass().getSimpleName();
                // rowBean.type = jsonElement.getClass();
            }
            rowBean.setFieldName(key);
            rowBeanList.add(rowBean);
        }
        return rowBeanList;
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAME_ARRAY.length;
    }

    /**
     * 设置表的表头名字
     *
     * @param column
     * @return
     */
    @Override
    public String getColumnName(int column) {
        return COLUMN_NAME_ARRAY[column];
    }

    /**
     * 设置表格的类型
     *
     * @param columnIndex
     * @return
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {

        Class<?> cla;
        if (columnIndex == 0) {
            cla = Boolean.class;
        } else {
            cla = String.class;
        }
        return cla;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        RowBean rowBean = rowBeanList.get(rowIndex);
        switch (columnIndex) {
            case 0:
                rowBean.setEnable((Boolean) aValue);
                break;
            case 1:
                rowBean.setPsiModifier((String) aValue);
                break;
            case 2:
                rowBean.setPsiType((PsiType) aValue);
                break;
            default:
                rowBean.setFieldName((String) aValue);
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        RowBean rowBean = rowBeanList.get(rowIndex);
        Object obj;
        switch (columnIndex) {
            case 0:
                obj = rowBean.getEnable();
                break;
            case 1:
                obj = rowBean.getPsiModifier();
                break;
            case 2:
                obj = rowBean.getPsiType();
                break;
            default:
                obj = rowBean.getFieldName();
        }
        return obj;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }


    public List<RowBean> getRowBeanList() {
        return rowBeanList;
    }
}
