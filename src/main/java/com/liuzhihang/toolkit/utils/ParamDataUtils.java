package com.liuzhihang.toolkit.utils;

import com.google.gson.*;
import com.intellij.psi.CommonClassNames;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiType;
import com.liuzhihang.toolkit.config.Settings;
import com.liuzhihang.toolkit.ui.ParamData;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.liuzhihang.toolkit.utils.SettingsHandlerUtils.paramNameHandler;

/**
 * @author liuzhihang
 * @date 2021/5/27 18:06
 */
public class ParamDataUtils {

    public static final String DEFAULT_FIELD = "N/A";

    private static final String CLASS_NAME_LIST_STRING = "java.util.List<String>";
    private static final String CLASS_NAME_LIST_DOUBLE = "java.util.List<Double>";
    private static final String CLASS_NAME_LIST_LONG = "java.util.List<Long>";
    private static final String CLASS_NAME_LIST_BOOLEAN = "java.util.List<Boolean>";

    @NotNull
    public static Set<ParamData> jsonObjectToParamData(@NotNull PsiClass psiClass, @NotNull PsiElementFactory psiElementFactory, @NotNull JsonObject jsonObject) {
        Set<ParamData> paramDataSet = new HashSet<>();


        for (String key : jsonObject.keySet()) {

            if (StringUtils.isBlank(key)) {
                continue;
            }

            JsonElement jsonElement = jsonObject.get(key);

            if (jsonElement.isJsonPrimitive()) {
                paramDataSet.add(jsonPrimitiveToParamData(psiClass, psiElementFactory, key, jsonElement.getAsJsonPrimitive()));
            } else if (jsonElement.isJsonArray()) {
                paramDataSet.add(jsonArrayToParamData(psiClass, psiElementFactory, key, jsonElement.getAsJsonArray()));
            } else if (jsonElement.isJsonObject()) {
                paramDataSet.add(jsonObjectToParamData(psiClass, psiElementFactory, key, jsonElement.getAsJsonObject()));
            } else if (jsonElement.isJsonNull()) {
                paramDataSet.add(jsonNullToParamData(psiClass, psiElementFactory, key, jsonElement.getAsJsonNull()));
            }
        }

        return paramDataSet;
    }

    @NotNull
    private static ParamData jsonPrimitiveToParamData(@NotNull PsiClass psiClass, @NotNull PsiElementFactory psiElementFactory,
                                                      @NotNull String key, @NotNull JsonPrimitive jsonPrimitive) {
        ParamData paramData = new ParamData(paramNameHandler(Settings.getInstance(psiClass.getProject()), key));
        paramData.setKey(key);
        paramData.setParentPisClass(psiClass);

        if (jsonPrimitive.isBoolean()) {

            paramData.setValue(jsonPrimitive.getAsString());
            PsiType type = psiElementFactory.createTypeFromText(CommonClassNames.JAVA_LANG_BOOLEAN, null);
            paramData.setParamType(type.getPresentableText());
            paramData.setParamPsiType(type);

        } else if (jsonPrimitive.isNumber()) {
            paramData.setValue(jsonPrimitive.getAsString());
            PsiType psiType = jsonPrimitive.getAsString().contains(".")
                    ? psiElementFactory.createTypeFromText(CommonClassNames.JAVA_LANG_DOUBLE, null)
                    : psiElementFactory.createTypeFromText(CommonClassNames.JAVA_LANG_LONG, null);
            paramData.setParamType(psiType.getPresentableText());
            paramData.setParamPsiType(psiType);
        } else {
            paramData.setValue(jsonPrimitive.getAsString());
            PsiType type = psiElementFactory.createTypeFromText(CommonClassNames.JAVA_LANG_STRING, null);

            paramData.setParamType(type.getPresentableText());
            paramData.setParamPsiType(type);
        }

        return paramData;
    }


