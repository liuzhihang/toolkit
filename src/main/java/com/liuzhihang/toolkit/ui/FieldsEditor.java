package com.liuzhihang.toolkit.ui;

import com.google.gson.JsonObject;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.liuzhihang.toolkit.model.impl.JsonFieldsTableModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.util.List;

/**
 * @author liuzhihang
 * @date 2019/8/22 7:12 下午
 */
public class FieldsEditor extends DialogWrapper {

    private JLabel classFullName;
    private JScrollPane fieldsText;
    private JButton cancelButton;
    private JButton okButton;
    private JPanel rootJPanel;
    private JLabel errorJLabel;
    private JTable fieldsTable;
    private Project project;
    private PsiFile psiFile;
    private Editor editor;
    private JsonObject jsonObject;
    private PsiClass psiClass;
    /**
     * json转换的模型
     */
    private JsonFieldsTableModel jsonFieldsTableModel;

    public FieldsEditor(Project project, PsiFile psiFile, Editor editor, PsiClass psiClass, JsonObject jsonObject) {
        super(project, true, DialogWrapper.IdeModalityType.MODELESS);
        this.project = project;
        this.psiFile = psiFile;
        this.editor = editor;
        this.psiClass = psiClass;
        this.jsonObject = jsonObject;
        this.jsonFieldsTableModel = new JsonFieldsTableModel(jsonObject, project);
        init();
        setTitle("JsonFormat");
        getRootPane().setDefaultButton(okButton);
        startListener();
    }

    private void startListener() {

        classFullNameAction();
        fieldsTableAction();

        cancelButton.addActionListener(actionEvent -> dispose());
        okButton.addActionListener(actionEvent -> okAction());
    }

    /**
     * 生成相关代码
     */
    private void okAction() {

        WriteCommandAction.runWriteCommandAction(project, () -> {


            // 获取PsiElementFactory来创建Element，包括字段，方法, 注解, 内部类等
            PsiElementFactory psiElementFactory = JavaPsiFacade.getElementFactory(project);
            // 创建字段
            PsiType typeFromText = psiElementFactory.createTypeFromText("java.lang.String", null);
            PsiField psiField = psiElementFactory.createField("userName", typeFromText);
            psiClass.add(psiField);
            //
            // psiClass.getModifierList().setModifierProperty(PsiModifier.ABSTRACT, true);
            // JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(psiClass.getProject());
            // styleManager.optimizeImports(psiClass.getContainingFile());
            // styleManager.shortenClassReferences(psiClass.getContainingFile());
            // CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(project);
            // codeStyleManager.reformat(psiClass);

        });

    }

    private void fieldsTableAction() {

        fieldsTable.setModel(jsonFieldsTableModel);
        // 设置背景色
        fieldsTable.setBackground(null);
        fieldsTable.setGridColor(Gray._75);
        fieldsTable.setSelectionBackground(Gray._70);

        // 设置列信息
        TableColumnModel columnModel = fieldsTable.getColumnModel();
        TableColumn column = columnModel.getColumn(0);
        column.setPreferredWidth(25);

        TableColumn column1 = columnModel.getColumn(1);
        ComboBox<String> comboBox1 = new ComboBox<>();
        comboBox1.addItem("private");
        comboBox1.addItem("public");
        comboBox1.addItem("protected");
        comboBox1.addItem("default");
        column1.setCellEditor(new DefaultCellEditor(comboBox1));
    }


    private void classFullNameAction() {

        if (psiFile instanceof PsiJavaFile) {

            PsiJavaFile psiJavaFile = (PsiJavaFile) this.psiFile;

            classFullName.setText(psiJavaFile.getPackageName());
        } else {
            errorJLabel.setForeground(JBColor.RED);
            errorJLabel.setText("This is not a Java file");
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
