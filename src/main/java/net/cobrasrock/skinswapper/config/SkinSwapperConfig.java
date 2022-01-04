package net.cobrasrock.skinswapper.config;

public class SkinSwapperConfig extends MidnightConfig {

    @Entry public static ModButton modButton = ModButton.LEFT;
    public enum ModButton {
        LEFT, RIGHT, CENTER
    }

    @Entry public static DisplayType displayType = DisplayType.NEW;
    public enum DisplayType {
        NEW ,LEGACY
    }

    @Entry public static boolean showDownloadScreen = true;

    @Entry public static boolean offlineMode = false;
}
