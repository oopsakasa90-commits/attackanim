package com.example.attackanim.anim;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Điều khiển animation đánh của từng người chơi bằng đồng hồ riêng (thời gian thực),
 * nên có thể chạy chậm hơn nhiều so với cú vung tay 0.3 giây của game.
 *
 * Mỗi lần game bắt đầu một cú vung mới (quan sát qua tiến trình vung tay), nếu animation trước
 * đã đi qua phần chính thì bắt đầu đòn kế tiếp trong combo. Quá {@code resetNanos} sau khi đòn
 * trước kết thúc thì combo quay về đòn đầu.
 */
public final class ComboTracker {
	/** Sau mốc này của một đòn thì cho phép nhận đòn kế tiếp (kết thúc follow-through). */
	private static final float ACCEPT_NEXT_AT = 0.72f;

	private static final class State {
		float lastProgress;
		boolean swinging;
		int step = -1;
		boolean playing;
		long start;
		long duration;
		long lastEnd = Long.MIN_VALUE / 2;
		long lastSeen;
	}

	private final Map<Integer, State> states = new HashMap<>();

	/** Kết quả của lần update gần nhất (dùng chung, chỉ gọi từ luồng render). */
	public float phase = -1f;
	public int step = 0;

	/**
	 * @param progress  handSwingProgress hiện tại của game (0 = không vung)
	 * @param combo     danh sách đòn đang dùng (để lấy độ dài từng đòn)
	 * @param baseNanos thời gian chuẩn của một đòn (nano giây)
	 * @return tiến trình animation 0..1 nếu đang chạy, -1 nếu đang nghỉ
	 */
	public float update(int id, float progress, long now, long resetNanos, Strike[] combo, long baseNanos) {
		State s = states.computeIfAbsent(id, k -> new State());
		s.lastSeen = now;

		boolean newSwing = false;
		if (progress > 0f) {
			if (!s.swinging || progress < s.lastProgress - 0.05f) {
				newSwing = true;
			}
			s.swinging = true;
			s.lastProgress = progress;
		} else {
			s.swinging = false;
			s.lastProgress = 0f;
		}

		if (s.playing && now - s.start >= s.duration) {
			s.playing = false;
			s.lastEnd = s.start + s.duration;
		}

		if (newSwing) {
			boolean ready = !s.playing || (now - s.start) >= (long) (s.duration * ACCEPT_NEXT_AT);
			if (ready) {
				if (s.playing) {
					s.lastEnd = now;
				}
				boolean fresh = s.step < 0 || now - s.lastEnd > resetNanos;
				s.step = fresh ? 0 : s.step + 1;
				s.start = now;
				s.duration = (long) (baseNanos * combo[s.step % combo.length].durationScale());
				s.playing = true;
			}
		}

		if (states.size() > 64) {
			prune(now);
		}

		step = Math.max(s.step, 0);
		phase = s.playing ? Math.min(1f, (float) (now - s.start) / (float) s.duration) : -1f;
		return phase;
	}

	private void prune(long now) {
		Iterator<State> it = states.values().iterator();
		while (it.hasNext()) {
			if (now - it.next().lastSeen > 60_000_000_000L) {
				it.remove();
			}
		}
	}
}
