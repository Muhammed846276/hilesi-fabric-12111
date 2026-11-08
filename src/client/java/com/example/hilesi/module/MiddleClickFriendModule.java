package com.example.hilesi.module;
import com.example.hilesi.util.FriendManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.glfw.GLFW;
public class MiddleClickFriendModule extends Module {
    private boolean wasDown=false;
    public MiddleClickFriendModule(){ super("MiddleClickFriend","Orta tikla arkadas ekle/cikar"); }
    @Override public void onTick(Minecraft client){
        if(!isEnabled()||client==null||client.getWindow()==null) return;
        boolean down=false; try { down = GLFW.glfwGetMouseButton(client.getWindow().getWindow(), GLFW.GLFW_MOUSE_BUTTON_MIDDLE)==GLFW.GLFW_PRESS; } catch(Throwable ignored){}
        if(down && !wasDown){
            try { if (client.crosshairPickEntity instanceof Player p) FriendManager.toggle(p.getGameProfile().name()); } catch(Throwable ignored){}
        }
        wasDown=down;
    }
}
