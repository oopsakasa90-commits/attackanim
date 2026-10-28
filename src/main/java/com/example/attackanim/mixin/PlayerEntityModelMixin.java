package com.example.attackanim.mixin;

import com.example.attackanim.AttackAnimConfig;
import com.example.attackanim.anim.AnimState;
import com.example.attackanim.anim.Combos;
import com.example.attackanim.anim.Pose;
import com.example.attackanim.anim.Strike;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.EntityPose;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Dáng đứng + combo đánh khi cầm kiếm (xem {@link Combos}).
 * - Chỉ tay chính cầm kiếm: combo 4 đòn.
 * - Tay phụ cũng cầm item: combo 5 đòn song kiếm (đòn của tay phụ chỉ là hiệu ứng hình ảnh).
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
	private void attackanim$apply(PlayerEntityRenderState state, CallbackInfo ci) {
		AttackAnimConfig cfg = AttackAnimConfig.get();
		if (!cfg.enabled) return;

		// Chỉ áp dụng khi đứng/đi bình thường (không bơi, bay, ngủ, ngồi xe, ngồi xổm, đang dùng item)
		if (state.pose != EntityPose.STANDING) return;
		if (state.hasVehicle || state.isGliding || state.isSwimming || state.isUsingItem) return;
		if (state.leaningPitch > 0.0F) return;

		boolean rightHanded = state.mainArm == Arm.RIGHT;
		ItemStack mainStack = rightHanded ? state.rightHandItem : state.leftHandItem;
		ItemStack offStack = rightHanded ? state.leftHandItem : state.rightHandItem;
		if (!attackanim$isWeapon(mainStack, cfg)) return;

		BipedEntityModel.ArmPose offPose = rightHanded ? state.leftArmPose : state.rightArmPose;
		if (offPose != BipedEntityModel.ArmPose.EMPTY && offPose != BipedEntityModel.ArmPose.ITEM) return;

		float progress = state.handSwingProgress;
		// Đang vung tay PHỤ (ví dụ đặt block bằng tay trái): để game tự xử lý
		if (progress > 0.0F && state.preferredArm != state.mainArm) return;

		boolean dual = !offStack.isEmpty();
		long now = System.nanoTime();
		int step = AnimState.TRACKER.update(state.id, progress, now, cfg.comboResetMs * 1_000_000L);

		Pose stance = dual ? Combos.DUAL_STANCE : Combos.SINGLE_STANCE;
		Pose pose = AnimState.POSE;
		if (progress > 0.0F) {
			Strike[] combo = dual ? Combos.DUAL : Combos.SINGLE;
			combo[step % combo.length].sample(progress, pose);
			pose.scaleFrom(stance, MathHelper.clamp(cfg.intensity, 0.0F, 2.0F));
		} else {
			if (!cfg.stance) return;
			System.arraycopy(stance.v, 0, pose.v, 0, Pose.COUNT);
		}

		// Thêm nhịp thở nhẹ khi đứng yên
		if (progress <= 0.0F) {
			float breathe = MathHelper.sin(state.age * 0.09F) * 0.025F;
			pose.v[Pose.RA_P] += breathe;
			pose.v[Pose.LA_P] -= breathe;
		}

		Pose applied = rightHanded ? pose : pose.mirrored();
		float legWeight = cfg.legStep ? MathHelper.clamp(1.0F - state.limbSwingAmplitude * 3.0F, 0.0F, 1.0F) : 0.0F;
		attackanim$applyPose(applied, cfg.bodyTwist, legWeight);
	}

	private void attackanim$applyPose(Pose p, boolean bodyTwist, float legWeight) {
		float[] v = p.v;
		float bodyYaw = bodyTwist ? v[Pose.B_Y] : 0.0F;

		this.body.pitch = v[Pose.B_P];
		this.body.yaw = bodyYaw;
		this.body.roll = v[Pose.B_R];

		// Đầu giữ hướng nhìn, bù lại phần thân xoay/chồm
		this.head.yaw -= bodyYaw;
		this.head.pitch -= v[Pose.B_P] * 0.6F;

		// Vai bám theo thân khi thân xoay (giống cách game làm)
		this.rightArm.originZ = MathHelper.sin(bodyYaw) * 5.0F + v[Pose.RA_OZ];
		this.rightArm.originX = -MathHelper.cos(bodyYaw) * 5.0F;
		this.rightArm.originY = 2.0F + v[Pose.RA_OY];
		this.leftArm.originZ = -MathHelper.sin(bodyYaw) * 5.0F + v[Pose.LA_OZ];
		this.leftArm.originX = MathHelper.cos(bodyYaw) * 5.0F;
		this.leftArm.originY = 2.0F + v[Pose.LA_OY];

		this.rightArm.pitch = v[Pose.RA_P] + v[Pose.B_P];
		this.rightArm.yaw = v[Pose.RA_Y] + bodyYaw;
		this.rightArm.roll = v[Pose.RA_R];
		this.leftArm.pitch = v[Pose.LA_P] + v[Pose.B_P];
		this.leftArm.yaw = v[Pose.LA_Y] + bodyYaw;
		this.leftArm.roll = v[Pose.LA_R];

		if (legWeight > 0.0F) {
			this.rightLeg.pitch += v[Pose.RL_P] * legWeight;
			this.leftLeg.pitch += v[Pose.LL_P] * legWeight;
			this.rightLeg.roll += v[Pose.RL_R] * legWeight;
			this.leftLeg.roll += v[Pose.LL_R] * legWeight;
		}
	}

	private static boolean attackanim$isWeapon(ItemStack stack, AttackAnimConfig cfg) {
		if (stack.isEmpty()) return false;
		String path = Registries.ITEM.getId(stack.getItem()).getPath();
		for (String keyword : cfg.weaponKeywords) {
			if (keyword != null && !keyword.isEmpty() && path.contains(keyword)) return true;
		}
		return false;
	}
}
