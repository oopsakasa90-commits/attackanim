package com.example.attackanim.anim;

import java.util.IdentityHashMap;
import java.util.Map;

/** Trạng thái dùng chung của animation (tách ra khỏi mixin vì mixin không nên chứa static có khởi tạo). */
public final class AnimState {
	private AnimState() {}

	public static final ComboTracker TRACKER = new ComboTracker();
	public static final Pose POSE = new Pose();

	/** Cổ tay của từng cánh tay (khóa = đối tượng ModelPart của cánh tay): {flex, twist} theo radian. */
	private static final Map<Object, float[]> WRIST = new IdentityHashMap<>();
	public static boolean wristHookLogged = false;

	public static void setWrist(Object armPart, float flex, float twist) {
		float[] w = WRIST.get(armPart);
		if (w == null) {
			w = new float[2];
			WRIST.put(armPart, w);
		}
		w[0] = flex;
		w[1] = twist;
	}

	public static float[] getWrist(Object armPart) {
		return WRIST.get(armPart);
	}
}
