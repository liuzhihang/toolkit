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
import com.liuzhihang.toolkit.ToolkitBundle;
import com.liuzhihang.toolkit.utils.EditorExUtils;
import com.liuzhihang.toolkit.utils.GsonFormatUtil;
import com.liuzhihang.toolkit.utils.NotificationUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.Objects;

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

        rightGroup.add(new AnAction("Remove Special Chars (\\)", "Remove special chars (\\)", AllIcons.Actions.RemoveMulticaret) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                String text = jsonDocument.getText();
                String resultText = text.replace("\\", "");

                WriteCommandAction.runWriteCommandAction(project, () -> jsonDocument.setText(resultText));
            }
        });

        rightGroup.add(new AnAction("Compress", "Compress one line", AllIcons.Actions.Collapseall) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                compressAction();
            }
        });

        rightGroup.add(new AnAction("Format", "Json format", AllIcons.Json.Object) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                formatAction();
            }
        });

        rightGroup.addSeparator();

        rightGroup.add(new AnAction("Copy", "Copy to clipboard", AllIcons.Actions.Copy) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {

                StringSelection selection = new StringSelection(jsonDocument.getText());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);
                popup.cancel();
                NotificationUtils.infoNotify(ToolkitBundle.message("notify.copy.success"), project);
            }
        });

        rightGroup.add(new AnAction("OK", "Generate entity", AllIcons.General.InspectionsOK) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                generate();
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
                errorJLabel.setForeground(JBColor.RED);
                errorJLabel.setText("Please enter the correct Json string!");
            }
        } catch (Exception e) {
            errorJLabel.setForeground(JBColor.RED);
            errorJLabel.setText("UnFormat Failed!");
        }

    }


    /**
     * next 按钮相关操作
     */
    private void generate() {
        try {
            String text = jsonDocument.getText().trim();

            JsonParser jsonParser = new JsonParser();

            if (psiClass == null) {
                errorJLabel.setForeground(JBColor.RED);
                errorJLabel.setText("Please use in Java objects");
            } else if (text.startsWith("{") && text.endsWith("}")) {
                JsonObject jsonObject = jsonParser.parse(text).getAsJsonObject();
                if (psiFile instanceof PsiJavaFile) {
                    WriteCommandAction.runWriteCommandAction(project, () -> doGenerate(psiClass, jsonObject));
                } else {
                    errorJLabel.setForeground(JBColor.RED);
                    errorJLabel.setText("This is not a Java file");
                }
            } else if (text.startsWith("[") && text.endsWith("]")) {
                errorJLabel.setForeground(JBColor.RED);
                errorJLabel.setText("JsonArray is not supported");
            } else {
                errorJLabel.setForeground(JBColor.RED);
                errorJLabel.setText("Please enter the correct Json string!");
            }
        } catch (Exception e) {
            errorJLabel.setForeground(JBColor.RED);
            errorJLabel.setText("Generate Java field Failed!");
        }
    }

    /**
     * 生成相关字段
     */
    private void doGenerate(PsiClass aClass, JsonObject jsonObject) {

        PsiField[] fields = aClass.getFields();

        for (String key : jsonObject.keySet()) {

            PsiType psiType = null;
            PsiField psiField = null;
            JsonElement jsonElement = jsonObject.get(key);
            if (jsonElement.isJsonPrimitive()) {
                JsonPrimitive asJsonPrimitive = jsonElement.getAsJsonPrimitive();
                String asString = asJsonPrimitive.getAsString();
                if (asString.startsWith("@")) {
                    continue;
                }
                if (asJsonPrimitive.isBoolean()) {
                    psiType = psiElementFactory.createTypeFromText("java.lang.Boolean", null);
                } else if (asJsonPrimitive.isNumber()) {
                    psiType = asString.contains(".") ? psiElementFactory.createTypeFromText("java.lang.Double", null) : psiElementFactory.createTypeFromText("java.lang.Long", null);
                } else {
                    psiType = stringPsiType;
                }
            } else if (jsonElement.isJsonArray()) {
                psiType = listPsiType;
                JsonElement jsonArrayElement = jsonElement.getAsJsonArray().get(0);
                // 判断类型, 赋值构建不同的List
                if (jsonArrayElement.isJsonPrimitive()) {
                    JsonPrimitive primitive = jsonArrayElement.getAsJsonPrimitive();
                    if (primitive.isBoolean()) {
                        psiType = psiElementFactory.createTypeFromText("java.util.List<Boolean>", null);
                    } else if (primitive.isNumber()) {
                        psiType = primitive.getAsString().contains(".")
                                ? psiElementFactory.createTypeFromText("java.util.List<Double>", null)
                                : psiElementFactory.createTypeFromText("java.util.List<Long>", null);
                    } else {
                        psiType = listPsiType;
                    }
                } else if (jsonArrayElement.isJsonObject()) {
                    String className = key.substring(0, 1).toUpperCase() + key.substring(1) + "Inner";

                    PsiClass exist = aClass.findInnerClassByName(className, false);

                    if (exist != null) {
                        exist.delete();
                    }
                    PsiClass innerClass = psiElementFactory.createClass(className);
                    // 创建内部类并添加修饰符
                    PsiModifierList modifierList = innerClass.getModifierList();
                    modifierList.setModifierProperty(PsiModifier.PUBLIC, true);
                    modifierList.setModifierProperty(PsiModifier.STATIC, true);

                    JsonObject jsonObjectElement = jsonArrayElement.getAsJsonObject();
                    doGenerate(innerClass, jsonObjectElement);
                    aClass.addBefore(innerClass, aClass.getRBrace());
                    PsiType typeFromText = psiElementFactory.createTypeFromText("java.util.List<" + className + ">", null);
                    psiField = psiElementFactory.createField(key, typeFromText);
                } else {
                    psiType = listPsiType;
                }

            } else if (jsonElement.isJsonObject()) {
                String className = key.substring(0, 1).toUpperCase() + key.substring(1) + "Inner";

                PsiClass exist = aClass.findInnerClassByName(className, false);

                if (exist != null) {
                    exist.delete();
                }
                PsiClass innerClass = psiElementFactory.createClass(className);
                // 创建内部类并添加修饰符
                PsiModifierList modifierList = innerClass.getModifierList();
                modifierList.setModifierProperty(PsiModifier.PUBLIC, true);
                modifierList.setModifierProperty(PsiModifier.STATIC, true);

                JsonObject jsonObjectElement = jsonElement.getAsJsonObject();
                doGenerate(innerClass, jsonObjectElement);
                aClass.addBefore(innerClass, aClass.getRBrace());
                psiField = psiElementFactory.createField(key, psiElementFactory.createType(innerClass));
            } else {
                psiType = stringPsiType;
            }

            if (psiField == null) {
                psiField = psiElementFactory.createField(key, psiType);
            }
            if (!containFields(fields, psiField)) {
                aClass.add(psiField);
            }

        }
    }

    private boolean containFields(PsiField[] fields, PsiField psiField) {

        for (PsiField field : fields) {
            if (Objects.equals(field.getName(), psiField.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * json format 相关操作
     */
    private void formatAction() {
        try {
            String text = jsonDocument.getText().trim();

            Gson gson = new GsonBuilder().setPrettyPrinting().create();

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

                errorJLabel.setForeground(JBColor.RED);
                errorJLabel.setText("Please enter the correct Json string!");
            }
        } catch (Exception e) {
            errorJLabel.setForeground(JBColor.RED);
            errorJLabel.setText("JsonFormat Failed!");
        }
    }

}
