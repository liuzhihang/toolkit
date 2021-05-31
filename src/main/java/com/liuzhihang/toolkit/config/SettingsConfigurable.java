package com.liuzhihang.toolkit.config;

import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.liuzhihang.toolkit.ui.SettingsForm;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author liuzhihang
 * @date 2020/3/4 13:05
 */
public class SettingsConfigurable implements SearchableConfigurable {

    private SettingsForm settingsForm;

    private Project project;

    public SettingsConfigurable(@NotNull Project project) {
        this.project = project;
    }

    @NotNull
    @Override
    public String getId() {
        return "liuzhihang.toolkit.SettingsConfigurable";
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Toolkit";
    }

    @Nullable
    @Override
    public JComponent createComponent() {

        settingsForm = new SettingsForm(project);

        return settingsForm.createCenterPanel();
    }

    @Override
    public boolean isModified() {

        return settingsForm.isModified();
    }

    @Override
    public void apply() {

        settingsForm.apply();
    }


}
