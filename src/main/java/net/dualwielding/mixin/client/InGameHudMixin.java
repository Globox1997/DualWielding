package net.dualwielding.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.fabricmc.api.Environment;
import net.dualwielding.access.PlayerAccess;
import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow
    @Final
    @Mutable
    private final MinecraftClient client;
    @Shadow
    private int scaledWidth;
    @Shadow
    private int scaledHeight;

    private static final Identifier CROSS_HAIR_TEXTURE = new Identifier("dualwielding", "textures/gui/crosshair_indicator.png");
    private static final Identifier HOTBAR_INDICATOR_TEXTURE = new Identifier("dualwielding", "textures/gui/crosshair_indicator.png");

    public InGameHudMixin(MinecraftClient client) {
        this.client = client;
    }

    @Inject(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getAttackCooldownProgress(F)F", shift = Shift.AFTER))
    private void renderCrosshairMixinTEST(DrawContext context, CallbackInfo info) {
        float o = ((PlayerAccess) this.client.player).getAttackCooldownProgressDualOffhand(1.0F);
        if (o < 1.0F) {
            int u = (int) (o * 17.0F);
            context.drawTexture(CROSS_HAIR_TEXTURE, this.scaledWidth / 2 - 8, this.scaledHeight / 2 - 7 + 16, 0.0F, 0.0F, u, 4, 16, 16);
        }
    }

    @Inject(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getAttackCooldownProgress(F)F", shift = Shift.AFTER))
    private void renderHotbar(float tickDelta, DrawContext context, CallbackInfo info) {
        float o = ((PlayerAccess) this.client.player).getAttackCooldownProgressDualOffhand(1.0F);
        if (o < 1.0F) {
            Arm arm = this.client.player.getMainArm().getOpposite();
            int r = (this.scaledWidth / 2) + 91 + 6;
            if (arm == Arm.RIGHT) {
                r = (this.scaledWidth / 2) - 91 - 22;
            }
            int s = (int) (o * 19.0F);
            context.drawTexture(HOTBAR_INDICATOR_TEXTURE, r, this.scaledHeight - 20 + 18 - s, 0.0F, 18.0F - s, 18, s, 32, 32);
        }
    }

}
