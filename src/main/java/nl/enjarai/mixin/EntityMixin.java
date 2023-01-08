package nl.enjarai.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import nl.enjarai.RollingDownInTheDeep;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Redirect(
            method = "updateSwimming",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;isSprinting()Z"
            )
    )
    private boolean rollingDownInTheDeep$disableSprintCheck(Entity entity) {
        return entity instanceof ClientPlayerEntity &&
                RollingDownInTheDeep.enabled() ||
                entity.isSprinting();
    }
}
