package com.liuzhihang.toolkit.ui;

import com.intellij.find.editorHeaderActions.Utils;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.psi.PsiModifier;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.WindowMoveListener;
import com.intellij.ui.components.labels.LinkLabel;
import com.intellij.util.ui.JBUI;
import com.liuzhihang.toolkit.config.Settings;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.liuzhihang.toolkit.ToolkitBundle.message;

/**
 * @author liuzhihang
 * @date 2021/5/26 14:06
 */
public class SettingsForm extends DialogWrapper {

    @NonNls
    public static final String TOOLKIT_SETTINGS_POPUP = "com.intellij.toolkit.settings.popup";

    private JPanel rootPanel;
    private JScrollPane settingsScrollPane;
    private JPanel settingsPanel;


    private LinkLabel<String> supportLinkLabel;

    private JPanel commentPanel;
    private JCheckBox generateCommentsCheckBox;

    private JPanel entityPanel;
    private JCheckBox useInnerClassCheckBox;
    private JTextField innerClassSuffixTextField;

    private JPanel fieldPanel;
    private JComboBox<String> fieldModifierComboBox;
    private JCheckBox fieldCamelcaseCheckBox;
    private JCheckBox gsonSerializedNameCheckBox;
    private JCheckBox fastJsonJSONFieldCheckBox;

    private JPanel methodPanel;
    private JCheckBox methodGetterSetterCheckBox;
    private JCheckBox methodToStringCheckBox;

    private JPanel annotationPanel;
    private JCheckBox lombokCheckBox;
    private JCheckBox lombokDataCheckBox;
    private JCheckBox lombokGetterCheckBox;
    private JCheckBox lombokSetterCheckBox;
    private JCheckBox lombokBuilderCheckBox;
    private JPanel tailToolbarPanel;


    private final Project project;

    private JBPopup popup;

    private final AtomicBoolean isModified = new AtomicBoolean(false);

    public SettingsForm(Project project) {

        super(project, true, DialogWrapper.IdeModalityType.PROJECT);
        this.project = project;
        init();

        supportLinkLabel.setBorder(JBUI.Borders.emptyTop(20));
        supportLinkLabel.setIcon(AllIcons.Actions.Find);
        supportLinkLabel.setListener((source, data) -> SupportForm.getInstance(project).popup(), null);

        commentPanel.setBorder(IdeBorderFactory.createTitledBorder(message("settings.comment.title")));
        entityPanel.setBorder(IdeBorderFactory.createTitledBorder(message("settings.entity.title")));
        fieldPanel.setBorder(IdeBorderFactory.createTitledBorder(message("settings.field.title")));
        methodPanel.setBorder(IdeBorderFactory.createTitledBorder(message("settings.method.title")));
        annotationPanel.setBorder(IdeBorderFactory.createTitledBorder(message("settings.annotation.title")));

        fieldModifierComboBox.addItem(PsiModifier.PUBLIC);
        fieldModifierComboBox.addItem(PsiModifier.PROTECTED);
        fieldModifierComboBox.addItem(PsiModifier.PRIVATE);

        initUI();
        initTailRightToolbar();
        addMouseListeners();

        initSettings();
        initListener();

        // 暂时关闭一些设置
        methodGetterSetterCheckBox.setEnabled(false);
        methodToStringCheckBox.setEnabled(false);
        useInnerClassCheckBox.setEnabled(false);

        if (fieldCamelcaseCheckBox.isSelected()) {
            gsonSerializedNameCheckBox.setEnabled(true);
            fastJsonJSONFieldCheckBox.setEnabled(true);
        } else {
            gsonSerializedNameCheckBox.setEnabled(false);
            fastJsonJSONFieldCheckBox.setEnabled(false);
        }
    }

    private void initUI() {
        rootPanel.setBorder(JBUI.Borders.empty(10, 10, 0, 10));
        settingsScrollPane.setBorder(JBUI.Borders.empty());
        settingsPanel.setBorder(JBUI.Borders.empty());
        tailToolbarPanel.setBorder(JBUI.Borders.empty());
    }

    private void addMouseListeners() {
        WindowMoveListener windowMoveListener = new WindowMoveListener(rootPanel);
        rootPanel.addMouseListener(windowMoveListener);
        rootPanel.addMouseMotionListener(windowMoveListener);

        settingsPanel.addMouseListener(windowMoveListener);
        settingsPanel.addMouseMotionListener(windowMoveListener);

    }


    @NotNull
    public static SettingsForm getInstance(@NotNull Project project) {

        return new SettingsForm(project);
    }

    public void popup() {
        // dialog 改成 popup, 第一个为根¸面板，第二个为焦点面板
        popup = JBPopupFactory.getInstance().createComponentPopupBuilder(rootPanel, rootPanel)
                .setProject(project)
                .setResizable(true)
                .setMovable(true)

                .setModalContext(false)
                .setRequestFocus(true)
                .setBelongsToGlobalPopupStack(true)
                .setDimensionServiceKey(null, TOOLKIT_SETTINGS_POPUP, true)
                .setLocateWithinScreenBounds(false)
                // 鼠标点击外部时是否取消弹窗 外部单击, 未处于 pin 状态则可关闭
                .setCancelOnMouseOutCallback(event -> event.getID() == MouseEvent.MOUSE_PRESSED)

                // 单击外部时取消弹窗
                .setCancelOnClickOutside(false)
                // 在其他窗口打开时取消
                .setCancelOnOtherWindowOpen(false)
                .setCancelOnWindowDeactivation(false)
                .createPopup();
        popup.showCenteredInCurrentWindow(project);
    }


