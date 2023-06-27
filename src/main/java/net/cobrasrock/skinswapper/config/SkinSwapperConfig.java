package net.cobrasrock.skinswapper.config;

public class SkinSwapperConfig extends MidnightConfig {

    //General Options
    @Comment public static Comment generalOptions;

    @Entry public static DisplayType displayType = DisplayType.NEW;
    public enum DisplayType {
        NEW ,LEGACY
    }

    @Entry public static boolean forceRelog = false;

    @Entry public static boolean showDownloadScreen = true;

    @Entry public static boolean showArmor = true;

    //Offline Mode
    @Comment public static Comment offlineModeOptions;

    @Entry public static boolean offlineMode = false;

    @Entry public static boolean offlineModeToggle = true;

    //Button Location
    @Comment public static Comment buttonLocationOptions;

    @Entry public static ModButton skinOptionsButton = ModButton.CENTER;
    @Entry public static ModButton multiplayerButton = ModButton.LEFT;
    @Entry public static ModButton singleplayerButton = ModButton.OFF;

    public enum ModButton {
        LEFT, RIGHT, CENTER, OFF
    }

    //changes setting
    public static void toggleOffline(){
        offlineMode = !offlineMode;
        for (EntryInfo info : entries) {
            if (info.field.isAnnotationPresent(Entry.class)) {
                try {
                    info.value = info.field.get(null);
                    info.tempValue = info.value.toString();
                } catch (IllegalAccessException ignored) {
                }
            }
        }
        MidnightConfig.write("skinswapper");
    }
}
