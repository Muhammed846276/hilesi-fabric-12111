package com.example.hilesi.screen;

import com.example.hilesi.module.Module;
import com.example.hilesi.module.ModuleManager;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ClickGuiScreen extends Screen {
    private static final int PANEL_X = 20;
    private static final int PANEL_Y = 20;
    private static final int PANEL_W = 180;
    private static final int HEADER_H = 18;
    private static final int ROW_H = 16;
    private static final int PADDING = 6;

    public ClickGuiScreen() {
        super(Text.literal("Hilesi Menu"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Arkaplan (varsayilan blur/darken yerine hafif panel)
        int panelH = HEADER_H + PADDING + ModuleManager.getModules().size() * ROW_H + PADDING;
        context.fill(PANEL_X, PANEL_Y, PANEL_X + PANEL_W, PANEL_Y + panelH, 0xCC101010);
        context.fill(PANEL_X, PANEL_Y, PANEL_X + PANEL_W, PANEL_Y + HEADER_H, 0xFF2A6FDB);
        context.drawTextWithShadow(this.textRenderer, "Hilesi - Sag Shift ile kapat",
                PANEL_X + 6, PANEL_Y + 5, 0xFFFFFF);

        int y = PANEL_Y + HEADER_H + PADDING;
        for (Module module : ModuleManager.getModules()) {
            boolean hovered = mouseX >= PANEL_X && mouseX <= PANEL_X + PANEL_W
                    && mouseY >= y - 2 && mouseY <= y + ROW_H - 2;
            int nameColor = module.isEnabled() ? 0xFF55FF55 : 0xFFCCCCCC;
            if (hovered) {
                context.fill(PANEL_X + 2, y - 2, PANEL_X + PANEL_W - 2, y + ROW_H - 3, 0x33FFFFFF);
            }
            String state = module.isEnabled() ? "[ON] " : "[OFF] ";
            context.drawTextWithShadow(this.textRenderer, state + module.getName(),
                    PANEL_X + PADDING, y, nameColor);
            y += ROW_H;
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        if (click.button() == 0) { // sol tik
            int y = PANEL_Y + HEADER_H + PADDING;
            for (Module module : ModuleManager.getModules()) {
                if (mouseX >= PANEL_X && mouseX <= PANEL_X + PANEL_W
                        && mouseY >= y - 2 && mouseY <= y + ROW_H - 2) {
                    module.toggle();
                    return true;
                }
                y += ROW_H;
            }
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean shouldPause() {
        return false; // tek oyuncu dunyada oyun durmasin
    }
}
