package com.liuzhihang.toolkit.ui;

import com.google.gson.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.*;
import com.intellij.ui.JBColor;
import com.liuzhihang.toolkit.utils.GsonFormatUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

/**
 * JsonFormat 窗口设置
 *
 * @author liuzhihang
 * @date 2019/5/8 12:40
 */
public class JsonFormat extends DialogWrapper {

    private JPanel rootJPanel;
    private JButton formatButton;
    private JTextPane textPane;
    private JButton removeSpecialCharsButton;
    private JLabel errorJLabel;
    private JButton cancelButton;
    private JButton nextButton;
    private JButton compressButton;
    private JLabel fileReference;
    private Project project;
    private PsiFile psiFile;
    private Editor editor;
    private PsiClass psiClass;

    private PsiElementFactory psiElementFactory;
    private PsiType stringPsiType;
    private PsiType listPsiType;

    public JsonFormat(@Nullable Project project, PsiFile psiFile, Editor editor, PsiClass psiClass) {
        super(project, true, IdeModalityType.MODELESS);
        this.project = project;
        this.psiFile = psiFile;
        this.editor = editor;
        this.psiClass = psiClass;

        // 获取PsiElementFactory来创建Element，包括字段，方法, 注解, 内部类等
        this.psiElementFactory = JavaPsiFacade.getElementFactory(project);
        this.stringPsiType = psiElementFactory.createTypeFromText("java.lang.String", null);
        this.listPsiType = psiElementFactory.createTypeFromText("java.util.List<String>", null);
        fileReference.setText(psiClass.getQualifiedName());

        init();
        setTitle("JsonFormat");
        getRootPane().setDefaultButton(nextButton);
        nextButton.setEnabled(true);
        nextButton.setText("OK");
        startListener();
    }

    /**
     * 监听行为
     */
    private void startListener() {

        // 监听formatButton按钮
        formatButton.addActionListener(actionEvent -> formatAction());
        compressButton.addActionListener(actionEvent -> compressAction());
        // 去除转义符号
        removeSpecialCharsButton.addActionListener(actionEvent -> {
            String text = textPane.getText();
            String resultText = text.replace("\\", "");
            textPane.setText(resultText);
        });

        cancelButton.addActionListener(actionEvent -> dispose());

        nextButton.addActionListener(actionEvent -> nextAction());
    }

    private void compressAction() {

        try {
            String text = textPane.getText().trim();

            JsonParser jsonParser = new JsonParser();
            if (text.startsWith("{") && text.endsWith("}")) {

                JsonObject jsonObject = jsonParser.parse(text).getAsJsonObject();
                textPane.setText(jsonObject.toString());
                errorJLabel.setText("");
            } else if (text.startsWith("[") && text.endsWith("]")) {

                JsonArray jsonArray = jsonParser.parse(text).getAsJsonArray();
                textPane.setText(jsonArray.toString());
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
    private void nextAction() {
        try {
            String text = textPane.getText().trim();

            JsonParser jsonParser = new JsonParser();
            if (text.startsWith("{") && text.endsWith("}")) {
                JsonObject jsonObject = jsonParser.parse(text).getAsJsonObject();
                if (psiFile instanceof PsiJavaFile) {
                    // 先不做展示, 直接转化为json
                    // DialogWrapper dialog = new FieldsEditor(project, psiFile, editor, psiClass, jsonObject);
                    // dialog.show();
                    this.dispose();
                    WriteCommandAction.runWriteCommandAction(project, () -> {
                        doGenerate(psiClass, jsonObject);
                    });

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
            errorJLabel.setText("JsonFormat Failed!");
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
                    psiType = PsiType.BOOLEAN;
                } else if (asJsonPrimitive.isNumber()) {
                    psiType = asString.contains(".") ? PsiType.DOUBLE : PsiType.LONG;
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
            String text = textPane.getText().trim();

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonParser jsonParser = new JsonParser();
            if (text.startsWith("{") && text.endsWith("}")) {

                JsonObject jsonObject = jsonParser.parse(text).getAsJsonObject();
                String writer = GsonFormatUtil.gsonFormat(gson, jsonObject);
                textPane.setText(writer);
                errorJLabel.setText("");
            } else if (text.startsWith("[") && text.endsWith("]")) {

                JsonArray jsonArray = jsonParser.parse(text).getAsJsonArray();
                String writer = GsonFormatUtil.gsonFormat(gson, jsonArray);
                textPane.setText(writer);
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


    @NotNull
    @Override
    protected Action[] createActions() {
        // 覆盖默认的 确认和撤销
        return new Action[]{};
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return rootJPanel;
    }
}
