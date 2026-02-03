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

## Kurulum
1. Android Studio ile projeyi açın.
2. Gradle senkronizasyonunu tamamlayın.
3. Bir cihaz/emülatör seçip çalıştırın.

## Bildirim İzinleri
- Android 13+ için POST_NOTIFICATIONS izni istenir.
- Android 12+ için kesin alarm iznini uygulama içindeki butondan etkinleştirin.

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
