package com.liuzhihang.toolkit.action;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.lang.jvm.JvmClassKind;
import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.InheritanceUtil;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.psi.util.PsiUtil;
import com.liuzhihang.toolkit.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.math.BigDecimal;
import java.util.List;
import java.util.*;

/**
 * JavaBean 复制为Json字符串
 *
 * @author liuzhihang
 * @date 2019/5/5 13:42
 */
public class CopyAsJsonAction extends AnAction {



    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        Project project = e.getData(PlatformDataKeys.PROJECT);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        Editor editor = e.getData(CommonDataKeys.EDITOR);

        if (editor == null || project == null || psiFile == null) {
            return;
        }

        PsiClass selectedClass = CustomPsiUtils.getTargetClass(editor, psiFile);

        if (selectedClass == null) {
            NotificationUtils.infoNotify("Please use in java class file", project);
            return;
        }

        try {
            Map<String, Object> fieldsMap = FieldsPsiUtils.getFieldsAndDefaultValue(selectedClass, null);

            Gson gson = new GsonBuilder().serializeNulls().create();
            String json = GsonFormatUtil.gsonFormat(gson, fieldsMap);

            // 使用自定义缩进格式 String json = new GsonBuilder().setPrettyPrinting().create().toJson(fieldsMap);
            StringSelection selection = new StringSelection(json);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
            String message = "Convert " + selectedClass.getName() + " to JSON success, copied to clipboard.";
            NotificationUtils.infoNotify(message, project);
        } catch (Exception ex) {
            NotificationUtils.errorNotify("Convert to JSON failed.", project);
        }

    }

    /**
     * 设置右键菜单是否隐藏 Doc View
     *
     * @param e
     */
    @Override
    public void update(@NotNull AnActionEvent e) {

        Project project = e.getData(PlatformDataKeys.PROJECT);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        Editor editor = e.getData(CommonDataKeys.EDITOR);

        Presentation presentation = e.getPresentation();

        if (editor == null || project == null || psiFile == null) {
            presentation.setEnabledAndVisible(false);
            return;
        }

        PsiClass targetClass = CustomPsiUtils.getTargetClass(editor, psiFile);

        if (targetClass == null || targetClass.isAnnotationType() || targetClass.isEnum() || targetClass.isInterface()) {
            presentation.setEnabledAndVisible(false);
        }
    }

    /**
     * 线程类型
     *
     * @return ActionUpdateThread
     */
    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
