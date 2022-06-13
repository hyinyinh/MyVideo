package com.hy.tiktok.constants;

/**
 * @author hy
 * @version 1.0
 * @Desctiption
 * @date 2022/5/3 10:58
 */
public class VlogConstant {

    public static final String MAPPING_TEMPLATE ="{\n" +
            "  \"mappings\": {\n" +
            "    \"properties\": {\n" +
            "      \"id\":{\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"vlogerName\":{\n" +
            "        \"type\":\"text\",\n" +
            "        \"analyzer\": \"ik_max_word\"\n" +
            "      },\n" +
            "      \"title\":{\n" +
            "       \"type\":\"text\",\n" +
            "        \"analyzer\": \"ik_max_word\"\n" +
            "      },\n" +
            "      \"likeCount\":{\n" +
            "       \"type\":\"integer\"\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";
}
