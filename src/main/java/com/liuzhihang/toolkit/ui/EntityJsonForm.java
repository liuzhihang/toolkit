package com.liuzhihang.toolkit.ui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import com.liuzhihang.toolkit.utils.FieldsPsiUtils;
import com.liuzhihang.toolkit.utils.GsonFormatUtil;
import com.liuzhihang.toolkit.utils.NotificationUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.Map;

/**
 * @author liuzhihang
 * @date 2021/4/16 12:09
 */
public class EntityJsonForm {

    private final FileType fileType = FileTypeManager.getInstance().getFileTypeByExtension("json");
    private final Document jsonDocument = EditorFactory.getInstance().createDocument("");

    private final Project project;
    private final PsiFile psiFile;
    private final PsiClass psiClass;
    private final JBPopup popup;

    private JPanel rootJPanel;
    private JPanel jsonPane;
    private JPanel tailToolbarPanel;
    private JLabel errorJLabel;

    public EntityJsonForm(@NotNull Project project, PsiFile psiFile, PsiClass psiClass, @NotNull JBPopup popup) {
        this.project = project;
        this.psiFile = psiFile;
        this.psiClass = psiClass;
        this.popup = popup;

        initUI();
        initJsonEditor();
        initTailLeftToolbar();
        initTailRightToolbar();

        initData();
    }

    @NotNull
    public static EntityJsonForm getInstance(@NotNull Project project, PsiFile psiFile, PsiClass psiClass, @NotNull JBPopup popup) {

        return new EntityJsonForm(project, psiFile, psiClass, popup);
    }

    private void initUI() {
        tailToolbarPanel.setBorder(JBUI.Borders.empty());
    }

    private void initJsonEditor() {

        EditorEx editorEx = EditorExUtils.createEditorEx(project, jsonDocument, fileType, true);
        JBScrollPane templateScrollPane = new JBScrollPane(editorEx.getComponent());
        jsonPane.add(templateScrollPane, BorderLayout.CENTER);
    }

    private void initTailLeftToolbar() {

    }

    private void initTailRightToolbar() {

        DefaultActionGroup rightGroup = new DefaultActionGroup();

        rightGroup.add(new AnAction("Copy", "Copy to clipboard", AllIcons.Actions.Copy) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {

                StringSelection selection = new StringSelection(jsonDocument.getText());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);
                popup.cancel();
                NotificationUtils.infoNotify(ToolkitBundle.message("notify.copy.success"), project);
            }
            @Override
            public @NotNull ActionUpdateThread getActionUpdateThread() {
                return ActionUpdateThread.BGT;
            }
        });

        // init toolbar
        ActionToolbarImpl toolbar = (ActionToolbarImpl) ActionManager.getInstance()
                .createActionToolbar("JsonPanelRightToolbar", rightGroup, true);
        toolbar.setTargetComponent(tailToolbarPanel);

        toolbar.setForceMinimumSize(true);
        // toolbar.setLayoutPolicy(ActionToolbar.NOWRAP_LAYOUT_POLICY);
        Utils.setSmallerFontForChildren(toolbar);

        tailToolbarPanel.add(toolbar.getComponent(), BorderLayout.EAST);

    }

    private void initData() {

        try {
            Map<String, Object> fieldsMap = FieldsPsiUtils.getFieldsAndDefaultValue(psiClass, null);

            Gson gson = new GsonBuilder().serializeNulls().create();
            String json = GsonFormatUtil.gsonFormat(gson, fieldsMap);

            WriteCommandAction.runWriteCommandAction(project, () -> jsonDocument.setText(json));

        } catch (Exception ex) {
            WriteCommandAction.runWriteCommandAction(project, () -> jsonDocument.setText("这里不支持实体转 Json"));
        }



    }

    public JPanel getRootJPanel() {
        return rootJPanel;
    }
}
