package com.example.attackanim.anim;

/** Trạng thái dùng chung của animation (tách ra khỏi mixin vì mixin không nên chứa static có khởi tạo). */
public final class AnimState {
	private AnimState() {}

	public static final ComboTracker TRACKER = new ComboTracker();
	public static final Pose POSE = new Pose();
}