    private void initTailRightToolbar() {

        DefaultActionGroup rightGroup = new DefaultActionGroup();

        rightGroup.add(new AnAction(message("common.close"), "", AllIcons.Actions.Cancel) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                // 关闭
                popup.cancel();
            }
            @Override
            public @NotNull ActionUpdateThread getActionUpdateThread() {
                return ActionUpdateThread.BGT;
            }
        });

        rightGroup.add(new AnAction(message("common.confirm"), "", AllIcons.Actions.Commit) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                // 确认
                apply();
                popup.cancel();
            }
            @Override
            public @NotNull ActionUpdateThread getActionUpdateThread() {
                return ActionUpdateThread.BGT;
            }
        });

        // init toolbar
        ActionToolbarImpl toolbar = (ActionToolbarImpl) ActionManager.getInstance()
                .createActionToolbar("SettingsPanelRightToolbar", rightGroup, true);
        toolbar.setTargetComponent(tailToolbarPanel);

        toolbar.setForceMinimumSize(true);
//        toolbar.setLayoutPolicy(ActionToolbar.NOWRAP_LAYOUT_POLICY);
        Utils.setSmallerFontForChildren(toolbar);

        tailToolbarPanel.add(toolbar.getComponent(), BorderLayout.EAST);

    }

    private void initSettings() {

        Settings settings = Settings.getInstance(project);

        generateCommentsCheckBox.setSelected(settings.getGenerateComments());
        useInnerClassCheckBox.setSelected(settings.getUseInnerClass());
        innerClassSuffixTextField.setText(settings.getInnerClassSuffix());
        fieldModifierComboBox.setSelectedItem(settings.getFieldModifier());
        fieldCamelcaseCheckBox.setSelected(settings.getCamelcase());
        gsonSerializedNameCheckBox.setSelected(settings.getSerializedName());
        fastJsonJSONFieldCheckBox.setSelected(settings.getJsonField());
        methodGetterSetterCheckBox.setSelected(settings.getGenerateGetterSetter());
        methodToStringCheckBox.setSelected(settings.getGenerateToString());
        lombokCheckBox.setSelected(settings.getUseLombok());
        if (settings.getUseLombok()) {
            lombokDataCheckBox.setSelected(settings.getUseLombok());
            lombokGetterCheckBox.setSelected(settings.getUseLombok());
            lombokSetterCheckBox.setSelected(settings.getUseLombok());
            lombokBuilderCheckBox.setSelected(settings.getUseLombok());
        } else {
            enableAllLombok(false);
        }
    }

    private void initListener() {

        generateCommentsCheckBox.addChangeListener(e -> isModified.set(true));
        useInnerClassCheckBox.addChangeListener(e -> isModified.set(true));
        innerClassSuffixTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                isModified.set(true);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                isModified.set(true);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                isModified.set(true);
            }
        });
        fieldModifierComboBox.addItemListener(e -> isModified.set(true));
        fieldCamelcaseCheckBox.addChangeListener(e -> {
            isModified.set(true);
            JCheckBox box = (JCheckBox) e.getSource();
            gsonSerializedNameCheckBox.setEnabled(box.isSelected());
            fastJsonJSONFieldCheckBox.setEnabled(box.isSelected());

        });
        gsonSerializedNameCheckBox.addChangeListener(e -> isModified.set(true));
        fastJsonJSONFieldCheckBox.addChangeListener(e -> isModified.set(true));
        methodGetterSetterCheckBox.addChangeListener(e -> isModified.set(true));
        methodToStringCheckBox.addChangeListener(e -> isModified.set(true));
        lombokCheckBox.addChangeListener(e -> {
            isModified.set(true);
            JCheckBox box = (JCheckBox) e.getSource();
            enableAllLombok(box.isSelected());

        });

        if (lombokCheckBox.isSelected()) {
            lombokDataCheckBox.addChangeListener(e -> isModified.set(true));
            lombokGetterCheckBox.addChangeListener(e -> isModified.set(true));
            lombokSetterCheckBox.addChangeListener(e -> isModified.set(true));
            lombokBuilderCheckBox.addChangeListener(e -> isModified.set(true));
        }

    }

    private void enableAllLombok(boolean flag) {
        lombokDataCheckBox.setEnabled(flag);
        lombokGetterCheckBox.setEnabled(flag);
        lombokSetterCheckBox.setEnabled(flag);
        lombokBuilderCheckBox.setEnabled(flag);
    }


    public boolean isModified() {

        return isModified.get();
    }

    public void apply() {

        if (!isModified.get()) {
            return;
        }
        Settings settings = Settings.getInstance(project);
        settings.setGenerateComments(generateCommentsCheckBox.isSelected());
        settings.setUseInnerClass(useInnerClassCheckBox.isSelected());
        settings.setInnerClassSuffix(innerClassSuffixTextField.getText().trim());
        settings.setFieldModifier(String.valueOf(fieldModifierComboBox.getSelectedItem()));
        settings.setCamelcase(fieldCamelcaseCheckBox.isSelected());
        settings.setSerializedName(gsonSerializedNameCheckBox.isSelected());
        settings.setJsonField(fastJsonJSONFieldCheckBox.isSelected());
        settings.setGenerateGetterSetter(methodGetterSetterCheckBox.isSelected());
        settings.setGenerateToString(methodToStringCheckBox.isSelected());
        settings.setUseLombok(lombokCheckBox.isSelected());
        settings.setLombokData(lombokDataCheckBox.isSelected());
        settings.setLombokGetter(lombokGetterCheckBox.isSelected());
        settings.setLombokSetter(lombokSetterCheckBox.isSelected());
        settings.setLombokBuilder(lombokBuilderCheckBox.isSelected());

    }


    @Nullable
    @Override
    public JComponent createCenterPanel() {
        return rootPanel;
    }
}
