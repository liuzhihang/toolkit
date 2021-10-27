package com.liuzhihang.toolkit.ui;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiType;

import java.util.List;

/**
 * @author liuzhihang
 * @date 2021/5/27 16:06
 */
public class ParamData {

    /**
     * 当前字段所属类
     */
    private PsiClass parentPisClass;

    private String key;

    private String value = "";

    private String paramName;

    private String paramType;

    private PsiType paramPsiType;

    /**
     * 注释
     */
    private String desc;

    /**
     * child 不为空时的类型
     */
    private PsiClass paramPsiClass;

    private List<ParamData> child;

    public ParamData(String paramName) {
        this.paramName = paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getParamName() {
        return paramName;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public PsiType getParamPsiType() {
        return paramPsiType;
    }

    public void setParamPsiType(PsiType paramPsiType) {
        this.paramPsiType = paramPsiType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<ParamData> getChild() {
        return child;
    }

    public void setChild(List<ParamData> child) {
        this.child = child;
    }

    public PsiClass getParentPisClass() {
        return parentPisClass;
    }

    public void setParentPisClass(PsiClass parentPisClass) {
        this.parentPisClass = parentPisClass;
    }

    public PsiClass getParamPsiClass() {
        return paramPsiClass;
    }

    public void setParamPsiClass(PsiClass paramPsiClass) {
        this.paramPsiClass = paramPsiClass;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * 重写 equals 字段名相同则为重复
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParamData paramData = (ParamData) o;

        return paramName.equals(paramData.paramName);
    }

    @Override
    public int hashCode() {
        return paramName.hashCode();
    }
}
