package net.cobrasrock.skinswapper.changeskin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.cobrasrock.skinswapper.gui.SkinScreen;
import net.cobrasrock.skinswapper.gui.SkinType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Contains the methods that interact with the mojang api.
 */
public class SkinChange {
    /**
     * Changes the player's skin.
     * @param fname The name of the file in the "skins" folder.
     * @param skinType The type of skin (classic vs slim).
     * @param scr The current screen.
     * @return Whether or not the skin was changed sucsessfully.
     */
    public static boolean changeSkin(File fname, SkinType skinType, SkinScreen scr) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        try {
            if("127.0.0.1".equals(InetAddress.getLocalHost().getHostAddress())) {
                scr.error = I18n.translate("skin.no_internet");
                return false;
            }

            String auth = MinecraftClient.getInstance().getSession().getAccessToken();

            //uploads skin
            HttpPost http = new HttpPost("https://api.minecraftservices.com/minecraft/profile/skins");

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addTextBody("variant", skinType.getName(), ContentType.TEXT_PLAIN);
            builder.addBinaryBody(
                    "file",
                    new FileInputStream(fname),
                    ContentType.IMAGE_PNG,
                    "skin.png"
            );

            http.setEntity(builder.build());
            http.addHeader("Authorization", "Bearer " + auth);
            HttpResponse response = httpClient.execute(http);

            if(response.getStatusLine().getStatusCode() == 200){
                return true;
            }

            else {
                scr.error = I18n.translate("skin.error") + response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase();
                return false;
            }
        }
        catch(Exception e) {
            scr.error = I18n.translate("skin.invalid");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Downloads a skin to the "skins" folder.
     * @param username The username of the skin.
     */
    public static void downloadSkin(String username) throws Exception {
        //act 1 gets uuid
        String a = SkinChange.getHTML("https://api.mojang.com/users/profiles/minecraft/" + username);
        JsonObject json = new JsonParser().parse(a).getAsJsonObject();
        String b = json.get("id").getAsString();

        //for file name
        String usernameCAPS = json.get("name").getAsString();

        //act 2 gets session texture value
        a = SkinChange.getHTML("https://sessionserver.mojang.com/session/minecraft/profile/" + b);

        json = new JsonParser().parse(a).getAsJsonObject();
        JsonArray c = json.getAsJsonArray("properties");
        for(int i = 0; i<c.size(); i++) {
            JsonObject temp = c.get(i).getAsJsonObject();
            b = temp.get("value").getAsString();
        }

        //act 3 decodes texture
        byte[] decoded = Base64.getDecoder().decode(b);
        b = new String(decoded, StandardCharsets.UTF_8);

        //act 4 gets url from texture
        json = new JsonParser().parse(b).getAsJsonObject();
        b = json.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();

        //act 5 downloads image
        URL url = new URL(b);
        BufferedImage img = ImageIO.read(url);
        File file = new File("skins" + File.separator + usernameCAPS + ".png");
        ImageIO.write(img, "png", file);
    }

    //taken from stack overflow, gets a String of html from a url.
    public static String getHTML(String urlToRead) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlToRead);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        return result.toString();
    }
}