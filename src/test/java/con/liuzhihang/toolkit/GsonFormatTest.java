package con.liuzhihang.toolkit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.liuzhihang.toolkit.utils.GsonFormatUtil;

import java.io.IOException;

/**
 * @author liuzhihang
 * @date 2021/10/9 16:08
 */
public class GsonFormatTest {


    public static void main(String[] args) throws IOException {

        String text = "{}";

        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

        JsonObject jsonObject = JsonParser.parseString(text).getAsJsonObject();
        String writer = GsonFormatUtil.gsonFormat(gson, jsonObject);

        System.out.println(writer);
    }
}
