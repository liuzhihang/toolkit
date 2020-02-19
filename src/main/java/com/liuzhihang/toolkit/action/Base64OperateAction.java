package com.liuzhihang.toolkit.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.SyntheticElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.liuzhihang.toolkit.ui.Base64Operate;
import com.liuzhihang.toolkit.ui.JsonFormat;
import org.jetbrains.annotations.Nullable;

/**
 * @author liuzhihang
 * @date 2020/2/18 17:16
 */
public class Base64OperateAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {

        // 获取当前project对象
        Project project = event.getData(PlatformDataKeys.PROJECT);
        // 获取当前编辑的文件, 可以进而获取 PsiClass, PsiField 对象
        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
        Editor editor = event.getData(CommonDataKeys.EDITOR);
        // 获取虚拟文件
        // VirtualFile actionFolder = event.getData(LangDataKeys.VIRTUAL_FILE);

        if (editor == null || project == null) {
            return;
        }
        // 获取Java类或者接口
        PsiClass psiClass = getTargetClass(editor, psiFile);

        DialogWrapper dialog = new Base64Operate(project, psiFile, editor, psiClass);
        dialog.show();
    }


    @Nullable
    protected PsiClass getTargetClass(Editor editor, PsiFile file) {
        int offset = editor.getCaretModel().getOffset();
        PsiElement element = file.findElementAt(offset);
        if (element == null) {
            return null;
        } else {
            PsiClass target = PsiTreeUtil.getParentOfType(element, PsiClass.class);
            return target instanceof SyntheticElement ? null : target;
        }
    }
}
