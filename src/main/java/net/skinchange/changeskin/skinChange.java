package net.skinchange.changeskin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.resource.language.I18n;
import net.skinchange.gui.SkinScreen;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.Scanner;


public abstract class skinChange
{
    public static boolean changeSkin(File fname, String skinType, SkinScreen scr)
    {
        HttpClient httpClient = HttpClientBuilder.create().build();
        try
            {
                if("127.0.0.1".equals(InetAddress.getLocalHost().getHostAddress().toString()))
                {
                    scr.error = I18n.translate("skin.no_internet");
                    return false;
                }
                Scanner scan = new Scanner(new File("config" + File.separator + "skinchange" + File.separator + "data.txt"));
                String username = scan.nextLine();
                String auth = scan.nextLine();

                scan.close();

                //gets uuid
                String a = getHTML("https://api.mojang.com/users/profiles/minecraft/"+username);
                JsonObject json = new JsonParser().parse(a).getAsJsonObject();
                String b = json.get("id").getAsString();

                //uploads skin
                HttpPut http = new HttpPut("https://api.mojang.com/user/profile/"+b+"/skin");

                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.addTextBody("model", skinType.toLowerCase(), ContentType.TEXT_PLAIN);
                builder.addBinaryBody(
                    "file",
                    new FileInputStream(fname),
                    ContentType.APPLICATION_OCTET_STREAM,
                    fname.getName()
                ); 

                http.setEntity(builder.build());
                http.addHeader("Authorization", "Bearer " + auth);
                httpClient.execute(http);

                return true;
            }
            catch(Exception e)
            {
                scr.error = I18n.translate("skin.invalid");
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