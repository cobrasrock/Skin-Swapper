package net.skinchange.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
public class SkinListWidget extends AlwaysSelectedEntryListWidget<SkinEntry>
{
    public SkinListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m)
    {
        super(minecraftClient, i, j, k, l, m);
    }

    public void select(SkinEntry entry)
    {
        this.setSelected(entry);
    }

    protected int getScrollbarPosition()
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