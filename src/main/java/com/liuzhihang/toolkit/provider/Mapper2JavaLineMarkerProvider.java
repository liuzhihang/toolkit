package com.liuzhihang.toolkit.provider;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.liuzhihang.toolkit.utils.IconUtils;
import com.liuzhihang.toolkit.utils.JavaUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * mapper文件跳转到java文件
 *
 * @author liuzhihang
 * @date 2019/4/30 16:17
 */
public class Mapper2JavaLineMarkerProvider extends RelatedItemLineMarkerProvider {

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo> result) {
        PsiNameIdentifierOwner psiNameIdentifierOwner = JavaUtils.process(element);
        if (psiNameIdentifierOwner != null) {
            NavigationGutterIconBuilder<PsiElement> builder =
                    NavigationGutterIconBuilder.create(IconUtils.NAVIGATE_TO_JAVA)
                            .setAlignment(GutterIconRenderer.Alignment.CENTER)
                            .setTarget(psiNameIdentifierOwner.getNameIdentifier())
                            .setTooltipTitle("Navigation to target in java ");
            result.add(builder.createLineMarkerInfo(element));
        }
    }
}
