package net.cobrasrock.skinswapper.config;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/** MidnightConfig v2.0.0 by TeamMidnightDust & Motschen
 *  Single class config library - feel free to copy!
 *
 *  Based on https://github.com/Minenash/TinyConfig
 *  Credits to Minenash */

@SuppressWarnings("unchecked")
public abstract class MidnightConfig {
    private static final Pattern INTEGER_ONLY = Pattern.compile("(-?[0-9]*)");
    private static final Pattern DECIMAL_ONLY = Pattern.compile("-?([\\d]+\\.?[\\d]*|[\\d]*\\.?[\\d]+|\\.)");

    public static final List<EntryInfo> entries = new ArrayList<>();

    protected static class EntryInfo {
        Field field;
        Object widget;
        int width;
        int max;
        Map.Entry<TextFieldWidget,Text> error;
        Object defaultValue;
        Object value;
        String tempValue;
        boolean inLimits = true;
        String id;
        Text name;
        int index;
    }

    public static final Map<String,Class<?>> configClass = new HashMap<>();
    private static Path path;

    private static final Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT).excludeFieldsWithModifiers(Modifier.PRIVATE).addSerializationExclusionStrategy(new HiddenAnnotationExclusionStrategy()).setPrettyPrinting().create();

    public static void init(String modid, Class<?> config) {
        path = new File("config" + File.separator + modid + ".json").toPath();
        configClass.put(modid, config);

        for (Field field : config.getFields()) {
            EntryInfo info = new EntryInfo();
            if (field.isAnnotationPresent(Entry.class) || field.isAnnotationPresent(Comment.class))
                initClient(modid, field, info);
            if (field.isAnnotationPresent(Entry.class))
                try {
                    info.defaultValue = field.get(null);
                } catch (IllegalAccessException ignored) {}
        }
        try { gson.fromJson(Files.newBufferedReader(path), config); }
        catch (Exception e) { write(modid); }

        for (EntryInfo info : entries) {
            if (info.field.isAnnotationPresent(Entry.class))
                try {
                    info.value = info.field.get(null);
                    info.tempValue = info.value.toString();
                } catch (IllegalAccessException ignored) {
                }
        }
    }

    private static void initClient(String modid, Field field, EntryInfo info) {
        Class<?> type = field.getType();
        Entry e = field.getAnnotation(Entry.class);
        info.width = e != null ? e.width() : 0;
        info.field = field;
        info.id = modid;

        if (e != null) {
            if (!e.name().equals("")) info.name = Text.translatable(e.name());
            if (type == int.class) textField(info, Integer::parseInt, INTEGER_ONLY, e.min(), e.max(), true);
            else if (type == double.class) textField(info, Double::parseDouble, DECIMAL_ONLY, e.min(), e.max(), false);
            else if (type == String.class || type == List.class) {
                info.max = e.max() == Double.MAX_VALUE ? Integer.MAX_VALUE : (int) e.max();
                textField(info, String::length, null, Math.min(e.min(), 0), Math.max(e.max(), 1), true);
            } else if (type == boolean.class) {
                Function<Object, Text> func = value -> Text.of((Boolean) value ? "True" : "False").copy().formatted((Boolean) value ? Formatting.GREEN : Formatting.RED);
                info.widget = new AbstractMap.SimpleEntry<ButtonWidget.PressAction, Function<Object, Text>>(button -> {
                    info.value = !(Boolean) info.value;
                    button.setMessage(func.apply(info.value));
                }, func);
            } else if (type.isEnum()) {
                List<?> values = Arrays.asList(field.getType().getEnumConstants());
                Function<Object, Text> func = value -> Text.translatable(modid + ".midnightconfig." + "enum." + type.getSimpleName() + "." + info.value.toString());
                info.widget = new AbstractMap.SimpleEntry<ButtonWidget.PressAction, Function<Object, Text>>(button -> {
                    int index = values.indexOf(info.value) + 1;
                    info.value = values.get(index >= values.size() ? 0 : index);
                    button.setMessage(func.apply(info.value));
                }, func);
            }
        }
        entries.add(info);
    }

    private static void textField(EntryInfo info, Function<String,Number> f, Pattern pattern, double min, double max, boolean cast) {
        boolean isNumber = pattern != null;
        info.widget = (BiFunction<TextFieldWidget, ButtonWidget, Predicate<String>>) (t, b) -> s -> {
            s = s.trim();
            if (!(s.isEmpty() || !isNumber || pattern.matcher(s).matches())) return false;

            Number value = 0;
            boolean inLimits = false;
            info.error = null;
            if (!(isNumber && s.isEmpty()) && !s.equals("-") && !s.equals(".")) {
                value = f.apply(s);
                inLimits = value.doubleValue() >= min && value.doubleValue() <= max;
                info.error = inLimits? null : new AbstractMap.SimpleEntry<>(t, Text.of(value.doubleValue() < min ?
                        "§cMinimum " + (isNumber? "value" : "length") + (cast? " is " + (int)min : " is " + min) :
                        "§cMaximum " + (isNumber? "value" : "length") + (cast? " is " + (int)max : " is " + max)));
            }

            info.tempValue = s;
            t.setEditableColor(inLimits? 0xFFFFFFFF : 0xFFFF7777);
            info.inLimits = inLimits;
            b.active = entries.stream().allMatch(e -> e.inLimits);

            if (inLimits && info.field.getType() != List.class)
                info.value = isNumber? value : s;
            else if (inLimits) {
                if (((List<String>) info.value).size() == info.index) ((List<String>) info.value).add("");
                ((List<String>) info.value).set(info.index, Arrays.stream(info.tempValue.replace("[", "").replace("]", "").split(", ")).toList().get(0));
            }

            return true;
        };
    }

    public static void write(String modid) {
        path = new File("config" + File.separator + modid + ".json").toPath();
        try {
            if (!Files.exists(path)) Files.createFile(path);
            Files.write(path, gson.toJson(configClass.get(modid).getDeclaredConstructor().newInstance()).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Screen getScreen(Screen parent, String modid) {
        return new MidnightConfigScreen(parent, modid);
    }

    private static class MidnightConfigScreen extends Screen {
        protected MidnightConfigScreen(Screen parent, String modid) {
            super(Text.translatable(modid + ".midnightconfig." + "title"));
            this.parent = parent;
            this.modid = modid;
            this.translationPrefix = modid + ".midnightconfig.";
        }
        private final String translationPrefix;
        private final Screen parent;
        private final String modid;
        private MidnightConfigListWidget list;
        private boolean reload = false;

        // Real Time config update //
        @Override
        public void tick() {
            super.tick();
            for (EntryInfo info : entries) {
                try {info.field.set(null, info.value);} catch (IllegalAccessException ignored) {}
            }
        }
        private void loadValues() {
            try { gson.fromJson(Files.newBufferedReader(path), configClass.get(modid)); }
            catch (Exception e) { write(modid); }

            for (EntryInfo info : entries) {
                if (info.field.isAnnotationPresent(Entry.class))
                    try {
                        info.value = info.field.get(null);
                        info.tempValue = info.value.toString();
                    } catch (IllegalAccessException ignored) {}
            }
        }
        @Override
        protected void init() {
            super.init();
            if (!reload) loadValues();

            this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, button -> {
                        loadValues();
                        Objects.requireNonNull(client).setScreen(parent);
                    })
                    .dimensions(this.width / 2 - 154, this.height - 28, 150, 20)
                    .build());

            ButtonWidget done = ButtonWidget.builder(ScreenTexts.DONE, (button) -> {
                        for (EntryInfo info : entries)
                            if (info.id.equals(modid)) {
                                try {
                                    info.field.set(null, info.value);
                                } catch (IllegalAccessException ignored) {}
                            }
                        write(modid);
                        Objects.requireNonNull(client).setScreen(parent);
                    })
                    .dimensions(this.width / 2 + 4, this.height - 28, 150, 20)
                    .build();

            this.addDrawableChild(done);

            this.list = new MidnightConfigListWidget(this.client, this.width, this.height, 32, this.height - 32, 25);
            if (this.client != null && this.client.world != null) this.list.setRenderBackground(false);
            this.addSelectableChild(this.list);
            for (EntryInfo info : entries) {
                if (info.id.equals(modid)) {
                    Text name = Objects.requireNonNullElseGet(info.name, () -> Text.translatable(translationPrefix + info.field.getName()));
                    ButtonWidget resetButton = ButtonWidget.builder(Text.of("Reset").copy().formatted(Formatting.RED), button -> {
                                info.value = info.defaultValue;
                                info.tempValue = info.defaultValue.toString();
                                info.index = 0;
                                double scrollAmount = list.getScrollAmount();
                                this.reload = true;
                                Objects.requireNonNull(client).setScreen(this);
                                list.setScrollAmount(scrollAmount);
                            })
                            .dimensions(width - 205, 0, 40, 20)
                            .build();

                    if (info.widget instanceof Map.Entry) {
                        Map.Entry<ButtonWidget.PressAction, Function<Object, Text>> widget = (Map.Entry<ButtonWidget.PressAction, Function<Object, Text>>) info.widget;
                        if (info.field.getType().isEnum()) widget.setValue(value -> Text.translatable(translationPrefix + "enum." + info.field.getType().getSimpleName() + "." + info.value.toString()));

                        this.list.addButton(ButtonWidget.builder(widget.getValue().apply(info.value), widget.getKey())
                                .dimensions(width - 160, 0,150, 20)
                                .build(),resetButton, null,name);

                    } else if (info.field.getType() == List.class) {
                        if (!reload) info.index = 0;
                        TextFieldWidget widget = new TextFieldWidget(textRenderer, width - 160, 0, 150, 20, null);
                        widget.setMaxLength(info.width);
                        if (info.index < ((List<String>)info.value).size()) widget.setText((String.valueOf(((List<String>)info.value).get(info.index))));
                        else widget.setText("");
                        Predicate<String> processor = ((BiFunction<TextFieldWidget, ButtonWidget, Predicate<String>>) info.widget).apply(widget, done);
                        widget.setTextPredicate(processor);
                        resetButton.setWidth(20);
                        resetButton.setMessage(Text.of("R").copy().formatted(Formatting.RED));

                        ButtonWidget cycleButton = ButtonWidget.builder(Text.of(String.valueOf(info.index)).copy().formatted(Formatting.GOLD), (button -> {
                                    ((List<String>)info.value).remove("");
                                    double scrollAmount = list.getScrollAmount();
                                    this.reload = true;
                                    info.index = info.index + 1;
                                    if (info.index > ((List<String>)info.value).size()) info.index = 0;
                                    Objects.requireNonNull(client).setScreen(this);
                                    list.setScrollAmount(scrollAmount);
                                }))
                                .dimensions(width - 185, 0, 20, 20)
                                .build();

                        this.list.addButton(widget, resetButton, cycleButton, name);
                    } else if (info.widget != null) {
                        TextFieldWidget widget = new TextFieldWidget(textRenderer, width - 160, 0, 150, 20, null);
                        widget.setMaxLength(info.width);
                        widget.setText(info.tempValue);
                        Predicate<String> processor = ((BiFunction<TextFieldWidget, ButtonWidget, Predicate<String>>) info.widget).apply(widget, done);
                        widget.setTextPredicate(processor);
                        this.list.addButton(widget, resetButton, null, name);
                    } else {
                        this.list.addCategory(name);
                    }
                }
            }

        }
        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            this.renderBackground(matrices);
            this.list.render(matrices, mouseX, mouseY, delta);
            drawCenteredTextWithShadow(matrices, textRenderer, title, width / 2, 15, 0xFFFFFF);

            for (EntryInfo info : entries) {
                if (info.id.equals(modid)) {
                    if (list.getHoveredButton(mouseX,mouseY).isPresent()) {
                        ClickableWidget buttonWidget = list.getHoveredButton(mouseX,mouseY).get();
                        Text text = ButtonEntry.buttonsWithText.get(buttonWidget);
                        Text name = Text.translatable(this.translationPrefix + info.field.getName());
                        String key = translationPrefix + info.field.getName() + ".tooltip";

                        if (info.error != null && text.equals(name)) renderTooltip(matrices, info.error.getValue(), mouseX, mouseY);
                        else if (I18n.hasTranslation(key) && text.equals(name)) {
                            List<Text> list = new ArrayList<>();
                            for (String str : I18n.translate(key).split("\n"))
                                list.add(Text.of(str));
                            renderTooltip(matrices, list, mouseX, mouseY);
                        }
                    }
                }
            }
            super.render(matrices,mouseX,mouseY,delta);
        }
    }

    public static class MidnightConfigListWidget extends ElementListWidget<abstractEntry> {
        TextRenderer textRenderer;

        public MidnightConfigListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
            super(minecraftClient, i, j, k, l, m);
            this.centerListVertically = false;
            textRenderer = minecraftClient.textRenderer;
        }
        @Override
        public int getScrollbarPositionX() { return this.width -7; }

        public void addButton(ClickableWidget button, ClickableWidget resetButton, ClickableWidget indexButton, Text text) {
            this.addEntry(ButtonEntry.create(button, text, resetButton, indexButton));
        }
        public void addCategory(Text text) {
            this.addEntry(CategoryEntry.create(text));
        }
        @Override
        public int getRowWidth() { return 10000; }
        public Optional<ClickableWidget> getHoveredButton(double mouseX, double mouseY) {
            for (abstractEntry entry : this.children()) {
                if(entry instanceof ButtonEntry) {
                    if (((ButtonEntry)entry).button != null && ((ButtonEntry)entry).button.isMouseOver(mouseX, mouseY)) {
                        return Optional.of(((ButtonEntry)entry).button);
                    }
                }
            }
            return Optional.empty();
        }
    }
    /**
     * Modified by cobrasrock to make comments more obvious
     */
    public abstract static class abstractEntry extends ElementListWidget.Entry<abstractEntry>{}
    public static class ButtonEntry extends abstractEntry {
        private static final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        public final ClickableWidget button;
        private final ClickableWidget resetButton;
        private final ClickableWidget indexButton;
        private final Text text;
        private final List<ClickableWidget> children = new ArrayList<>();
        public static final Map<ClickableWidget, Text> buttonsWithText = new HashMap<>();

        private ButtonEntry(ClickableWidget button, Text text, ClickableWidget resetButton, ClickableWidget indexButton) {
            buttonsWithText.put(button,text);
            this.button = button;
            this.resetButton = resetButton;
            this.text = text;
            this.indexButton = indexButton;
            if (button != null) children.add(button);
            if (resetButton != null) children.add(resetButton);
            if (indexButton != null) children.add(indexButton);
        }
        public static ButtonEntry create(ClickableWidget button, Text text, ClickableWidget resetButton, ClickableWidget indexButton) {
            return new ButtonEntry(button, text, resetButton, indexButton);
        }
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            if (button != null) {
                button.setY(y);
                button.render(matrices, mouseX, mouseY, tickDelta);
            }
            if (resetButton != null) {
                button.setY(y);
                resetButton.render(matrices, mouseX, mouseY, tickDelta);
            }
            if (indexButton != null) {
                button.setY(y);
                indexButton.render(matrices, mouseX, mouseY, tickDelta);
            }
            if (text != null && (!text.getString().contains("spacer") || button != null))
                DrawableHelper.drawTextWithShadow(matrices,textRenderer, text,12,y+5,0xFFFFFF);
        }
        public List<? extends Element> children() {return children;}
        public List<? extends Selectable> selectableChildren() {return children;}
    }
    //comment entries
    public static class CategoryEntry extends abstractEntry {
        private static final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        private final Text text;
        private final List<ClickableWidget> children = new ArrayList<>();

        private CategoryEntry(Text text) {
            this.text = text;
        }
        public static CategoryEntry create(Text text) {
            return new CategoryEntry(((MutableText)text).formatted(Formatting.BOLD, Formatting.YELLOW));
        }
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            DrawableHelper.drawTextWithShadow(matrices,textRenderer, text,12,y+5,0xFFFFFF);
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return children;
        }
        @Override
        public List<? extends Element> children() {
            return children;
        }
    }
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Entry {
        int width() default 100;
        double min() default Double.MIN_NORMAL;
        double max() default Double.MAX_VALUE;
        String name() default "";
    }
    @Retention(RetentionPolicy.RUNTIME) @Target(ElementType.FIELD) public @interface Comment {}

    public static class HiddenAnnotationExclusionStrategy implements ExclusionStrategy {
        public boolean shouldSkipClass(Class<?> clazz) { return false; }
        public boolean shouldSkipField(FieldAttributes fieldAttributes) {
            return fieldAttributes.getAnnotation(Entry.class) == null;
        }
    }
}