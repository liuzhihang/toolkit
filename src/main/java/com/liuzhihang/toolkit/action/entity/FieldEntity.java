package com.liuzhihang.toolkit.action.entity;

/**
 * 每个字段的实体
 *
 * @author liuzhihang
 * @date 2019/8/27 2:16 下午
 */
public class FieldEntity {

    private Boolean enable;
    private String scope;
    private Class fieldType;
    private String fieldTypeName;
    private String fieldName;
    private String comment;
    private String packageName;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Class getFieldType() {
        return fieldType;
    }

    public void setFieldType(Class fieldType) {
        this.fieldType = fieldType;
    }

    public String getFieldTypeName() {
        return fieldTypeName;
    }

    public void setFieldTypeName(String fieldTypeName) {
        this.fieldTypeName = fieldTypeName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public String toString() {
        return "FieldEntity{" +
                "enable=" + enable +
                ", scope='" + scope + '\'' +
                ", fieldType=" + fieldType +
                ", fieldTypeName='" + fieldTypeName + '\'' +
                ", fieldName='" + fieldName + '\'' +
                ", comment='" + comment + '\'' +
                ", packageName='" + packageName + '\'' +
                '}';
    }
}
