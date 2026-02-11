# SmokeFree Coach (Sigarasız Koç)

Tamamen çevrimdışı çalışan sigara bırakma telkin uygulaması. Bildirimler `assets/messages_tr.json` içindeki telkin cümlelerinden planlanır ve DataStore Preferences ile ayarlar saklanır.

## Özellikler
- Minimum SDK 26, Target/Compile SDK 35
- Kotlin + Jetpack Compose (Material3)
- Offline telkin sözleri (`assets/messages_tr.json`)
- DataStore Preferences (günlük bildirim sayısı, başlangıç/bitiş saati, son planlanan tarih, favoriler)
- AlarmManager + `setExactAndAllowWhileIdle`
- Android 12+ için `SCHEDULE_EXACT_ALARM` ayar yönlendirme akışı
- NotificationChannel, bildirim tıklayınca detay sayfası
- AdMob Banner + App Open Ad (test id'leri)
- Room Database tabanlı rozet/ödül/sağlık verileri
- Gamification: kupalar/rozetler, ödül hedefleri, sağlık zaman çizelgesi
- Achievement Engine + WorkManager bildirimleri (offline çalışır)

## Kurulum
1. Android Studio ile projeyi açın.
2. Gradle senkronizasyonunu tamamlayın.
3. Bir cihaz/emülatör seçip çalıştırın.

## Uygulama İkonunu Android Studio "New Image Asset" ile Değiştirme
1. `app` modülünde `res` klasörüne sağ tıklayın.
2. **New > Image Asset** seçin.
3. Icon Type olarak **Launcher Icons (Adaptive and Legacy)** seçin.
4. Kendi görselinizi seçip isimleri `ic_launcher` / `ic_launcher_round` bırakın.
5. Finish dedikten sonra Android Studio mevcut launcher ikon dosyalarını günceller.

Not: Manifest artık `@mipmap/ic_launcher` ve `@mipmap/ic_launcher_round` kullandığı için bu akışla doğrudan ikon değiştirebilirsiniz.

## Bildirim İzinleri
- Android 13+ için POST_NOTIFICATIONS izni istenir.
- Android 12+ için kesin alarm iznini uygulama içindeki butondan etkinleştirin.

## Bilgilendirme Notu
Bu uygulama tıbbi iddia içermez. Sağlık bilgileri yalnızca bilgilendirme amaçlıdır.

## Placeholder Asset Listesi
Uygulamadaki tüm görseller drawable referanslarıdır. Eğer gerçek asset yoksa placeholder vector kullanılır.

- achievement_decision
- achievement_premium
- badge_smoke_20
- badge_smoke_100
- badge_smoke_1000
- badge_smoke_10000
- badge_days_1
- badge_days_3
- badge_days_7
- badge_days_10
- badge_days_14
- badge_days_30
- badge_life_1
- badge_life_3
- badge_life_7
- badge_life_10
- badge_life_14
- badge_life_30
- reward_ticket
- reward_shoes
- reward_gift
- reward_phone
- health_heart
- health_lungs
- health_timer
- health_progress
- mascot_brain

## AdMob Test ID'leri
- Uygulama ID: `ca-app-pub-3940256099942544~3347511713`
- Banner ID: `ca-app-pub-3940256099942544/6300978111`
- App Open ID: `ca-app-pub-3940256099942544/9257395921`

## Release için yapılacaklar
- AdMob gerçek ID'leri ile değiştirin.
- `versionCode` ve `versionName` artırın.
- `proguard-rules.pro` dosyası ile gerekli keep kurallarını ekleyin.
- Play Store için ikonlar ve ekran görüntülerini hazırlayın.

## Zamanlama Mantığı
- N=1 ise bildirim orta noktada planlanır.
- N>1 ise uçlar dahil eşit aralıklı plan yapılır.
- Bitiş <= başlangıç ise hata gösterilir.
