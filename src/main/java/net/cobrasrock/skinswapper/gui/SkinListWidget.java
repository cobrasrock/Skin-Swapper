package net.cobrasrock.skinswapper.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;

public class SkinListWidget extends AlwaysSelectedEntryListWidget<SkinEntry>
{
    SkinScreen parent;

    public SkinListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight, SkinScreen parent)
    {
        super(client, width, height, top, bottom, itemHeight);
        this.parent = parent;
    }

    @Override
    protected int getScrollbarPositionX()
    {
		return this.width - 6;
    }
    
    @Override
    protected int getMaxPosition()
    {
		return super.getMaxPosition() + 4;
    }
    
    @Override
    public int getRowWidth()
    {
		return this.width - (Math.max(0, this.getMaxPosition() - (this.bottom - this.top - 4)) > 0 ? 18 : 12);
    }

}