package com.example.shadowpanel.module;
import com.example.shadowpanel.util.FriendManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.glfw.GLFW;
public class MiddleClickFriendModule extends Module {
    private boolean wasDown=false;
    public MiddleClickFriendModule(){ super("MiddleClickFriend"); }
    @Override public void onTick(MinecraftClient client){
        if(!isEnabled()||client==null||client.getWindow()==null) return;
        boolean down=false; try{ down=GLFW.glfwGetMouseButton(client.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_MIDDLE)==GLFW.GLFW_PRESS; }catch(Throwable ignored){}
        if(down&&!wasDown){
            try{
                if(client.targetedEntity instanceof PlayerEntity p) {
                    Object gp = p.getGameProfile();
                    String name = null;
                    try { name = (String) gp.getClass().getMethod("getName").invoke(gp); } catch(Throwable e){}
                    if (name==null) try { name = (String) gp.getClass().getMethod("name").invoke(gp); } catch(Throwable e){}
                    if (name!=null) FriendManager.toggle(name);
                }
            }catch(Throwable ignored){}
        }
        wasDown=down;
    }
}
