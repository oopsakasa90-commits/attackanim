# Third-Person Attack Animation (Fabric 1.21.11)

Mod client-side: thay animation vung tay khi tấn công bằng đòn chém 3 pha
(giơ cao → chém chéo xuống → thu về), kèm xoay thân và bước chân.
Dễ thấy nhất khi bật góc nhìn thứ 3 (F5). Không cần Fabric API.

## Build

Yêu cầu: JDK 21.

1. Tạo project mẫu tại https://fabricmc.net/develop/template (chọn 1.21.11) rồi
   copy thư mục `gradle/`, `gradlew`, `gradlew.bat` của nó vào đây
   (project này chưa kèm Gradle wrapper).
2. Đối chiếu `gradle.properties` (loader, loom, yarn) với trang https://fabricmc.net/develop
   và sửa nếu khác.
3. Chạy `./gradlew build`. File mod nằm ở `build/libs/attackanim-1.0.0.jar`.
4. Bỏ file jar vào thư mục `mods` cùng Fabric Loader cho 1.21.11.

Cách khác: giữ nguyên project mẫu của Fabric và chỉ copy `src/main/java/com/example/attackanim/**`
cùng `attackanim.mixins.json`, rồi khai báo trong `fabric.mod.json` của bạn.

## Cấu hình

File `config/attackanim.json` được tạo tự động:

| Khóa        | Mặc định | Ý nghĩa                         |
|-------------|----------|---------------------------------|
| `enabled`   | `true`   | Bật/tắt animation               |
| `intensity` | `1.0`    | Độ mạnh chuyển động (0.0 - 2.0) |
| `swingDurationMs` | `520` | Thời gian một đòn chém (ms) |
| `bodyTwist` | `true`   | Xoay thân khi chém              |
| `legStep`   | `true`   | Bước chân khi chém              |

## Tùy chỉnh đòn chém

Mọi thứ nằm trong `PlayerEntityModelMixin.java`: các mốc pha (`0.30`, `0.55`)
và biên độ góc của từng pha.

## Lưu ý

- Animation áp dụng cho mọi cú vung tay (đánh, đào, đặt block), vì game dùng chung một tiến trình vung tay.
- Dự án dùng Yarn mappings (đã được xác minh có bản cho 1.21.11). Fabric đã ngừng hỗ trợ chính thức Yarn,
  nên nếu muốn chuyển sang Mojang mappings hãy dùng `./gradlew migrateMappings`.

## Phiên bản 1.2: combo đánh, dáng cầm kiếm, xoay kiếm ở cổ tay

Chỉ áp dụng khi **tay chính cầm kiếm** (đổi từ khóa vũ khí trong `weaponKeywords`).

**Chế độ theo tay phụ**
- Tay phụ trống: combo 4 đòn, tay phụ cong nhẹ để sau lưng.
- Tay phụ cầm item khác (không phải kiếm): combo 4 đòn như trên, tay phụ giữ ở hông sau như cầm bao kiếm.
- Tay phụ cũng cầm kiếm: thủ thế song kiếm và combo 5 đòn.

**Combo một tay** (lặp lại): chém ngang phải→trái, chém ngang trái→phải, đâm thẳng, bổ từ trên xuống.

**Combo song kiếm**: tay chính chém chéo từ trên phải xuống dưới trái, tay phụ chém chéo từ trên trái xuống dưới phải,
tay chính chém ngang phải→trái, tay phụ chém ngang trái→phải, cuối cùng hai tay chụm về trước rồi đánh ngang ra hai bên.
Đòn của tay phụ chỉ là hiệu ứng hình ảnh, sát thương vẫn tính từ tay chính như game gốc.

**Xoay kiếm**: thanh kiếm xoay riêng quanh cổ tay (gập + xoay theo trục cánh tay), nối dài cánh tay lúc chém
để cả cánh tay và lưỡi kiếm quét thành một cung dài, lưỡi kiếm xoay theo chiều chém. Thân đi trước, cánh tay theo sau,
cổ tay chạy muộn nhất cho cú chém mượt. Nếu `latest.log` có dòng `Wrist hook active` nghĩa là phần xoay kiếm đã chạy.

**Tốc độ**: mỗi đòn chạy theo đồng hồ riêng, mặc định 520 ms (game gốc khoảng 300 ms), có đoạn khựng nhẹ lúc trúng đòn.
Đòn bổ nặng và đòn song kiếm cuối còn chậm hơn một chút. Chỉnh bằng `swingDurationMs`.

Combo quay về đòn đầu nếu `comboResetMs` (mặc định 1200 ms) không đánh. Người thuận tay trái được lật gương tự động.

Không kết hợp được với resource pack Fresh Animations (Entity Model Features) vì pack đó thay luôn
animation của người chơi. Chọn dùng một trong hai.

## Ghi chú nguồn tham khảo

Phối hợp thân/tay/chân khi vung được tham khảo từ resource pack Fresh Animations Player Add-on của FreshLX
(chỉ tham khảo ý tưởng, không sao chép tài nguyên). Combo và dáng đứng do dự án này tự viết.
