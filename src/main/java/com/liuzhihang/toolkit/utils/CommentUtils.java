package com.liuzhihang.toolkit.utils;

/**
 * @author liuzhihang
 * @date 2019/5/6 16:43
 */
public class CommentUtils {

    /**
     * 移除注释文本中的 / * 空格 换行符
     *
     * @param commentText
     * @return
     */
    public static String removeSymbol(String commentText) {
        return commentText.replace("*", "")
                .replace("/", "")
                .replace(" ", "")
                .replace("\n", "")
                .replace("\t", "");
    }
}
