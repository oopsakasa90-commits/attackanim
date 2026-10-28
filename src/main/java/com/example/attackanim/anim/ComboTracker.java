package com.example.attackanim.anim;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Đếm số đòn đánh liên tiếp của từng người chơi bằng cách quan sát tiến trình vung tay mỗi frame.
 * Nếu quá {@code resetNanos} không đánh thì combo bắt đầu lại từ đòn đầu.
 */
public final class ComboTracker {
	private static final class State {
		float lastProgress;
		boolean swinging;
		int step = -1;
		long lastEnd = Long.MIN_VALUE / 2;
		long lastSeen;
	}

	private final Map<Integer, State> states = new HashMap<>();

	/**
	 * @param id       id thực thể
	 * @param progress handSwingProgress hiện tại (0 = không vung)
	 * @return số thứ tự đòn hiện tại trong combo (0, 1, 2, ...), người gọi tự lấy phần dư theo độ dài combo
	 */
	public int update(int id, float progress, long now, long resetNanos) {
		State s = states.computeIfAbsent(id, k -> new State());
		s.lastSeen = now;
		if (progress > 0f) {
			if (!s.swinging || progress < s.lastProgress - 0.05f) {
				if (s.swinging) {
					// đòn trước vừa kết thúc ngay frame này (không có frame nghỉ ở giữa)
					s.lastEnd = now;
				}
				boolean fresh = s.step < 0 || now - s.lastEnd > resetNanos;
				s.step = fresh ? 0 : s.step + 1;
				s.swinging = true;
			}
			s.lastProgress = progress;
		} else {
			if (s.swinging) {
				s.swinging = false;
				s.lastEnd = now;
			}
			s.lastProgress = 0f;
		}
		if (states.size() > 64) {
			prune(now);
		}
		return Math.max(s.step, 0);
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
