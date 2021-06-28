package info.itsthesky.websky.tools;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import net.md_5.bungee.api.ChatColor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Utils {

    public static String getFileContent(File file) {
        try {
            return Files.toString(file, Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "<none>";
    }

    public static InputStream getStreamFromString(String input) {
        return new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
    }

    public static int counterString(String string, Pattern pattern) {
        Matcher matcher = pattern.matcher(string);
        int count = 0;
        while (matcher.find()) count++;
        return count;
    }

    public Map<String, String> queryToMap(String query) {
        if(query == null) {
            return null;
        }
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            }else{
                result.put(entry[0], "");
            }
        }
        return result;
    }

    public static String join(List<String> list) {
        StringBuilder builder = new StringBuilder();
        for (String l : list) builder.append(l).append("\n");
        return builder.substring(0, builder.length() - 2);
    }

    public static String colored(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

}
