package com.example.attackanim.anim;

import java.util.ArrayList;
import java.util.List;

/** Một đòn đánh: chuỗi keyframe theo tiến trình vung tay t = 0..1. */
public final class Strike {
	public static final int LINEAR = 0;
	/** Chậm - nhanh - chậm (anticipation, thu về). */
	public static final int SMOOTH = 1;
	/** Tăng tốc dần, nhanh nhất lúc tới keyframe (cú chém/đâm). */
	public static final int IN = 2;
	/** Nhanh rồi giảm tốc (follow-through). */
	public static final int OUT = 3;

	private final float[] times;
	private final Pose[] poses;
	private final int[] eases;

	private Strike(float[] times, Pose[] poses, int[] eases) {
		this.times = times;
		this.poses = poses;
		this.eases = eases;
	}

	public static Builder from(Pose start) {
		return new Builder(start);
	}

	public static final class Builder {
		private final List<Float> t = new ArrayList<>();
		private final List<Pose> p = new ArrayList<>();
		private final List<Integer> e = new ArrayList<>();

		private Builder(Pose start) {
			t.add(0f);
			p.add(start.copy());
			e.add(LINEAR);
		}

		/** Keyframe tại thời điểm time; ease áp dụng cho đoạn đi TỚI keyframe này. */
		public Builder key(float time, int ease, Pose pose) {
			t.add(time);
			p.add(pose.copy());
			e.add(ease);
			return this;
		}

		public Strike end(int ease, Pose pose) {
			key(1f, ease, pose);
			float[] ts = new float[t.size()];
			int[] es = new int[t.size()];
			for (int i = 0; i < ts.length; i++) {
				ts[i] = t.get(i);
				es[i] = e.get(i);
			}
			return new Strike(ts, p.toArray(new Pose[0]), es);
		}
	}

	public Strike mirrored() {
		Pose[] mp = new Pose[poses.length];
		for (int i = 0; i < poses.length; i++) {
			mp[i] = poses[i].mirrored();
		}
		return new Strike(times.clone(), mp, eases.clone());
	}

	private static float ease(int type, float u) {
		switch (type) {
			case SMOOTH:
				return u * u * (3f - 2f * u);
			case IN:
				return u * u * u;
			case OUT: {
				float k = 1f - u;
				return 1f - k * k * k;
			}
			default:
				return u;
		}
	}

	/** Lấy tư thế tại tiến trình time (0..1). */
	public void sample(float time, Pose out) {
		if (time <= 0f) {
			System.arraycopy(poses[0].v, 0, out.v, 0, Pose.COUNT);
			return;
		}
		int last = times.length - 1;
		if (time >= 1f) {
			System.arraycopy(poses[last].v, 0, out.v, 0, Pose.COUNT);
			return;
		}
		int i = 1;
		while (i < last && time > times[i]) {
			i++;
		}
		float span = times[i] - times[i - 1];
		float u = span <= 0f ? 1f : (time - times[i - 1]) / span;
		Pose.lerp(poses[i - 1], poses[i], ease(eases[i], u), out);
	}
}
