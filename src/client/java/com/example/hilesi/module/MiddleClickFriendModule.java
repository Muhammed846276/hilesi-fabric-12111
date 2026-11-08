package com.example.hilesi.module;
import com.example.hilesi.util.FriendManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.glfw.GLFW;
public class MiddleClickFriendModule extends Module {
    private boolean wasDown=false;
    public MiddleClickFriendModule(){ super("MiddleClickFriend"); }
    @Override public void onTick(MinecraftClient client){ if(!isEnabled()||client==null||client.getWindow()==null) return; boolean down=false; try{ down=GLFW.glfwGetMouseButton(client.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_MIDDLE)==GLFW.GLFW_PRESS; }catch(Throwable ignored){} if(down&&!wasDown){ try{ if(client.targetedEntity instanceof PlayerEntity p) FriendManager.toggle(p.getGameProfile().getName()); }catch(Throwable ignored){} } wasDown=down; }
}
