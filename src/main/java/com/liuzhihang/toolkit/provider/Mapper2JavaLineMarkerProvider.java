package com.liuzhihang.toolkit.provider;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.liuzhihang.toolkit.utils.IconUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Objects;

/**
 * mapper文件跳转到java文件
 *
 * @author liuzhihang
 * @date 2019/4/30 16:17
 */
public class Mapper2JavaLineMarkerProvider extends RelatedItemLineMarkerProvider {

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {


        PsiNameIdentifierOwner psiNameIdentifierOwner = process(element);
        if (psiNameIdentifierOwner != null) {
            NavigationGutterIconBuilder<PsiElement> builder =
                    NavigationGutterIconBuilder.create(IconUtils.NAVIGATE_TO_JAVA)
                            .setAlignment(GutterIconRenderer.Alignment.CENTER)
                            .setTarget(psiNameIdentifierOwner.getNameIdentifier())
                            .setTooltipTitle("Navigation to target in java ");
            result.add(builder.createLineMarkerInfo(element));
        }

    }

    private PsiNameIdentifierOwner process(PsiElement element) {
        if (element instanceof XmlTag) {
            XmlTag xmlTag = (XmlTag) element;
            String xmlTagName = xmlTag.getName();
            if ("mapper".equals(xmlTagName)) {
                XmlAttribute namespace = xmlTag.getAttribute("namespace");
                if (namespace != null && StringUtils.isNotBlank(namespace.getValue())) {
                    Project project = xmlTag.getProject();
                    JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
                    return javaPsiFacade.findClass(namespace.getValue(), GlobalSearchScope.allScope(project));
                }
            } else if ("select".equals(xmlTagName) || "update".equals(xmlTagName) || "delete".equals(xmlTagName) || "insert".equals(xmlTagName)) {
                // 先根据父tag找到对应psiClass 然后再找方法
                XmlTag parentTag = xmlTag.getParentTag();
                XmlAttribute id = xmlTag.getAttribute("id");
                if (parentTag != null && id != null && StringUtils.isNotBlank(id.getValue())) {
                    // 父tag获取namespace
                    XmlAttribute namespace = parentTag.getAttribute("namespace");
                    if (namespace != null && StringUtils.isNotBlank(namespace.getValue())) {
                        Project project = parentTag.getProject();
                        JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
                        PsiClass psiClass = javaPsiFacade.findClass(namespace.getValue(), GlobalSearchScope.allScope(project));
                        if (psiClass != null) {
                            PsiMethod[] methods = psiClass.getMethods();
                            for (PsiMethod method : methods) {
                                if (Objects.equals(method.getName(), id.getValue())) {
                                    return method;
                                }
                            }
                        }
                    }

                }
            }
        }
        return null;
    }
}
