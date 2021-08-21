package com.liuzhihang.toolkit.ui;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import com.intellij.find.editorHeaderActions.Utils;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;
import com.liuzhihang.toolkit.ToolkitBundle;
import com.liuzhihang.toolkit.utils.EditorExUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

import static com.liuzhihang.toolkit.ToolkitBundle.message;

/**
 * @author liuzhihang
 * @date 2021/8/20 17:33
 */
public class CronForm {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final FileType fileType = FileTypeManager.getInstance().getFileTypeByExtension("text");
    private final Document cornDocument = EditorFactory.getInstance().createDocument("");

    private final Project project;
    private final PsiFile psiFile;
    private final PsiClass psiClass;
    private final JBPopup popup;

    private JPanel rootJPanel;
    private JPanel tailToolbarPanel;
    private JPanel inputPanel;
    private JBTextField inputTextField;
    private JComboBox<CronType> cronTypeComboBox;

    private JPanel outputPane;
    private JLabel errorJLabel;

    public CronForm(@NotNull Project project, PsiFile psiFile, PsiClass psiClass, @NotNull JBPopup popup) {
        this.project = project;
        this.psiFile = psiFile;
        this.psiClass = psiClass;
        this.popup = popup;

        initUI();
        initCronTypeComboBox();
        initTailLeftToolbar();
        initTailRightToolbar();
    }


    @NotNull
    public static CronForm getInstance(@NotNull Project project, PsiFile psiFile, PsiClass psiClass, @NotNull JBPopup popup) {

        return new CronForm(project, psiFile, psiClass, popup);
    }

    private void initUI() {

        tailToolbarPanel.setBorder(JBUI.Borders.empty());
        errorJLabel.setBorder(JBUI.Borders.emptyLeft(5));
        inputPanel.setBorder(JBUI.Borders.empty(10));
        outputPane.setBorder(JBUI.Borders.empty(0, 10));
        EditorEx editorEx = EditorExUtils.createEditorEx(project, cornDocument, fileType, false);

        JComponent component = editorEx.getComponent();
        component.setBorder(IdeBorderFactory.createTitledBorder(ToolkitBundle.message("cron.output.pane.title")));

        outputPane.add(component, BorderLayout.CENTER);
    }

    private void initCronTypeComboBox() {

        inputTextField.getEmptyText();
        cronTypeComboBox.addItem(CronType.SPRING);
        cronTypeComboBox.addItem(CronType.QUARTZ);
        cronTypeComboBox.addItem(CronType.UNIX);
        cronTypeComboBox.addItem(CronType.CRON4J);

    }

    private void initTailLeftToolbar() {

    }

    private void initTailRightToolbar() {

        DefaultActionGroup rightGroup = new DefaultActionGroup();

        rightGroup.add(new AnAction(message("cron.execute"), "", AllIcons.Debugger.ThreadRunning) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {

                String cronText = inputTextField.getText();
                if (StringUtils.isBlank(cronText)) {
                    return;
                }

                CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor((CronType) Objects.requireNonNull(cronTypeComboBox.getSelectedItem()));
                CronParser parser = new CronParser(cronDefinition);

                try {
                    Cron cron = parser.parse(cronText);

                    ExecutionTime executionTime = ExecutionTime.forCron(cron);

                    ZonedDateTime startTime = ZonedDateTime.now();

                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < 10; i++) {
                        Optional<ZonedDateTime> executeTimeOptional = executionTime.nextExecution(startTime);
                        if (executeTimeOptional.isPresent()) {

                            ZonedDateTime executeTime = executeTimeOptional.get();
                            stringBuilder.append(executeTime.format(FORMATTER)).append("\n");
                            startTime = executeTime;
                        }
                    }

                    WriteCommandAction.runWriteCommandAction(project, () -> cornDocument.setText(stringBuilder.toString()));

                } catch (Exception ex) {
                    notifyErrorJLabel(message("cron.text.error"));
                }

            }
        });


        // init toolbar
        ActionToolbarImpl toolbar = (ActionToolbarImpl) ActionManager.getInstance()
                .createActionToolbar("CronPanelRightToolbar", rightGroup, true);
        toolbar.setTargetComponent(tailToolbarPanel);

        toolbar.setForceMinimumSize(true);
        toolbar.setLayoutPolicy(ActionToolbar.NOWRAP_LAYOUT_POLICY);
        Utils.setSmallerFontForChildren(toolbar);

        tailToolbarPanel.add(toolbar.getComponent(), BorderLayout.EAST);

    }



    private void notifyErrorJLabel(String s) {
        errorJLabel.setForeground(JBColor.RED);
        errorJLabel.setText(s);
    }

    public JPanel getRootJPanel() {
        return rootJPanel;
    }
}
