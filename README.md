# Desktop TV Launcher (Android TV & Google TV)

> A modern PC-style desktop launcher interface for Android TV and Google TV, designed specifically for mouse and air-mouse navigation with floating windows, taskbar, start menu, app multitasking, and offline reliability.

---

## 📥 Download APK (डाउनलोड कैसे करें)

### Method A: Direct from this Repository (सीधा डाउनलोड)
The pre-built signed Release and Debug APKs are available directly in the root and `releases/` directory of this repository:
- 🚀 **Official Release APK**: [`DesktopTVLauncher-Release.apk`](DesktopTVLauncher-Release.apk) *(15.8 MB - Recommended)*
- 📦 **Releases Folder**: [`releases/DesktopTVLauncher-Release.apk`](releases/DesktopTVLauncher-Release.apk)

### Method B: GitHub Actions / Releases Tab
1. Open this repository on GitHub in your browser.
2. Go to the **Actions** tab → click the latest workflow run → download **`DesktopTVLauncher-Debug-APK`**.
3. Or check the **Releases** section on the right side of the GitHub repo to get the latest APK.

---

## 📺 How to Install on your Android TV (अपने TV में Install कैसे करें)

Here are the 4 easiest ways to install this APK on any Android TV (Mi TV, Sony Bravia, Realme TV, OnePlus TV, TCL, Chromecast with Google TV, Fire TV, or generic Android TV Box):

---

### तरीका 1: "Send Files to TV" App (सबसे आसान तरीका - Phone से TV में)
1. अपने **Android Mobile** और **Android TV** दोनों में Play Store से **"Send Files to TV"** app install करें।
2. TV पर Play Store से कोई भी File Manager (जैसे **FX File Explorer** या **File Commander**) install कर लें।
3. अपने मोबाइल में `DesktopTVLauncher-v1.0.apk` डाउनलोड करें।
4. Mobile और TV दोनों को **same Wi-Fi** network से connect करें।
5. Phone में *Send Files to TV* खोलें → **Send** पर tap करें → डाउनलोड किया हुआ `.apk` select करें → अपने TV का नाम select करें।
6. File TV में Transfer हो जाएगी। TV पर File Manager खोलकर **Download** folder में जाएँ और APK पर click करके **Install** कर लें!

---

### तरीका 2: Pen Drive / USB Drive से (USB Flash Drive)
1. अपने Computer या Phone में `DesktopTVLauncher-v1.0.apk` को एक **USB Pen Drive** में copy करें।
2. Pen Drive को अपने **Android TV के USB Port** में लगाएँ।
3. TV की स्क्रीन पर USB detected notification आएगी, या TV के File Manager (उदा. File Commander / Total Commander) को खोलें।
4. Pen Drive के अंदर `DesktopTVLauncher-v1.0.apk` पर click करें और **Install** दबाएँ।
   *(अगर "Unknown Sources" का pop-up आए, तो Settings में जाकर उस File Manager के लिए "Allow from this source" enable करें)*।

---

### तरीका 3: "Downloader by AFTVnews" App (सीधा TV के अंदर डाउनलोड)
1. अपने Android TV के Google Play Store पर जाएँ और **Downloader by AFTVnews** app install करें।
2. TV Settings → Security & Restrictions → Unknown Sources में जाकर **Downloader** app को अनुमति (Allow) दें।
3. Downloader app खोलें और इस GitHub repository के raw APK download link को enter करें।
4. डाउनलोड खत्म होते ही screen पर **Install** बटन आएगा, उसपर click करें!

---

### तरीका 4: Wireless ADB (Developer / PC से)
यदि आपके TV में Developer Options और Network Debugging चालू है:
```bash
# 1. अपने TV के IP Address से connect करें (TV Settings -> Network में IP देखें)
adb connect 192.168.1.xxx:5555

# 2. APK install करें
adb install -r releases/DesktopTVLauncher-v1.0.apk
```

---

## ⚙️ TV Settings Note (जरूरी सेटिंग्स)
* Android TV पर किसी भी Third-party APK को install करने के लिए:
  * **Settings** → **Device Preferences** (या **Apps**) → **Security & Restrictions** → **Install Unknown Apps**
  * जिस App (File Manager या Downloader) से install कर रहे हैं, उसे **ON** (Allow) करें।

---

## 🖱️ Navigation Recommendation (उपयोग सुझाव)
यह Launcher खास तौर पर **Mouse Navigation** (USB Mouse, Wireless 2.4GHz Mouse, या Air Remote Gyroscope) के लिए बनाया गया है। सर्वोत्तम अनुभव के लिए अपने TV के USB पोर्ट में एक साधारण USB/Wireless माउस लगाएँ!
