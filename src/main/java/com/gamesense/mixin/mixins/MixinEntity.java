package com.gamesense.mixin.mixins;

import com.gamesense.api.event.GameSenseEvent;
import com.gamesense.api.event.events.EntityCollisionEvent;
import com.gamesense.api.event.events.StepEvent;
import com.gamesense.client.GameSense;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.movement.SafeWalk;
import com.gamesense.client.module.modules.movement.Scaffold;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class MixinEntity {

    @Inject(method = "applyEntityCollision", at = @At("HEAD"), cancellable = true)
    public void velocity(Entity entityIn, CallbackInfo ci) {
        EntityCollisionEvent event = new EntityCollisionEvent();
        GameSense.EVENT_BUS.post(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isSneaking()Z"))
    public boolean isSneaking(Entity entity) {
        return (ModuleManager.isModuleEnabled(Scaffold.class)) && !Minecraft.getMinecraft().gameSettings.keyBindSprint.isKeyDown()|| ModuleManager.isModuleEnabled(SafeWalk.class) || entity.isSneaking();
    }

/*    @Shadow private double d14; // X as defined on line 664
    @Shadow private double d7; // Z as defined on line 666 (spooky)
    @Shadow private double x;
    @Shadow private double z;
    @Inject(method = "move", at = @At(value = "HEAD"))
    public void move(MoverType type, double x, double y, double z, CallbackInfo ci) {
        if (!(d14 * d14 + d7 * d7 >= x * x + z * z)) {
            StepEvent event = new StepEvent();
            GameSense.EVENT_BUS.post(event);
        }
    }*/
}