package com.example.shadowpanel.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;
import java.util.List;

/** AutoMace (Yarn 1.21.11): SpearMace / StunSlam / FullAuto */
public class AutoMaceModule extends Module {
    public enum Mode { SPEAR_MACE, STUN_SLAM, FULL_AUTO }
    private Mode mode = Mode.SPEAR_MACE;
    private double range = 6.0;
    private int cps = 8;
    private boolean targetPlayers = true;
    private double boostHeight = 12.0;
    private long nextAction = 0;
    private int stunPhase = 0;

    public AutoMaceModule() { super("AutoMace", "Spear+Mace, StunSlam(balta), FullAuto(elytra+fişek+slam)"); }
    public Mode getMode() { return mode; }
    public void cycleMode(int d) { Mode[] v = Mode.values(); mode = v[(mode.ordinal() + (d>0?1:v.length-1)) % v.length]; }
    public double getRange() { return range; }
    public void cycleRange(int d) { range = Math.max(3.0, Math.min(10.0, range + d*0.5)); }
    public int getCps() { return cps; }
    public void cycleCps(int d) { cps = Math.max(1, Math.min(20, cps + d)); }
    public boolean isTargetPlayers() { return targetPlayers; }
    public void toggleTargetPlayers() { targetPlayers = !targetPlayers; }
    public double getBoostHeight() { return boostHeight; }
    public void cycleBoostHeight(int d) { boostHeight = Math.max(6.0, Math.min(40.0, boostHeight + d*2.0)); }

    @Override public void onTick(MinecraftClient mc) {
        if (!isEnabled() || mc == null || mc.player == null || mc.world == null || mc.interactionManager == null) return;
        if (mc.currentScreen != null) return;
        long now = System.currentTimeMillis();
        if (now < nextAction) return;
        try {
            switch (mode) {
                case SPEAR_MACE -> tickSpearMace(mc);
                case STUN_SLAM -> tickStunSlam(mc);
                case FULL_AUTO -> tickFullAuto(mc);
            }
        } catch (Throwable ignored) {}
        nextAction = now + Math.max(50, 1000L / Math.max(1, cps));
    }

    private void tickSpearMace(MinecraftClient mc) {
        LivingEntity target = findTarget(mc, range);
        if (target == null) return;
        int mace = findHotbar(mc, this::isMace), spear = findHotbar(mc, this::isSpear);
        double dist = mc.player.distanceTo(target);
        boolean falling = mc.player.getVelocity().y < -0.08;
        if (falling && mace >= 0) selectSlot(mc, mace);
        else if (dist > 3.5 && spear >= 0) selectSlot(mc, spear);
        else if (mace >= 0) selectSlot(mc, mace);
        else if (spear >= 0) selectSlot(mc, spear);
        lookAt(mc, target); attack(mc, target);
    }

    private void tickStunSlam(MinecraftClient mc) {
        LivingEntity target = findTarget(mc, range);
        if (target == null) { stunPhase = 0; return; }
        int axe = findHotbar(mc, this::isAxe), mace = findHotbar(mc, this::isMace);
        if (axe < 0 || mace < 0) { tickSpearMace(mc); return; }
        lookAt(mc, target);
        if (stunPhase == 0) { selectSlot(mc, axe); attack(mc, target); stunPhase = 1; nextAction = System.currentTimeMillis() + 120; }
        else { selectSlot(mc, mace); attack(mc, target); stunPhase = 0; nextAction = System.currentTimeMillis() + 80; }
    }

