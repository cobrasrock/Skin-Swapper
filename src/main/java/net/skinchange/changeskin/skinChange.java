package net.skinchange.changeskin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.skinchange.gui.SkinScreen;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;

public abstract class skinChange
{
    public static boolean changeSkin(File fname, String skinType, SkinScreen scr)
    {
        HttpClient httpClient = HttpClientBuilder.create().build();
        try
        {
            if("127.0.0.1".equals(InetAddress.getLocalHost().getHostAddress()))
            {
                scr.error = I18n.translate("skin.no_internet");
                return false;
            }

            String auth = MinecraftClient.getInstance().getSession().getAccessToken();

            //uploads skin
            HttpPost http = new HttpPost("https://api.minecraftservices.com/minecraft/profile/skins");

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addTextBody("variant", skinType, ContentType.TEXT_PLAIN);
            builder.addBinaryBody(
                    "file",
                    new FileInputStream(fname),
                    ContentType.IMAGE_PNG,
                    "skin.png"
            );

            http.setEntity(builder.build());
            http.addHeader("Authorization", "Bearer " + auth);
            HttpResponse response = httpClient.execute(http);

            return true;
        }
        catch(Exception e)
        {
            scr.error = I18n.translate("skin.invalid");
            e.printStackTrace();
            return false;
        }
    }

    //taken from stack overflow
    public static String getHTML(String urlToRead) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlToRead);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null)
        {
            result.append(line);
        }
        rd.close();
        return result.toString();
    }
}