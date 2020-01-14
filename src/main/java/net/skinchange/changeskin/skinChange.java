package net.skinchange.changeskin;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.HttpResponse;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.net.InetAddress;

import net.skinchange.gui.AccountScreen;
import net.skinchange.gui.SkinScreen;


public abstract class skinChange
{
    public static boolean authenticate(String username, String password, AccountScreen scr)
    {
        HttpClient httpClient = HttpClientBuilder.create().build();

        try
        {
            if("127.0.0.1".equals(InetAddress.getLocalHost().getHostAddress().toString()))
            {
                scr.error = "No Internet Connection";
                return false;
            }
            HttpPost http = new HttpPost("https://authserver.mojang.com/authenticate");
            String b = "{\"agent\" : {\"name\":\"Minecraft\",\"version\":1}, \"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
            StringEntity payload = new StringEntity(b);
            
            http.setEntity(payload);
            http.addHeader("Content-Type", "application/json");
            HttpResponse response = httpClient.execute(http);
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity);

            try
            {
                JsonObject json = new JsonParser().parse(content).getAsJsonObject();
                String authtoken = json.get("accessToken").getAsString();
                String UsersName = json.get("selectedProfile").getAsJsonObject().get("name").getAsString();

                //writes to file
                PrintWriter writer = new PrintWriter("config\\skinchange\\data.txt", "UTF-8");
                writer.println(UsersName);
                writer.println(authtoken);
                writer.close();
                return true;
            }
            catch(Exception e)
            {
                scr.error = "Invalid Username or Password";
                return false;
            }

        }
        catch(Exception e)
        {
            return false;
        }
    }
    
    public static boolean changeSkin(File fname, String skinType, SkinScreen scr)
    {
        HttpClient httpClient = HttpClientBuilder.create().build();
        try
            {
                if("127.0.0.1".equals(InetAddress.getLocalHost().getHostAddress().toString()))
                {
                    scr.error = "No Internet Connection";
                    return false;
                }
                Scanner scan = new Scanner(new File("config\\skinchange\\data.txt"));
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
                scr.error = "Invalid Username or Password";
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