    private void tickFullAuto(MinecraftClient mc) {
        LivingEntity target = findTarget(mc, Math.max(range, 24.0));
        if (target == null) return;
        boolean elytraOn = isWearingElytra(mc);
        int mace = findHotbar(mc, this::isMace), rocket = findHotbar(mc, this::isRocket);
        if (elytraOn && (mc.player.isFallFlying() || isMovingForward(mc)) && rocket >= 0) {
            double dy = mc.player.getY() - target.getY();
            boolean needHeight = dy < boostHeight * 0.5 && mc.player.getY() < target.getY() + boostHeight;
            if (needHeight || (mc.player.isFallFlying() && mc.player.getVelocity().y > -0.2 && dy < boostHeight)) {
                selectSlot(mc, rocket);
                try { mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND); } catch (Throwable ignored) {}
                lookToward(mc, target.getX(), target.getY() + boostHeight, target.getZ());
                nextAction = System.currentTimeMillis() + 450; return;
            }
        }
        if (mace >= 0) {
            double above = mc.player.getY() - target.getY();
            double horiz = Math.hypot(mc.player.getX() - target.getX(), mc.player.getZ() - target.getZ());
            boolean falling = mc.player.getVelocity().y < -0.1 || mc.player.fallDistance > 1.5f;
            if (above > 1.5 && horiz < 4.5 && falling) { selectSlot(mc, mace); lookAt(mc, target); attack(mc, target); nextAction = System.currentTimeMillis() + 120; return; }
            if (horiz < range) tickSpearMace(mc);
        } else tickSpearMace(mc);
    }

    private LivingEntity findTarget(MinecraftClient mc, double r) {
        Box box = mc.player.getBoundingBox().expand(r);
        List<LivingEntity> list = mc.world.getEntitiesByClass(LivingEntity.class, box, e -> e != mc.player && e.isAlive() && (!targetPlayers || e instanceof PlayerEntity) && mc.player.squaredDistanceTo(e) <= r*r);
        return list.stream().min(Comparator.comparingDouble(e -> mc.player.squaredDistanceTo(e))).orElse(null);
    }
    private int findHotbar(MinecraftClient mc, java.util.function.Predicate<ItemStack> pred) {
        try { for (int i=0;i<9;i++){ ItemStack s = mc.player.getInventory().getStack(i); if (s!=null && !s.isEmpty() && pred.test(s)) return i; } } catch (Throwable ignored) {}
        return -1;
    }
    private void selectSlot(MinecraftClient mc, int slot) { if (slot<0||slot>8) return; try { mc.player.getInventory().selectedSlot = slot; } catch (Throwable ignored) {} }
    private boolean isMace(ItemStack s){ try { return s.isOf(Items.MACE) || path(s).equals("mace"); } catch (Throwable t){ return path(s).contains("mace"); } }
    private boolean isSpear(ItemStack s){ String p=path(s); return p.equals("spear")||p.endsWith("_spear"); }
    private boolean isAxe(ItemStack s){ return path(s).endsWith("_axe"); }
    private boolean isRocket(ItemStack s){ try { return s.isOf(Items.FIREWORK_ROCKET)||path(s).contains("firework"); } catch (Throwable t){ return path(s).contains("firework"); } }
    private String path(ItemStack s){ try { Identifier id = Registries.ITEM.getId(s.getItem()); return id!=null?id.getPath():""; } catch (Throwable t){ return ""; } }
    private boolean isWearingElytra(MinecraftClient mc){ try { for (ItemStack a: mc.player.getArmorItems()) if (a!=null && !a.isEmpty() && (a.isOf(Items.ELYTRA)||path(a).contains("elytra"))) return true; } catch (Throwable ignored) {} return false; }
    private boolean isMovingForward(MinecraftClient mc){ try { return mc.options.forwardKey.isPressed(); } catch (Throwable t){ return false; } }
    private void lookAt(MinecraftClient mc, Entity target){ try { Vec3d e = target.getPos().add(0,target.getHeight()*0.5,0); lookToward(mc,e.x,e.y,e.z); } catch (Throwable ignored) {} }
    private void lookToward(MinecraftClient mc, double x,double y,double z){ try { Vec3d eye=mc.player.getEyePos(); double dx=x-eye.x,dy=y-eye.y,dz=z-eye.z,hd=Math.sqrt(dx*dx+dz*dz); mc.player.setYaw((float)(Math.toDegrees(Math.atan2(dz,dx))-90f)); mc.player.setPitch((float)(-Math.toDegrees(Math.atan2(dy,hd)))); } catch (Throwable ignored) {} }
    private void attack(MinecraftClient mc, LivingEntity target){ try { mc.interactionManager.attackEntity(mc.player,target); mc.player.swingHand(Hand.MAIN_HAND); } catch (Throwable ignored) {} }
}
