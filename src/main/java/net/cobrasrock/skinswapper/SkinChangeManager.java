package net.cobrasrock.skinswapper;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.cobrasrock.skinswapper.config.SkinSwapperConfig;
import net.cobrasrock.skinswapper.gui.SkinType;
import net.cobrasrock.skinswapper.gui.SkinUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.Map;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class SkinChangeManager {
    public static boolean skinChanged;
    public static String skinType;
    public static Identifier skinId;
    private static boolean wasOfflineMode = SkinSwapperConfig.offlineMode;

    public static void onSkinChange(SkinType type, File skinFile){

        if(type.equals(SkinType.SLIM)){
            skinType = "slim";
        }

        else {
            skinType = "default";
        }

        //registers texture
        skinId = new Identifier("skinswapper_skin");
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
        }
    }

    //sets skin to match files on launch
    public static void initializeSkin(){
        if(SkinSwapperConfig.offlineMode) {
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

        else {
            //loads from mojang
            Runnable runnable = () -> {
                try {
                    GameProfile profile = MinecraftClient.getInstance().getSession().getProfile();
                    profile.getProperties().clear();
                    MinecraftClient.getInstance().getSessionService().fillProfileProperties(profile, true);

                    Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = MinecraftClient.getInstance().getSkinProvider().getTextures(profile);
                    MinecraftProfileTexture profileTexture = map.get(MinecraftProfileTexture.Type.SKIN);

                    skinType = profileTexture.getMetadata("model");
                    if (skinType == null) {
                        skinType = "default";
                    }
                    skinId = MinecraftClient.getInstance().getSkinProvider().loadSkin(profileTexture, MinecraftProfileTexture.Type.SKIN);
                }

                //failed to get skin, loads default
                catch (Exception e) {
                    //todo remove for stable
                    System.out.println("Error Loading Skin:");
                    e.printStackTrace();

                    GameProfile profile = MinecraftClient.getInstance().getSession().getProfile();
                    skinId = DefaultSkinHelper.getTexture(profile.getId());
                    skinType = DefaultSkinHelper.getModel(profile.getId());
                } finally {
                    skinChanged = true;
                }
            };

            Util.getMainWorkerExecutor().execute(runnable);
        }
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

                if(!Compatibility.onOnlineSkinChange(hostname, port)) {
                    connection = ClientConnection.connect(address, MinecraftClient.getInstance().options.shouldUseNativeTransport());
                    connection.setPacketListener(new ClientLoginNetworkHandler(connection, MinecraftClient.getInstance(), new MultiplayerScreen(new TitleScreen()), (Text text) -> {}));
                    connection.send(new HandshakeC2SPacket(hostname, port, NetworkState.LOGIN));
                    connection.send(new LoginHelloC2SPacket(MinecraftClient.getInstance().getSession().getProfile()));
                }
            }
        } catch (Exception ignored){}
    }
}
