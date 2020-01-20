package com.liuzhihang.toolkit.model.impl;

import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiType;

/**
 * 表格预览时每行展示的对象
 * <p>
 * 在生成时也需要使用
 *
 * @author liuzhihang
 * @date 2020/1/20 16:50
 */
public class RowBean {

    private Boolean enable;
    /**
     * 修饰符
     */
    private String psiModifier;
    /**
     * 类型
     */
    private PsiType psiType;
    /**
     * 字段名
     */
    private String fieldName;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getPsiModifier() {
        return psiModifier;
    }

    public void setPsiModifier(String psiModifier) {
        this.psiModifier = psiModifier;
    }

    public PsiType getPsiType() {
        return psiType;
    }

    public void setPsiType(PsiType psiType) {
        this.psiType = psiType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
}
