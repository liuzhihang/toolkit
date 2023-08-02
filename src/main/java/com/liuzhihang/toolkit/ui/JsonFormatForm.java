package com.liuzhihang.toolkit.ui;

import com.google.gson.*;
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
import com.intellij.psi.*;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import com.liuzhihang.toolkit.utils.EditorExUtils;
import com.liuzhihang.toolkit.utils.GsonFormatUtil;
import com.liuzhihang.toolkit.utils.NotificationUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import static com.liuzhihang.toolkit.ToolkitBundle.message;

/**
 * JsonFormat 窗口设置
 *
 * @author liuzhihang
 * @date 2019/5/8 12:40
 */
public class JsonFormatForm {

    private final FileType fileType = FileTypeManager.getInstance().getFileTypeByExtension("json");
    private final Document jsonDocument = EditorFactory.getInstance().createDocument("");

    private final Project project;
    private final PsiFile psiFile;
    private final PsiClass psiClass;
    private final PsiElementFactory psiElementFactory;
    private final PsiType stringPsiType;
    private final PsiType listPsiType;
    private final JBPopup popup;

    private JPanel rootJPanel;
    private JLabel errorJLabel;
    private JPanel jsonFormatPane;
    private JPanel tailToolbarPanel;


    public JsonFormatForm(@NotNull Project project, PsiFile psiFile, PsiClass psiClass, @NotNull JBPopup popup) {
        this.project = project;
        this.psiFile = psiFile;
        this.psiClass = psiClass;
        this.popup = popup;

        // 获取PsiElementFactory来创建Element，包括字段，方法, 注解, 内部类等
        this.psiElementFactory = JavaPsiFacade.getElementFactory(project);
        this.stringPsiType = psiElementFactory.createTypeFromText("java.lang.String", null);
        this.listPsiType = psiElementFactory.createTypeFromText("java.util.List<String>", null);

        initUI();
        initJsonEditor();
        initTailLeftToolbar();
        initTailRightToolbar();

    }

    @NotNull
    public static JsonFormatForm getInstance(@NotNull Project project, PsiFile psiFile, PsiClass psiClass, @NotNull JBPopup popup) {

        return new JsonFormatForm(project, psiFile, psiClass, popup);
    }

    private void initUI() {
        tailToolbarPanel.setBorder(JBUI.Borders.empty());
    }

    private void initJsonEditor() {

        EditorEx editorEx = EditorExUtils.createEditorEx(project, jsonDocument, fileType, false);

        JBScrollPane templateScrollPane = new JBScrollPane(editorEx.getComponent());

        jsonFormatPane.add(templateScrollPane, BorderLayout.CENTER);

    }

    private void initTailLeftToolbar() {

    }

    private void initTailRightToolbar() {

        DefaultActionGroup rightGroup = new DefaultActionGroup();

        rightGroup.add(new AnAction(message("json.format.remove.transfer.text"), "", AllIcons.General.Remove) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                String text = jsonDocument.getText();
                String resultText = text.replace("\\", "");

                WriteCommandAction.runWriteCommandAction(project, () -> jsonDocument.setText(resultText));
            }
        });

        rightGroup.add(new AnAction(message("json.format.compress.text"), "", AllIcons.Actions.Collapseall) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                compressAction();
            }
        });

        rightGroup.add(new AnAction(message("json.format.format.text"), "", AllIcons.Json.Object) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                formatAction();
            }
        });

        rightGroup.addSeparator();

        rightGroup.add(new AnAction(message("json.format.copy.text"), "", AllIcons.Actions.Copy) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {

                StringSelection selection = new StringSelection(jsonDocument.getText());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);
                popup.cancel();
                NotificationUtils.infoNotify(message("notify.copy.success"), project);
            }
        });

        rightGroup.add(new AnAction(message("json.format.next.text"), "", AllIcons.Actions.Rerun) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                preview();
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


    private void compressAction() {

        try {
            String text = jsonDocument.getText().trim();

            if (text.startsWith("{") && text.endsWith("}")) {

                JsonObject jsonObject = JsonParser.parseString(text).getAsJsonObject();
                WriteCommandAction.runWriteCommandAction(project, () -> jsonDocument.setText(jsonObject.toString()));
                errorJLabel.setText("");

            } else if (text.startsWith("[") && text.endsWith("]")) {

                JsonArray jsonArray = JsonParser.parseString(text).getAsJsonArray();
                WriteCommandAction.runWriteCommandAction(project, () -> jsonDocument.setText(jsonArray.toString()));
                errorJLabel.setText("");

            } else {
                notifyErrorJLabel(message("json.format.text.error"));
            }
        } catch (Exception e) {
            notifyErrorJLabel(message("json.format.compress.error"));
        }

    }


    /**
     * 预览 按钮相关操作
     */
    private void preview() {
        String text = jsonDocument.getText().trim();
        if (text.length() == 0) {
            return;
        }
        if (!(psiFile instanceof PsiJavaFile)) {
            notifyErrorJLabel(message("notify.label.use.java.file"));
            return;
        }
        if (psiClass == null) {
            notifyErrorJLabel(message("notify.label.use.class"));
            return;
        }
        if (text.startsWith("[") && text.endsWith("]")) {
            notifyErrorJLabel(message("notify.label.not.support.jsonArray"));
            return;
        }

        if (!text.startsWith("{") || !text.endsWith("}")) {
            notifyErrorJLabel(message("json.format.text.error"));
            return;
        }

        try {
            JsonObject jsonObject = JsonParser.parseString(text).getAsJsonObject();
            ParamPreviewForm.getInstance(project, psiFile, psiClass, jsonObject).popup();
        } catch (Exception e) {
            e.printStackTrace();
            notifyErrorJLabel(message("json.format.text.error"));
        }


    }

    private void notifyErrorJLabel(String s) {
        errorJLabel.setForeground(JBColor.RED);
        errorJLabel.setText(s);
    }

    /**
     * json format 相关操作
     */
    private void formatAction() {
        try {
            String text = jsonDocument.getText().trim();

            Gson gson = new GsonBuilder()
                    .disableHtmlEscaping()
                    .serializeNulls()
                    .setPrettyPrinting()
                    .create();

            if (text.startsWith("{") && text.endsWith("}")) {

                JsonObject jsonObject = JsonParser.parseString(text).getAsJsonObject();
                String writer = GsonFormatUtil.gsonFormat(gson, jsonObject);
                WriteCommandAction.runWriteCommandAction(project, () -> jsonDocument.setText(writer));
                errorJLabel.setText("");

            } else if (text.startsWith("[") && text.endsWith("]")) {

                JsonArray jsonArray = JsonParser.parseString(text).getAsJsonArray();
                String writer = GsonFormatUtil.gsonFormat(gson, jsonArray);
                WriteCommandAction.runWriteCommandAction(project, () -> jsonDocument.setText(writer));
                errorJLabel.setText("");

            } else {

                notifyErrorJLabel(message("json.format.text.error"));
            }
        } catch (Exception e) {
            notifyErrorJLabel("JsonFormat Failed!");
        }
    }

}
