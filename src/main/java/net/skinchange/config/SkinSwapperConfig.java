package net.skinchange.config;

public class SkinSwapperConfig extends MidnightConfig {

    @Entry public static ModButton modButton = ModButton.LEFT;
    public static enum ModButton {
        LEFT, RIGHT, CENTER
    }

    @Entry public static DisplayType displayType = DisplayType.NEW;
    public static enum DisplayType {
        NEW ,LEGACY
    }

    @Entry public static boolean showDownloadScreen = true;
}
