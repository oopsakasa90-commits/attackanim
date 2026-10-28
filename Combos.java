package com.example.attackanim.anim;

import static com.example.attackanim.anim.Strike.IN;
import static com.example.attackanim.anim.Strike.LINEAR;
import static com.example.attackanim.anim.Strike.OUT;
import static com.example.attackanim.anim.Strike.SMOOTH;

/**
 * Toàn bộ dáng đứng và combo. Mọi tư thế viết cho người thuận tay PHẢI
 * (tay chính = tay phải), người thuận tay trái sẽ được lật gương tự động.
 *
 * Nhịp của mỗi đòn (t = 0..1): 0.00-0.28 lấy đà, 0.28-0.50 chém nhanh, 0.50-0.57 khựng lúc trúng đòn,
 * 0.57-0.78 follow-through, 0.78-1.00 thu về dáng đứng.
 * Cổ tay: flex 0 = kiếm vuông góc cánh tay (cầm bình thường), flex +90 = kiếm nối dài cánh tay
 * (dùng lúc chém để cả cánh tay + lưỡi kiếm quét thành một cung dài); twist xoay lưỡi theo hướng chém.
 */
public final class Combos {
	private Combos() {}

	// ------------------------------------------------------------------
	// DÁNG ĐỨNG (khi không đánh)
	// ------------------------------------------------------------------

	/** Chỉ cầm kiếm tay chính, tay phụ trống: kiếm nghiêng lên chĩa về trước, tay phụ cong nhẹ để sau lưng. */
	public static final Pose SINGLE_STANCE = new Pose()
		.rArm(-30, -14, 8).rPos(0, -1).rWrist(-5, 0)
		.lArm(36, -12, 20)
		.body(3, -10, 0)
		.legs(-12, 10, 3, -3);

	/** Tay phụ cầm item khác (không phải vũ khí): tay cong nhẹ đặt ở hông sau như đang giữ bao kiếm. */
	public static final Pose SCABBARD_STANCE = SINGLE_STANCE.copy()
		.lArm(30, -8, 9).lWrist(0, 0);

	/** Cầm 2 kiếm: hai tay đưa ra trước, mũi kiếm chéo nhau như thủ thế song kiếm, người hơi chồm, chân dang rộng. */
	public static final Pose DUAL_STANCE = new Pose()
		.rArm(-45, -30, 4).rPos(0, -1.5f).rWrist(-10, 0)
		.lArm(-45, 30, -4).lPos(0, -1.5f).lWrist(-10, 0)
		.body(6, 0, 0)
		.legs(-6, -6, 8, -8);

	// ------------------------------------------------------------------
	// COMBO MỘT TAY: ngang P->T, ngang T->P, đâm, chém từ trên xuống
	// (tay phụ chuyển động dựa trên dáng đứng của nó nên dùng chung cho tay trống / cầm bao kiếm)
	// ------------------------------------------------------------------

	private static Strike[] single(Pose st) {
		return new Strike[] {
			// 1. Chém ngang từ phải sang trái
			Strike.from(st)
				.key(0.28f, SMOOTH, st.copy().rArm(-75, 65, 15).rWrist(50, -62).body(0, 34, 0).lAdd(-6, 0, -10))
				.key(0.50f, IN, st.copy().rArm(-80, -60, -10).rWrist(88, -71).body(3, -30, 0).lAdd(-11, 0, -35)
					.legs(-28, 18, 3, -3))
				.key(0.57f, LINEAR, st.copy().rArm(-80, -66, -12).rWrist(88, -75).body(3, -33, 0).lAdd(-12, 0, -38)
					.legs(-28, 18, 3, -3))
				.key(0.78f, OUT, st.copy().rArm(-72, -82, -18).rWrist(70, -80).body(3, -38, 0).lAdd(-16, 0, -45)
					.legs(-24, 16, 3, -3))
				.end(SMOOTH, st),

			// 2. Chém ngang từ trái sang phải
			Strike.from(st)
				.key(0.28f, SMOOTH, st.copy().rArm(-75, -62, -12).rWrist(50, 72).body(0, -34, 0).lAdd(-6, 0, -10))
				.key(0.50f, IN, st.copy().rArm(-80, 62, 14).rWrist(88, 82).body(3, 34, 0).lAdd(-11, 0, -35)
					.legs(-28, 18, 3, -3))
				.key(0.57f, LINEAR, st.copy().rArm(-80, 68, 16).rWrist(88, 85).body(3, 37, 0).lAdd(-12, 0, -38)
					.legs(-28, 18, 3, -3))
				.key(0.78f, OUT, st.copy().rArm(-72, 84, 22).rWrist(70, 88).body(3, 40, 0).lAdd(-16, 0, -45)
					.legs(-24, 16, 3, -3))
				.end(SMOOTH, st),

			// 3. Đâm kiếm về phía trước
			Strike.from(st).duration(0.85f)
				.key(0.30f, SMOOTH, st.copy().rArm(10, -10, 8).rPos(0, 3).rWrist(-10, 0).body(-4, -22, 0)
					.lAdd(-1, 0, -10))
				.key(0.50f, IN, st.copy().rArm(-60, -8, 3).rPos(-0.5f, -7f).rWrist(60, -10).body(10, 18, 0)
					.lAdd(9, -8, 5).legs(-34, 24, 3, -3))
				.key(0.58f, LINEAR, st.copy().rArm(-62, -8, 3).rPos(-0.5f, -7.5f).rWrist(62, -10).body(11, 18, 0)
					.lAdd(9, -8, 5).legs(-34, 24, 3, -3))
				.key(0.80f, OUT, st.copy().rArm(-55, -8, 3).rPos(-0.5f, -6f).rWrist(55, -5).body(8, 14, 0)
					.lAdd(9, -8, 5).legs(-30, 20, 3, -3))
				.end(SMOOTH, st),

			// 4. Chém từ trên bổ xuống (đòn nặng, chậm hơn)
			Strike.from(st).duration(1.2f)
				.key(0.34f, SMOOTH, st.copy().rArm(-155, -6, 10).rPos(-1, -1).rWrist(55, 0).body(-10, 6, 0)
					.lAdd(-56, 2, -10))
				.key(0.54f, IN, st.copy().rArm(-55, -6, 4).rPos(0, -2).rWrist(55, 0).body(22, -4, 0)
					.lAdd(-6, 0, -5).legs(-30, 20, 3, -3))
				.key(0.62f, LINEAR, st.copy().rArm(-50, -6, 4).rPos(0, -2).rWrist(52, 0).body(24, -4, 0)
					.lAdd(-6, 0, -5).legs(-30, 20, 3, -3))
				.key(0.80f, OUT, st.copy().rArm(-35, -6, 4).rPos(0, -2).rWrist(48, 0).body(26, -4, 0)
					.lAdd(-6, 0, -5).legs(-26, 18, 3, -3))
				.end(SMOOTH, st)
		};
	}

