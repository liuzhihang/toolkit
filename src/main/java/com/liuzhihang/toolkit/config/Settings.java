package com.liuzhihang.toolkit.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiModifier;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 配置持久保存
 * <p>
 * <p>
 * See <a href="http://www.jetbrains.org/intellij/sdk/docs/basics/persisting_state_of_components.html">IntelliJ Platform SDK DevGuide</a>
 *
 * @author liuzhihang
 * @date 2020/2/27 19:02
 */
@State(name = "SettingsComponent", storages = {@Storage("liuzhihang-ToolkitSettings.xml")})
public class Settings implements PersistentStateComponent<Settings> {

    /**
     * 注释相关
     */
    private Boolean generateComments = false;

    /**
     * 内部类相关
     */
    private Boolean useInnerClass = true;

    private String innerClassSuffix = "Inner";

    /**
     * 字段相关
     */
    private String fieldModifier = PsiModifier.PRIVATE;
    private Boolean camelcase = false;

    /**
     * 方法相关
     */
    private Boolean generateGetterSetter = false;
    private Boolean generateToString = false;

    /**
     * 注解相关
     */
    private Boolean useLombok = false;
    private Boolean lombokData = false;
    private Boolean lombokGetter = false;
    private Boolean lombokSetter = false;
    private Boolean lombokBuilder = false;


    public static Settings getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, Settings.class);
    }


    @Nullable
    @Override
    public Settings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull Settings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public Boolean getGenerateComments() {
        return generateComments;
    }

    public void setGenerateComments(Boolean generateComments) {
        this.generateComments = generateComments;
    }

    public Boolean getUseInnerClass() {
        return useInnerClass;
    }

    public void setUseInnerClass(Boolean useInnerClass) {
        this.useInnerClass = useInnerClass;
    }

    public String getInnerClassSuffix() {
        return innerClassSuffix;
    }

    public void setInnerClassSuffix(String innerClassSuffix) {
        this.innerClassSuffix = innerClassSuffix;
    }

    public String getFieldModifier() {
        return fieldModifier;
    }

    public void setFieldModifier(String fieldModifier) {
        this.fieldModifier = fieldModifier;
    }

    public Boolean getCamelcase() {
        return camelcase;
    }

    public void setCamelcase(Boolean camelcase) {
        this.camelcase = camelcase;
    }

    public Boolean getGenerateGetterSetter() {
        return generateGetterSetter;
    }

    public void setGenerateGetterSetter(Boolean generateGetterSetter) {
        this.generateGetterSetter = generateGetterSetter;
    }

    public Boolean getGenerateToString() {
        return generateToString;
    }

    public void setGenerateToString(Boolean generateToString) {
        this.generateToString = generateToString;
    }

    public Boolean getUseLombok() {
        return useLombok;
    }

    public void setUseLombok(Boolean useLombok) {
        this.useLombok = useLombok;
    }

    public Boolean getLombokData() {
        return lombokData;
    }

    public void setLombokData(Boolean lombokData) {
        this.lombokData = lombokData;
    }

    public Boolean getLombokGetter() {
        return lombokGetter;
    }

    public void setLombokGetter(Boolean lombokGetter) {
        this.lombokGetter = lombokGetter;
    }

    public Boolean getLombokSetter() {
        return lombokSetter;
    }

    public void setLombokSetter(Boolean lombokSetter) {
        this.lombokSetter = lombokSetter;
    }

    public Boolean getLombokBuilder() {
        return lombokBuilder;
    }

    public void setLombokBuilder(Boolean lombokBuilder) {
        this.lombokBuilder = lombokBuilder;
    }
}