    @NotNull
    private static ParamData jsonArrayToParamData(@NotNull PsiClass psiClass, @NotNull PsiElementFactory psiElementFactory,
                                                  @NotNull String key, @NotNull JsonArray jsonArray) {

        Settings settings = Settings.getInstance(psiClass.getProject());

        ParamData paramData = new ParamData(paramNameHandler(settings, key));
        paramData.setKey(key);
        paramData.setParentPisClass(psiClass);
        paramData.setValue(jsonArray.toString());

        if (jsonArray.size() == 0) {
            // 长度为 0 不能确定格式
            PsiType type = psiElementFactory.createTypeFromText(CommonClassNames.JAVA_UTIL_LIST, null);
            paramData.setParamPsiType(type);
            paramData.setParamType(type.getPresentableText());
            return paramData;
        }

        JsonElement jsonArrayElement = jsonArray.get(0);
        // 判断类型, 赋值构建不同的List
        if (jsonArrayElement.isJsonPrimitive()) {
            JsonPrimitive primitive = jsonArrayElement.getAsJsonPrimitive();
            if (primitive.isBoolean()) {
                PsiType type = psiElementFactory.createTypeFromText(CLASS_NAME_LIST_BOOLEAN, null);
                paramData.setParamType(type.getPresentableText());
                paramData.setParamPsiType(type);

            } else if (primitive.isNumber()) {
                PsiType psiType = primitive.getAsString().contains(".")
                        ? psiElementFactory.createTypeFromText(CLASS_NAME_LIST_DOUBLE, null)
                        : psiElementFactory.createTypeFromText(CLASS_NAME_LIST_LONG, null);

                paramData.setParamType(psiType.getPresentableText());
                paramData.setParamPsiType(psiType);

            } else {
                PsiType type = psiElementFactory.createTypeFromText(CLASS_NAME_LIST_STRING, null);

                paramData.setParamType(type.getPresentableText());
                paramData.setParamPsiType(type);
            }
        } else if (jsonArrayElement.isJsonObject()) {
            String className = key.substring(0, 1).toUpperCase() + key.substring(1) + settings.getInnerClassSuffix();

            PsiType type = psiElementFactory.createTypeFromText("java.util.List<" + className + ">", null);
            paramData.setParamType(type.getPresentableText());
            paramData.setParamPsiType(type);

            // 创建内部类并添加修饰符
            PsiClass innerClass = psiElementFactory.createClass(className);
            Set<ParamData> paramDataSet = jsonObjectToParamData(innerClass, psiElementFactory, jsonArrayElement.getAsJsonObject());

            ArrayList<ParamData> paramDataArrayList = new ArrayList<>(paramDataSet);
            ParamDataUtils.sort(paramDataArrayList);
            paramData.setChild(paramDataArrayList);
            paramData.setParamPsiClass(innerClass);

        } else if (jsonArrayElement.isJsonArray()) {
            // 嵌套还是数组
            PsiType type = psiElementFactory.createTypeFromText("java.util.List<List>", null);
            paramData.setParamType(type.getPresentableText());
            paramData.setParamPsiType(type);

            jsonArrayToParamData(psiClass, psiElementFactory, key, jsonArrayElement.getAsJsonArray());

        } else {
            PsiType type = psiElementFactory.createTypeFromText(CommonClassNames.JAVA_LANG_STRING, null);
            paramData.setParamPsiType(type);
            paramData.setParamType(type.getPresentableText());
        }

        return paramData;
    }


    @NotNull
    private static ParamData jsonObjectToParamData(@NotNull PsiClass psiClass, @NotNull PsiElementFactory psiElementFactory,
                                                   @NotNull String key, @NotNull JsonObject jsonObject) {

        Settings settings = Settings.getInstance(psiClass.getProject());

        ParamData paramData = new ParamData(paramNameHandler(settings, key));
        paramData.setKey(key);
        paramData.setParentPisClass(psiClass);
        paramData.setValue(null);

        String className = key.substring(0, 1).toUpperCase() + key.substring(1) + settings.getInnerClassSuffix();

        PsiType type = psiElementFactory.createTypeFromText(className, null);
        paramData.setParamType(type.getPresentableText());
        paramData.setParamPsiType(type);

        // 创建内部类并添加修饰符
        PsiClass innerClass = psiElementFactory.createClass(className);

        Set<ParamData> paramDataSet = jsonObjectToParamData(innerClass, psiElementFactory, jsonObject);

        ArrayList<ParamData> paramDataArrayList = new ArrayList<>(paramDataSet);
        ParamDataUtils.sort(paramDataArrayList);
        paramData.setChild(paramDataArrayList);
        paramData.setParamPsiClass(innerClass);

        return paramData;
    }


    @NotNull
    private static ParamData jsonNullToParamData(@NotNull PsiClass psiClass, @NotNull PsiElementFactory psiElementFactory,
                                                 @NotNull String key, @NotNull JsonNull jsonNull) {

        ParamData paramData = new ParamData(paramNameHandler(Settings.getInstance(psiClass.getProject()), key));
        paramData.setKey(key);
        paramData.setParentPisClass(psiClass);
        paramData.setValue("");

        PsiType type = psiElementFactory.createTypeFromText(CommonClassNames.JAVA_LANG_STRING, null);

        paramData.setParamPsiType(type);
        paramData.setParamType(type.getPresentableText());

        return paramData;
    }

    public static List<ParamData> sort(List<ParamData> paramDataList) {
        paramDataList.sort((o1, o2) -> {

            if (o1.getKey().equals(DEFAULT_FIELD) && o2.getKey().equals(DEFAULT_FIELD)) {
                return 0;
            }

            if (o1.getKey().equals(DEFAULT_FIELD)) {
                return -1;
            }
            if (o2.getKey().equals(DEFAULT_FIELD)) {
                return 1;
            }

            if (CollectionUtils.isEmpty(o1.getChild()) && CollectionUtils.isNotEmpty(o2.getChild())) {
                return -1;
            }
            if (CollectionUtils.isNotEmpty(o1.getChild()) && CollectionUtils.isEmpty(o2.getChild())) {
                return 1;
            }

            return -o1.getParamName().compareTo(o2.getParamName());

        });

        return paramDataList;
    }


}
