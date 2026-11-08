package com.example.shadowpanel.module;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;

/**
 * AutoMace: SpearMace / StunSlam (axe) / FullAutoMace (elytra+firework -> mace slam)
 */
public class AutoMaceModule extends Module {
    public enum Mode { SPEAR_MACE, STUN_SLAM, FULL_AUTO }
    private Mode mode = Mode.SPEAR_MACE;
    private double range = 6.0;
    private int cps = 8;
    private boolean targetPlayers = true;
    private double boostHeight = 12.0; // full auto: yukari cikis hedefi
    private long nextAction = 0;
    private int stunPhase = 0; // 0=axe,1=mace

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

    @Override public void onTick(Minecraft mc) {
        if (!isEnabled() || mc == null || mc.player == null || mc.level == null || mc.gameMode == null) return;
        if (mc.gui.screen() != null) return;
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

    private void tickSpearMace(Minecraft mc) {
        LivingEntity target = findTarget(mc, range);
        if (target == null) return;
        int mace = findHotbar(mc, this::isMace);
        int spear = findHotbar(mc, this::isSpear);
        double dist = mc.player.distanceTo(target);
        boolean falling = mc.player.getDeltaMovement().y < -0.08;
        if (falling && mace >= 0) selectSlot(mc, mace);
        else if (dist > 3.5 && spear >= 0) selectSlot(mc, spear);
        else if (mace >= 0) selectSlot(mc, mace);
        else if (spear >= 0) selectSlot(mc, spear);
        lookAt(mc, target);
        attack(mc, target);
    }

    private void tickStunSlam(Minecraft mc) {
        LivingEntity target = findTarget(mc, range);
        if (target == null) { stunPhase = 0; return; }
        int axe = findHotbar(mc, this::isAxe);
        int mace = findHotbar(mc, this::isMace);
        if (axe < 0 || mace < 0) { // fallback: whatever we have
            tickSpearMace(mc); return;
        }
        lookAt(mc, target);
        if (stunPhase == 0) { selectSlot(mc, axe); attack(mc, target); stunPhase = 1; nextAction = System.currentTimeMillis() + 120; }
        else { selectSlot(mc, mace); attack(mc, target); stunPhase = 0; nextAction = System.currentTimeMillis() + 80; }
    }

    private void tickFullAuto(Minecraft mc) {
        LivingEntity target = findTarget(mc, Math.max(range, 24.0));
        if (target == null) return;
        boolean elytraOn = isWearingElytra(mc);
        boolean moving = isMovingForward(mc);
        int mace = findHotbar(mc, this::isMace);
        int rocket = findHotbar(mc, this::isRocket);

        // boost phase: elytra + moving -> use firework to gain height
        if (elytraOn && (mc.player.isFallFlying() || moving) && rocket >= 0) {
            double dy = mc.player.getY() - target.getY();
            boolean needHeight = dy < boostHeight * 0.5 && mc.player.getY() < target.getY() + boostHeight;
            if (needHeight || (mc.player.isFallFlying() && mc.player.getDeltaMovement().y > -0.2 && dy < boostHeight)) {
                selectSlot(mc, rocket);
                try { mc.gameMode.useItem(mc.player, InteractionHand.MAIN_HAND); } catch (Throwable ignored) {}
                lookToward(mc, target.getX(), target.getY() + boostHeight, target.getZ());
                nextAction = System.currentTimeMillis() + 450;
                return;
            }
        }
        // slam phase: above target and falling/descending with mace
        if (mace >= 0) {
            double above = mc.player.getY() - target.getY();
            double horiz = Math.hypot(mc.player.getX() - target.getX(), mc.player.getZ() - target.getZ());
            boolean falling = mc.player.getDeltaMovement().y < -0.1 || mc.player.fallDistance > 1.5f;
            if (above > 1.5 && horiz < 4.5 && falling) {
                selectSlot(mc, mace);
                lookAt(mc, target);
                attack(mc, target);
                nextAction = System.currentTimeMillis() + 120;
                return;
            }
            // if close enough without full setup, basic mace/spear fallback
            if (horiz < range) tickSpearMace(mc);
        } else {
            tickSpearMace(mc);
        }
    }

    private LivingEntity findTarget(Minecraft mc, double r) {
        AABB box = mc.player.getBoundingBox().inflate(r);
        List<LivingEntity> list = mc.level.getEntitiesOfClass(LivingEntity.class, box, e ->
                e != mc.player && e.isAlive() && (!targetPlayers || e instanceof Player) && mc.player.distanceToSqr(e) <= r*r);
        return list.stream().min(Comparator.comparingDouble(e -> mc.player.distanceToSqr(e))).orElse(null);
    }

    private int findHotbar(Minecraft mc, java.util.function.Predicate<ItemStack> pred) {
        try {
            for (int i = 0; i < 9; i++) { ItemStack s = mc.player.getInventory().getItem(i); if (s != null && !s.isEmpty() && pred.test(s)) return i; }
        } catch (Throwable ignored) {}
        return -1;
    }

    private void selectSlot(Minecraft mc, int slot) {
        if (slot < 0 || slot > 8) return;
        try {
            // Mojang 26.x: Inventory.selected or getSelectedSlot/setSelectedSlot varies; try both
            try { mc.player.getInventory().getClass().getMethod("setSelectedSlot", int.class).invoke(mc.player.getInventory(), slot); }
            catch (Throwable e) {
                try { java.lang.reflect.Field f = mc.player.getInventory().getClass().getDeclaredField("selected"); f.setAccessible(true); f.setInt(mc.player.getInventory(), slot); }
                catch (Throwable e2) { /* last resort */ }
            }
        } catch (Throwable ignored) {}
    }

    private boolean isMace(ItemStack s) { try { return s.is(Items.MACE) || path(s).equals("mace"); } catch (Throwable t) { return path(s).contains("mace"); } }
    private boolean isSpear(ItemStack s) { String p = path(s); return p.equals("spear") || p.endsWith("_spear"); }
    private boolean isAxe(ItemStack s) { String p = path(s); return p.endsWith("_axe"); }
    private boolean isRocket(ItemStack s) { try { return s.is(Items.FIREWORK_ROCKET) || path(s).contains("firework"); } catch (Throwable t) { return path(s).contains("firework"); } }

    private String path(ItemStack s) {
        try { Identifier id = BuiltInRegistries.ITEM.getKey(s.getItem()); return id != null ? id.getPath() : ""; } catch (Throwable t) { return ""; }
    }

    private boolean isWearingElytra(Minecraft mc) {
        try {
            try {
                ItemStack a = mc.player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST);
                if (a != null && !a.isEmpty() && (a.is(Items.ELYTRA) || path(a).contains("elytra"))) return true;
            } catch (Throwable ignored) {}
            try {
                for (Object o : (Iterable<?>) mc.player.getClass().getMethod("getArmorSlots").invoke(mc.player)) {
                    ItemStack a = (ItemStack) o; if (a != null && !a.isEmpty() && (a.is(Items.ELYTRA) || path(a).contains("elytra"))) return true;
                }
            } catch (Throwable ignored) {}
        } catch (Throwable ignored) {}
        return false;
    }

    private boolean isMovingForward(Minecraft mc) {
        try { return mc.options.keyUp.isDown(); } catch (Throwable t) { return false; }
    }

    private void lookAt(Minecraft mc, Entity target) {
        try {
            Vec3 e = target.position().add(0, target.getBbHeight() * 0.5, 0);
            lookToward(mc, e.x, e.y, e.z);
        } catch (Throwable ignored) {}
    }
    private void lookToward(Minecraft mc, double x, double y, double z) {
        try {
            Vec3 eye = mc.player.getEyePosition();
            double dx=x-eye.x, dy=y-eye.y, dz=z-eye.z;
            double hd = Math.sqrt(dx*dx+dz*dz);
            mc.player.setYRot((float)(Math.toDegrees(Math.atan2(dz,dx))-90f));
            mc.player.setXRot((float)(-Math.toDegrees(Math.atan2(dy,hd))));
        } catch (Throwable ignored) {}
    }
    private void attack(Minecraft mc, LivingEntity target) {
        try { mc.gameMode.attack(mc.player, target); mc.player.swing(InteractionHand.MAIN_HAND); } catch (Throwable ignored) {}
    }
}
