package com.lg.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by XingYun on 2016/5/10.
 */
public class SQLUtil {
    public static final String getQueryFromFile(String filePath) throws IOException {
        FileReader reader = null;
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder("");
        try {
            // read file content from file
            reader = new FileReader(filePath);
            br = new BufferedReader(reader);
            String str;
            while ((str = br.readLine()) != null) {
                String fixedStr = StringUtil.trimFirstAndLastChar(str, '\t');
                sb.append(fixedStr + " \n");
                //System.out.println(fixedStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                br.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
        return sb.toString();
    }

}
