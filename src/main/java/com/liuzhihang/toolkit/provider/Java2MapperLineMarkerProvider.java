package com.liuzhihang.toolkit.provider;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.util.xml.DomElement;
import com.liuzhihang.toolkit.utils.IconUtils;
import com.liuzhihang.toolkit.utils.XmlUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * 行标记和跳转符号
 *
 * @author liuzhihang
 * @date 2019/4/30 15:33
 */
public class Java2MapperLineMarkerProvider extends RelatedItemLineMarkerProvider {

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo> result) {

        DomElement domElement = XmlUtils.process(element);
        if (domElement != null) {
            NavigationGutterIconBuilder<PsiElement> builder =
                    NavigationGutterIconBuilder.create(IconUtils.NAVIGATE_TO_XML)
                            .setAlignment(GutterIconRenderer.Alignment.CENTER)
                            .setTarget(domElement.getXmlTag())
                            .setTooltipTitle("Navigation to target in mapper xml");
            if (element instanceof PsiNameIdentifierOwner) {
                PsiNameIdentifierOwner psiNameIdentifierOwner = (PsiNameIdentifierOwner) element;
                if (psiNameIdentifierOwner.getNameIdentifier() != null) {
                    result.add(builder.createLineMarkerInfo(psiNameIdentifierOwner.getNameIdentifier()));
                }
            }
        }

    }
}
