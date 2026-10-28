package com.example.attackanim.anim;

/**
 * Một tư thế của cơ thể (đơn vị: góc theo radian, vị trí theo pixel của model).
 *
 * Quy ước (nhân vật thuận tay PHẢI, tay chính = tay phải):
 *  - pitch: âm = vung ra trước/lên, dương = ra sau
 *  - yaw:   dương = quay về bên PHẢI của người chơi, âm = về bên TRÁI
 *  - roll:  tay phải: dương = dang ra ngoài; tay trái: âm = dang ra ngoài
 *  - oy/oz: dịch gốc vai (oz âm = đẩy ra trước, oy âm = nâng lên)
 *
 * Với người thuận tay trái, toàn bộ tư thế được lật gương bằng {@link #mirrored()}.
 */
public final class Pose {
	public static final int RA_P = 0, RA_Y = 1, RA_R = 2, RA_OY = 3, RA_OZ = 4;
	public static final int LA_P = 5, LA_Y = 6, LA_R = 7, LA_OY = 8, LA_OZ = 9;
	public static final int B_P = 10, B_Y = 11, B_R = 12;
	public static final int RL_P = 13, LL_P = 14, RL_R = 15, LL_R = 16;
	public static final int COUNT = 17;

	/** Các chỉ số có đơn vị pixel (không phải góc). */
	public final float[] v = new float[COUNT];

	public Pose() {}

	public Pose(Pose other) {
		System.arraycopy(other.v, 0, this.v, 0, COUNT);
	}

	public Pose copy() {
		return new Pose(this);
	}

	private static float rad(float deg) {
		return (float) Math.toRadians(deg);
	}

	/** Tay phải (độ). */
	public Pose rArm(float pitch, float yaw, float roll) {
		v[RA_P] = rad(pitch);
		v[RA_Y] = rad(yaw);
		v[RA_R] = rad(roll);
		return this;
	}

	/** Tay trái (độ). */
	public Pose lArm(float pitch, float yaw, float roll) {
		v[LA_P] = rad(pitch);
		v[LA_Y] = rad(yaw);
		v[LA_R] = rad(roll);
		return this;
	}

	/** Dịch vai tay phải (pixel). */
	public Pose rPos(float oy, float oz) {
		v[RA_OY] = oy;
		v[RA_OZ] = oz;
		return this;
	}

	/** Dịch vai tay trái (pixel). */
	public Pose lPos(float oy, float oz) {
		v[LA_OY] = oy;
		v[LA_OZ] = oz;
		return this;
	}

	/** Thân người (độ). */
	public Pose body(float pitch, float yaw, float roll) {
		v[B_P] = rad(pitch);
		v[B_Y] = rad(yaw);
		v[B_R] = rad(roll);
		return this;
	}

	/** Chân (độ): pitch chân phải/trái, roll chân phải/trái (dạng chân). */
	public Pose legs(float rPitch, float lPitch, float rRoll, float lRoll) {
		v[RL_P] = rad(rPitch);
		v[LL_P] = rad(lPitch);
		v[RL_R] = rad(rRoll);
		v[LL_R] = rad(lRoll);
		return this;
	}

	/** Lật gương trái-phải (dùng cho người thuận tay trái, hoặc tạo đòn đối xứng). */
	public Pose mirrored() {
		Pose m = new Pose();
		m.v[RA_P] = v[LA_P];
		m.v[RA_Y] = -v[LA_Y];
		m.v[RA_R] = -v[LA_R];
		m.v[RA_OY] = v[LA_OY];
		m.v[RA_OZ] = v[LA_OZ];
		m.v[LA_P] = v[RA_P];
		m.v[LA_Y] = -v[RA_Y];
		m.v[LA_R] = -v[RA_R];
		m.v[LA_OY] = v[RA_OY];
		m.v[LA_OZ] = v[RA_OZ];
		m.v[B_P] = v[B_P];
		m.v[B_Y] = -v[B_Y];
		m.v[B_R] = -v[B_R];
		m.v[RL_P] = v[LL_P];
		m.v[LL_P] = v[RL_P];
		m.v[RL_R] = -v[LL_R];
		m.v[LL_R] = -v[RL_R];
		return m;
	}

	public static void lerp(Pose a, Pose b, float t, Pose out) {
		for (int i = 0; i < COUNT; i++) {
			out.v[i] = a.v[i] + (b.v[i] - a.v[i]) * t;
		}
	}

	/** this = base + (this - base) * k, dùng để chỉnh độ mạnh của chuyển động. */
	public void scaleFrom(Pose base, float k) {
		for (int i = 0; i < COUNT; i++) {
			v[i] = base.v[i] + (v[i] - base.v[i]) * k;
		}
	}
}
