package cn.example.mobilesafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2016/4/12.
 */
public class StreamUtils {

    /**
     * @param is
     * @return String
     * @throws java.io.IOException
     */
    public static String readFromStream(InputStream is) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int len;
        while((len = is.read(b)) != -1){
            out.write(b,0,len);
        }
        String result = out.toString();
        out.close();
        return result;
    }
}
