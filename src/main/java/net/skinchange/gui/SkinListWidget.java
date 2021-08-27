package net.skinchange.gui;

import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.util.Identifier;
import net.skinchange.changeskin.SkinChange;

public class SkinListWidget extends AlwaysSelectedEntryListWidget<SkinEntry>
{
    SkinScreen parent;

    public SkinListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m, SkinScreen parent)
    {
        super(minecraftClient, i, j, k, l, m);
        this.parent = parent;
    }

    public void select(SkinEntry entry)
    {
        this.setSelected(entry);
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