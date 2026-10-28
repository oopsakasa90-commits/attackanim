package com.example.attackanim.anim;

import static com.example.attackanim.anim.Strike.IN;
import static com.example.attackanim.anim.Strike.OUT;
import static com.example.attackanim.anim.Strike.SMOOTH;

/**
 * Toàn bộ dáng đứng và combo. Mọi tư thế viết cho người thuận tay PHẢI
 * (tay chính = tay phải), người thuận tay trái sẽ được lật gương tự động.
 */
public final class Combos {
	private Combos() {}

	// ------------------------------------------------------------------
	// DÁNG ĐỨNG (khi không đánh)
	// ------------------------------------------------------------------

	/**
	 * Chỉ cầm kiếm tay chính: kiếm nghiêng lên chĩa về trước, thân xoay nghiêng,
	 * tay phụ cong nhẹ đặt ra sau lưng, chân bước lệch.
	 */
	public static final Pose SINGLE_STANCE = new Pose()
		.rArm(-38, -17, 7).rPos(0, -1)
		.lArm(36, -12, 20)
		.body(3, -10, 0)
		.legs(-12, 10, 3, -3);

	/**
	 * Cầm 2 vũ khí: hai tay đưa ra trước, mũi kiếm chéo nhau như thủ thế song kiếm,
	 * người hơi chồm, hai chân dang rộng.
	 */
	public static final Pose DUAL_STANCE = new Pose()
		.rArm(-52, -30, 4).rPos(0, -1.5f)
		.lArm(-52, 30, -4).lPos(0, -1.5f)
		.body(6, 0, 0)
		.legs(-6, -6, 8, -8);

	// ------------------------------------------------------------------
	// COMBO MỘT TAY (kiếm tay chính): ngang P->T, ngang T->P, đâm, chém từ trên xuống
	// ------------------------------------------------------------------

	public static final Strike[] SINGLE = new Strike[] {
		// 1. Chém ngang từ phải sang trái
		Strike.from(SINGLE_STANCE)
			.key(0.22f, SMOOTH, SINGLE_STANCE.copy()
				.rArm(-60, 70, 25).body(0, 30, 0).lArm(30, -12, 10))
			.key(0.50f, IN, SINGLE_STANCE.copy()
				.rArm(-60, -55, -18).body(3, -32, 0).lArm(25, -12, -15))
			.key(0.72f, OUT, SINGLE_STANCE.copy()
				.rArm(-58, -70, -22).body(3, -38, 0).lArm(20, -12, -25))
			.end(SMOOTH, SINGLE_STANCE),

		// 2. Chém ngang từ trái sang phải
		Strike.from(SINGLE_STANCE)
			.key(0.22f, SMOOTH, SINGLE_STANCE.copy()
				.rArm(-60, -65, -20).body(0, -30, 0).lArm(30, -12, 10))
			.key(0.50f, IN, SINGLE_STANCE.copy()
				.rArm(-60, 60, 25).body(3, 34, 0).lArm(25, -12, -15))
			.key(0.72f, OUT, SINGLE_STANCE.copy()
				.rArm(-58, 75, 28).body(3, 40, 0).lArm(20, -12, -25))
			.end(SMOOTH, SINGLE_STANCE),

		// 3. Đâm kiếm về phía trước
		Strike.from(SINGLE_STANCE)
			.key(0.25f, SMOOTH, SINGLE_STANCE.copy()
				.rArm(-12, -12, 10).rPos(0, 3).body(-4, -22, 0).lArm(35, -12, 10))
			.key(0.48f, IN, SINGLE_STANCE.copy()
				.rArm(-32, -8, 3).rPos(-0.5f, -7f).body(10, 18, 0).lArm(45, -20, 25)
				.legs(-34, 24, 3, -3))
			.key(0.75f, OUT, SINGLE_STANCE.copy()
				.rArm(-36, -8, 3).rPos(-0.5f, -6f).body(8, 14, 0).lArm(45, -20, 25)
				.legs(-30, 20, 3, -3))
			.end(SMOOTH, SINGLE_STANCE),

		// 4. Chém từ trên bổ xuống
		Strike.from(SINGLE_STANCE)
			.key(0.30f, SMOOTH, SINGLE_STANCE.copy()
				.rArm(-150, -8, 14).rPos(-1, -1).body(-10, 6, 0).lArm(-20, -10, 10))
			.key(0.52f, IN, SINGLE_STANCE.copy()
				.rArm(-28, -6, 4).rPos(0, -3).body(22, -4, 0).lArm(30, -12, 15)
				.legs(-30, 20, 3, -3))
			.key(0.76f, OUT, SINGLE_STANCE.copy()
				.rArm(-10, -6, 4).rPos(0, -2).body(26, -4, 0).lArm(30, -12, 15)
				.legs(-26, 18, 3, -3))
			.end(SMOOTH, SINGLE_STANCE)
	};

	// ------------------------------------------------------------------
	// COMBO HAI TAY: tay chính chéo, tay phụ chéo, tay chính ngang, tay phụ ngang, cả hai
	// ------------------------------------------------------------------

	private static final Strike DUAL_DIAG_MAIN = Strike.from(DUAL_STANCE)
		// từ góc trên bên phải ...
		.key(0.25f, SMOOTH, DUAL_STANCE.copy()
			.rArm(-125, 40, 40).body(0, 22, 0).lArm(-46, 34, -4))
		// ... chém xuống góc dưới bên trái
		.key(0.52f, IN, DUAL_STANCE.copy()
			.rArm(-62, -55, -25).body(8, -30, 0).lArm(-40, 24, -8))
		.key(0.74f, OUT, DUAL_STANCE.copy()
			.rArm(-48, -70, -30).body(10, -38, 0).lArm(-40, 24, -8))
		.end(SMOOTH, DUAL_STANCE);

	private static final Strike DUAL_SIDE_MAIN = Strike.from(DUAL_STANCE)
		.key(0.22f, SMOOTH, DUAL_STANCE.copy()
			.rArm(-60, 70, 25).body(0, 30, 0).lArm(-46, 34, -4))
		.key(0.50f, IN, DUAL_STANCE.copy()
			.rArm(-60, -55, -18).body(3, -32, 0).lArm(-40, 26, -6))
		.key(0.72f, OUT, DUAL_STANCE.copy()
			.rArm(-58, -70, -22).body(3, -38, 0).lArm(-40, 26, -6))
		.end(SMOOTH, DUAL_STANCE);

	private static final Strike DUAL_BOTH = Strike.from(DUAL_STANCE)
		// chụm hai vũ khí về phía trước
		.key(0.26f, SMOOTH, DUAL_STANCE.copy()
			.rArm(-78, -42, 4).rPos(0, -3)
			.lArm(-78, 42, -4).lPos(0, -3)
			.body(12, 0, 0))
		// rồi đánh ngang ra hai bên
		.key(0.54f, IN, DUAL_STANCE.copy()
			.rArm(-66, 72, 20).rPos(0, -1)
			.lArm(-66, -72, -20).lPos(0, -1)
			.body(10, 0, 0))
		.key(0.76f, OUT, DUAL_STANCE.copy()
			.rArm(-64, 84, 24)
			.lArm(-64, -84, -24)
			.body(8, 0, 0))
		.end(SMOOTH, DUAL_STANCE);

	public static final Strike[] DUAL = new Strike[] {
		DUAL_DIAG_MAIN,
		DUAL_DIAG_MAIN.mirrored(),
		DUAL_SIDE_MAIN,
		DUAL_SIDE_MAIN.mirrored(),
		DUAL_BOTH
	};
}
