package com.liuzhihang.toolkit.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * java文件工具类
 *
 * @author liuzhihang
 * @date 2019/4/30 15:54
 */
public class JavaUtils {

    public static PsiNameIdentifierOwner process(PsiElement element) {
        if (element instanceof XmlTag) {
            XmlTag xmlTag = (XmlTag) element;
            if ("mapper".equals(xmlTag.getName())) {
                return processMapper(xmlTag);
            } else if (isCRUDStatement(xmlTag.getName())) {
                return processCRUDStatement(xmlTag);
            }
        }
        return null;
    }

    private static boolean isCRUDStatement(String statementName) {

        if ("select".equals(statementName)) {
            return true;
        }
        if ("update".equals(statementName)) {
            return true;
        }
        if ("delete".equals(statementName)) {
            return true;
        }
        if ("insert".equals(statementName)) {
            return true;
        }

        return false;
    }

    private static PsiClass processMapper(XmlTag xmlTag) {
        XmlAttribute namespace = xmlTag.getAttribute("namespace");
        if (namespace != null && StringUtils.isNotBlank(namespace.getValue())) {
            Project project = xmlTag.getProject();
            JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
            return javaPsiFacade.findClass(namespace.getValue(), GlobalSearchScope.allScope(project));
        }
        return null;
    }

    private static PsiMethod processCRUDStatement(XmlTag xmlTag) {
        XmlTag parentTag = xmlTag.getParentTag();
        XmlAttribute id = xmlTag.getAttribute("id");
        if (parentTag != null && id != null && StringUtils.isNotBlank(id.getValue())) {
            PsiClass psiClass = processMapper(parentTag);
            if (psiClass != null) {
                PsiMethod[] methods = psiClass.getMethods();
                for (PsiMethod method : methods) {
                    if (Objects.equals(method.getName(), id.getValue())) {
                        return method;
                    }
                }
            }
        }
        return null;
    }

}
