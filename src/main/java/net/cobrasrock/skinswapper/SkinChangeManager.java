package net.cobrasrock.skinswapper;

import net.cobrasrock.skinswapper.config.SkinSwapperConfig;
import net.cobrasrock.skinswapper.gui.SkinType;
import net.cobrasrock.skinswapper.gui.SkinUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.time.Duration;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class SkinChangeManager {
    public static boolean skinChanged;
    public static String skinType;
    public static Identifier skinId;
    public  static Identifier onlineSkinId;
    public  static String onlineSkinType;
    public  static int count = 0;
    private static boolean wasOfflineMode = SkinSwapperConfig.offlineMode;

    public static void onSkinChange(SkinType type, File skinFile){

        if(type.equals(SkinType.SLIM)){
            skinType = "slim";
        }

        else {
            skinType = "default";
        }

        //registers texture
        skinId = new Identifier("skinswapper_skin" + count);
        count++;
        NativeImage rawNativeImage = SkinUtils.toNativeImage(skinFile);
        NativeImage processedNativeImage = SkinUtils.remapTexture(rawNativeImage);
        NativeImageBackedTexture processedImageBackedTexture = new NativeImageBackedTexture(processedNativeImage);
        MinecraftClient.getInstance().getTextureManager().registerTexture(skinId, processedImageBackedTexture);

        skinChanged = true;

        //if in offline mode, write to file
        if(SkinSwapperConfig.offlineMode){
            //deletes old skin file
            File oldFile;

            if(type == SkinType.CLASSIC) {
                oldFile = new File("config" + File.separator + "skinswapper_lastskin_slim.png");
            }
            else {
                oldFile = new File("config" + File.separator + "skinswapper_lastskin_default.png");
            }

            oldFile.delete();

            //creates new file
            try {
                File dest = new File("config" + File.separator + "skinswapper_lastskin_" + skinType + ".png");
                Files.copy(skinFile.toPath(), dest.toPath(), REPLACE_EXISTING);
            } catch (IOException ignored){}
        } else {
            onlineSkinId = skinId; //updates online skin
            onlineSkinType = skinType;
        }
    }

    //sets skin to match files on launch
    public static void initializeOfflineSkin(){
        File skinFile = new File("config" + File.separator + "skinswapper_lastskin_default.png");
        SkinType skinType = SkinType.CLASSIC;

        //checks if skin is classic or slim
        if (!skinFile.exists()) {
            skinFile = new File("config" + File.separator + "skinswapper_lastskin_slim.png");
            skinType = SkinType.SLIM;
        }

        //no skin file available
        if (!skinFile.exists()) {
            return;
        }

        //schedules skin change
        SkinChangeManager.onSkinChange(skinType, skinFile);
    }

    //determines if the settings have changed
    public static boolean isSettingsChanged(){
        if(wasOfflineMode != SkinSwapperConfig.offlineMode){
            wasOfflineMode = SkinSwapperConfig.offlineMode;
            return true;
        }

        else {
            return false;
        }
    }

    //refreshes skin server side by relogging
    public static void changeOnServer(){
        try {
            if(!MinecraftClient.getInstance().isInSingleplayer() || MinecraftClient.getInstance().world != null) {

                ClientConnection connection = MinecraftClient.getInstance().getNetworkHandler().getConnection();
                InetSocketAddress address = (InetSocketAddress)connection.getAddress();
                String hostname = address.getHostName();
                int port = address.getPort();

                connection = ClientConnection.connect(address, MinecraftClient.getInstance().options.shouldUseNativeTransport());
                connection.setPacketListener(new ClientLoginNetworkHandler(connection, MinecraftClient.getInstance(), MinecraftClient.getInstance().getCurrentServerEntry(), new MultiplayerScreen(new TitleScreen()), false, Duration.ZERO, (Text text) -> {}));
                connection.send(new HandshakeC2SPacket(hostname, port, NetworkState.LOGIN));
                connection.send(new LoginHelloC2SPacket(MinecraftClient.getInstance().getSession().getUsername(), java.util.Optional.ofNullable(MinecraftClient.getInstance().getSession().getUuidOrNull())));
            }
        } catch (Exception ignored){}
    }
}