	/** Combo một tay khi tay phụ trống. */
	public static final Strike[] SINGLE = single(SINGLE_STANCE);

	/** Combo một tay khi tay phụ cầm item không phải vũ khí (tay phụ giữ ở hông sau như cầm bao kiếm). */
	public static final Strike[] SCABBARD = single(SCABBARD_STANCE);

	// ------------------------------------------------------------------
	// COMBO SONG KIẾM (cả hai tay cầm kiếm): chéo chính, chéo phụ, ngang chính, ngang phụ, cả hai
	// ------------------------------------------------------------------

	private static final Strike DUAL_DIAG_MAIN = Strike.from(DUAL_STANCE).duration(1.1f)
		// từ góc trên bên phải ...
		.key(0.28f, SMOOTH, DUAL_STANCE.copy().rArm(-130, 40, 38).rWrist(55, 36).body(0, 24, 0).lArm(-46, 34, -4))
		// ... chém xuống góc dưới bên trái
		.key(0.50f, IN, DUAL_STANCE.copy().rArm(-62, -52, -25).rWrist(80, 41).body(8, -30, 0).lArm(-40, 24, -8))
		.key(0.57f, LINEAR, DUAL_STANCE.copy().rArm(-60, -58, -27).rWrist(80, 44).body(9, -33, 0).lArm(-40, 24, -8))
		.key(0.78f, OUT, DUAL_STANCE.copy().rArm(-48, -70, -30).rWrist(65, 48).body(10, -38, 0).lArm(-40, 24, -8))
		.end(SMOOTH, DUAL_STANCE);

	private static final Strike DUAL_SIDE_MAIN = Strike.from(DUAL_STANCE)
		.key(0.28f, SMOOTH, DUAL_STANCE.copy().rArm(-75, 65, 15).rWrist(50, -62).body(0, 34, 0).lArm(-46, 34, -4))
		.key(0.50f, IN, DUAL_STANCE.copy().rArm(-80, -60, -10).rWrist(88, -71).body(3, -30, 0).lArm(-40, 26, -6))
		.key(0.57f, LINEAR, DUAL_STANCE.copy().rArm(-80, -66, -12).rWrist(88, -75).body(3, -33, 0).lArm(-40, 26, -6))
		.key(0.78f, OUT, DUAL_STANCE.copy().rArm(-72, -82, -18).rWrist(70, -80).body(3, -38, 0).lArm(-40, 26, -6))
		.end(SMOOTH, DUAL_STANCE);

	private static final Strike DUAL_BOTH = Strike.from(DUAL_STANCE).duration(1.3f)
		// chụm hai kiếm về phía trước
		.key(0.30f, SMOOTH, DUAL_STANCE.copy()
			.rArm(-80, -40, 4).rPos(0, -3).rWrist(30, -70)
			.lArm(-80, 40, -4).lPos(0, -3).lWrist(30, 70)
			.body(12, 0, 0))
		// rồi đánh ngang ra hai bên
		.key(0.54f, IN, DUAL_STANCE.copy()
			.rArm(-70, 72, 18).rPos(0, -1).rWrist(88, -86)
			.lArm(-70, -72, -18).lPos(0, -1).lWrist(88, 86)
			.body(10, 0, 0))
		.key(0.62f, LINEAR, DUAL_STANCE.copy()
			.rArm(-70, 78, 20).rPos(0, -1).rWrist(88, -88)
			.lArm(-70, -78, -20).lPos(0, -1).lWrist(88, 88)
			.body(10, 0, 0))
		.key(0.80f, OUT, DUAL_STANCE.copy()
			.rArm(-66, 86, 24).rWrist(70, -90)
			.lArm(-66, -86, -24).lWrist(70, 90)
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
