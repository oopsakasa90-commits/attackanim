package com.example.attackanim.mixin;

import com.example.attackanim.AttackAnimConfig;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Thay animation vung tay khi tấn công bằng một đòn chém 3 pha:
 * giơ cao (wind-up) -> chém chéo xuống (slash) -> thu về (recover),
 * kèm xoay thân và bước chân. Hiển thị rõ nhất ở góc nhìn thứ 3.
 */
@Mixin(PlayerEntityModel.class)
public abstract class PlayerEntityModelMixin extends BipedEntityModel<PlayerEntityRenderState> {

	// Constructor giả, chỉ để mixin kế thừa được BipedEntityModel (không bao giờ được gọi).
	protected PlayerEntityModelMixin(ModelPart root) {
		super(root);
	}

	@Inject(
		method = "setAngles(Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;)V",
		at = @At("TAIL")
	)
	private void attackanim$slash(PlayerEntityRenderState state, CallbackInfo ci) {
		AttackAnimConfig cfg = AttackAnimConfig.get();
		if (!cfg.enabled) return;

		float p = state.handSwingProgress;
		if (p <= 0.0F || p >= 1.0F) return;

		float k = MathHelper.clamp(cfg.intensity, 0.0F, 2.0F);
		boolean right = state.mainArm == Arm.RIGHT;
		float side = right ? 1.0F : -1.0F;

		ModelPart main = right ? this.rightArm : this.leftArm;
		ModelPart off = right ? this.leftArm : this.rightArm;
		ModelPart frontLeg = right ? this.leftLeg : this.rightLeg;
		ModelPart backLeg = right ? this.rightLeg : this.leftLeg;

		// Ba pha của đòn chém (tổng = 1.0)
		float wind = MathHelper.clamp(p / 0.30F, 0.0F, 1.0F);            // 0.00 - 0.30: giơ lên
		float slash = MathHelper.clamp((p - 0.30F) / 0.25F, 0.0F, 1.0F); // 0.30 - 0.55: chém xuống
		float rec = MathHelper.clamp((p - 0.55F) / 0.45F, 0.0F, 1.0F);   // 0.55 - 1.00: thu về

		float eWind = easeOut(wind);
		float eSlash = easeIn(slash);
		float eRec = easeOut(rec);

		// Tay chính: pitch 0 -> -2.6 (giơ cao) -> -0.3 (chém tới trước) -> 0
		float pitch = (-2.6F * eWind + 2.3F * eSlash + 0.3F * eRec) * k;
		float yaw = (-0.5F * eWind + 1.4F * eSlash - 0.9F * eRec) * k;
		float roll = (0.35F * eWind - 0.5F * eSlash + 0.15F * eRec) * k;

		// Xoay thân (ngược chiều khi giơ, quét theo khi chém)
		float bodyYaw = 0.0F;
		if (cfg.bodyTwist) {
			bodyYaw = side * (-0.45F * eWind + 1.0F * eSlash - 0.55F * eRec) * k;
			this.body.yaw = bodyYaw;
			this.head.yaw -= bodyYaw * 0.5F;

			// Giữ vai bám theo thân (giống cách vanilla làm)
			this.rightArm.originZ = MathHelper.sin(bodyYaw) * 5.0F;
			this.rightArm.originX = -MathHelper.cos(bodyYaw) * 5.0F;
			this.leftArm.originZ = -MathHelper.sin(bodyYaw) * 5.0F;
			this.leftArm.originX = MathHelper.cos(bodyYaw) * 5.0F;
		}

		main.pitch = pitch;
		main.yaw = side * yaw + bodyYaw;
		main.roll = side * roll;

		// Tay phụ giữ thăng bằng
		off.pitch = (0.4F * (eWind - eRec) - 0.3F * eSlash) * k + bodyYaw;
		off.yaw = bodyYaw;
		off.roll = -side * 0.25F * (eWind + eSlash) * (1.0F - eRec) * k;

		// Bước chân theo nhịp chém
		if (cfg.legStep) {
			float step = eSlash * (1.0F - eRec);
			frontLeg.pitch = -0.6F * step * k;
			backLeg.pitch = 0.5F * step * k;
		}
	}

	private static float easeOut(float t) {
		return 1.0F - (1.0F - t) * (1.0F - t);
	}

	private static float easeIn(float t) {
		return t * t;
	}
}
