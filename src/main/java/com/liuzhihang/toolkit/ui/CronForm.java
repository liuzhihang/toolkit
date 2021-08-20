package com.liuzhihang.toolkit.ui;

import com.cronutils.model.CronType;
import com.intellij.find.editorHeaderActions.Utils;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;
import com.liuzhihang.toolkit.ToolkitBundle;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

import static com.liuzhihang.toolkit.ToolkitBundle.message;

/**
 * @author liuzhihang
 * @date 2021/8/20 17:33
 */
public class CronForm {

    private final Project project;
    private final PsiFile psiFile;
    private final PsiClass psiClass;
    private final JBPopup popup;

    private JPanel rootJPanel;
    private JPanel tailToolbarPanel;
    private JPanel inputPanel;
    private JBTextField inputTextField;
    private JComboBox<String> cronTypeComboBox;

    private JPanel outputPane;
    private JTextField outputTextField;
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
        outputPane.setBorder(IdeBorderFactory.createTitledBorder(ToolkitBundle.message("cron.output.pane.title")));
        tailToolbarPanel.setBorder(JBUI.Borders.empty());
        inputPanel.setBorder(JBUI.Borders.empty(10));
        outputPane.setBorder(JBUI.Borders.empty(10));
    }

    private void initCronTypeComboBox() {
        inputTextField.getEmptyText();
        cronTypeComboBox.addItem(CronType.SPRING.name());
        cronTypeComboBox.addItem(CronType.QUARTZ.name());
        cronTypeComboBox.addItem(CronType.UNIX.name());
        cronTypeComboBox.addItem(CronType.CRON4J.name());

    }

    private void initTailLeftToolbar() {

    }

    private void initTailRightToolbar() {

        DefaultActionGroup rightGroup = new DefaultActionGroup();

        rightGroup.add(new AnAction(message("cron.execute"), "", AllIcons.Debugger.ThreadRunning) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {

                // WriteCommandAction.runWriteCommandAction(project, );
            }
        });


        // init toolbar
        ActionToolbarImpl toolbar = (ActionToolbarImpl) ActionManager.getInstance()
                .createActionToolbar("JsonFormatPanelRightToolbar", rightGroup, true);
        toolbar.setTargetComponent(tailToolbarPanel);

        toolbar.setForceMinimumSize(true);
        toolbar.setLayoutPolicy(ActionToolbar.NOWRAP_LAYOUT_POLICY);
        Utils.setSmallerFontForChildren(toolbar);

        tailToolbarPanel.add(toolbar.getComponent(), BorderLayout.EAST);

    }


    public JPanel getRootJPanel() {
        return rootJPanel;
    }
}
