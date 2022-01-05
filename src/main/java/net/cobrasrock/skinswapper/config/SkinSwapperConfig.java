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

    @Entry public static boolean offlineModeToggle = false;

    @Entry public static boolean offlineMode = false;

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
