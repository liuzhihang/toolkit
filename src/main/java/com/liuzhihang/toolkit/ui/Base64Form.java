package com.liuzhihang.toolkit.ui;

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
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import com.liuzhihang.toolkit.ToolkitBundle;
import com.liuzhihang.toolkit.utils.EditorExUtils;
import com.liuzhihang.toolkit.utils.NotificationUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author liuzhihang
 * @date 2020/2/18 16:48
 */
public class Base64Form  {

    private final FileType fileType = FileTypeManager.getInstance().getFileTypeByExtension("text");
    private final Document inputDocument = EditorFactory.getInstance().createDocument("");
    private final Document outputDocument = EditorFactory.getInstance().createDocument("");

    private final Project project;
    private final PsiFile psiFile;
    private final PsiClass psiClass;
    private final JBPopup popup;

    private JPanel rootJPanel;
    private JLabel errorJLabel;

    private JPanel tailToolbarPanel;
    private JPanel base64InputPanel;
    private JPanel base64OutputPanel;

    public Base64Form(@NotNull Project project, PsiFile psiFile, PsiClass psiClass, @NotNull JBPopup popup) {
        this.project = project;
        this.psiFile = psiFile;
        this.psiClass = psiClass;
        this.popup = popup;

        initUI();
        initTextEditor();
        initTailLeftToolbar();
        initTailRightToolbar();
    }

    @NotNull
    public static Base64Form getInstance(@NotNull Project project, PsiFile psiFile, PsiClass psiClass, @NotNull JBPopup popup) {

        return new Base64Form(project, psiFile, psiClass, popup);
    }

    private void initUI() {
        tailToolbarPanel.setBorder(JBUI.Borders.empty());
    }

    private void initTextEditor() {

        EditorEx inputEditorEx = EditorExUtils.createEditorEx(project, inputDocument, fileType, false);
        base64InputPanel.add(new JBScrollPane(inputEditorEx.getComponent()), BorderLayout.CENTER);

        EditorEx outputEditorEx = EditorExUtils.createEditorEx(project, outputDocument, fileType, true);
        base64OutputPanel.add(new JBScrollPane(outputEditorEx.getComponent()), BorderLayout.CENTER);

    }

    private void initTailLeftToolbar() {

    }

    private void initTailRightToolbar() {

        DefaultActionGroup rightGroup = new DefaultActionGroup();


        rightGroup.add(new AnAction("Encode", "Encode", AllIcons.Actions.ShowHiddens) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {

                String text = inputDocument.getText().trim();

                String writer = Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));

                WriteCommandAction.runWriteCommandAction(project, () -> outputDocument.setText(writer));
            }
        });

        rightGroup.add(new AnAction("Decoder", "Decoder", AllIcons.Actions.ShowCode) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {

                String text = inputDocument.getText().trim();

                byte[] decode = Base64.getDecoder().decode(text);
                String writer = new String(decode, StandardCharsets.UTF_8);

                WriteCommandAction.runWriteCommandAction(project, () -> outputDocument.setText(writer));
            }
        });

        rightGroup.addSeparator();

        rightGroup.add(new AnAction("Copy", "Copy to clipboard", AllIcons.Actions.Copy) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {

                StringSelection selection = new StringSelection(outputDocument.getText());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);
                popup.cancel();
                NotificationUtils.infoNotify(ToolkitBundle.message("notify.copy.success"), project);
            }
        });

        rightGroup.add(new AnAction("Close", "Generate entity", AllIcons.General.InspectionsOK) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                popup.cancel();
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
