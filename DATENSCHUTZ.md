# Datenschutzerklärung für die Android-App „Uhrzeit Ansage“

**Stand:** März 2025

Diese Datenschutzerklärung informiert Sie über die Art, den Umfang und den Zweck der Verarbeitung personenbezogener Daten innerhalb der Android-App **„Uhrzeit Ansage – Sprechende Uhr“** (nachfolgend „App“ genannt).

---

## 1. Verantwortlicher

Verantwortlich für die Datenverarbeitung im Sinne der Datenschutz-Grundverordnung (DSGVO) und anderer nationaler Datenschutzgesetze ist:

**Christian Stryi**  
E-Mail: [cstryi@freenet.de](mailto:cstryi@freenet.de)  

---

## 2. Erhebung und Speicherung personenbezogener Daten

### a) Keine Erhebung personenbezogener Daten
Bei der Nutzung der App werden **keinerlei personenbezogene Daten** (wie Name, E-Mail-Adresse, Standortdaten, IP-Adresse oder Geräte-IDs) erhoben, verarbeitet oder an den Entwickler oder Dritte übermittelt.

### b) Keine Internetverbindung / Offline-Betrieb
Die App benötigt **keine Internetberechtigung** (`android.permission.INTERNET`). Alle Funktionen der App arbeiten zu 100 % offline auf Ihrem Endgerät.

### c) Lokale Speicherung der Einstellungen
Sämtliche von Ihnen erstellten Ansageregeln (z. B. Uhrzeiten, Intervalle, Wochentage, benutzerdefinierte Ansagetexte und Lautstärkeeinstellungen) werden ausschließlich **lokal auf Ihrem Gerät** (in den sogenannten `SharedPreferences`) gespeichert. Diese Daten verlassen Ihr Gerät zu keinem Zeitpunkt. Wenn Sie die App deinstallieren, werden alle lokal gespeicherten Einstellungen vollständig und unwiderruflich gelöscht.

---

## 3. Verwendete App-Berechtigungen

Damit die App wie gewünscht funktioniert (automatische Zeitansagen im Hintergrund), benötigt sie bestimmte Android-Systemberechtigungen. Diese werden ausschließlich für die angegebenen technischen Zwecke verwendet:

- **Exakte Alarme (`SCHEDULE_EXACT_ALARM` / `USE_EXACT_ALARM`)**  
  Wird benötigt, um die Sprachausgaben exakt zu den von Ihnen festgelegten Uhrzeiten auszulösen.
- **Nach dem Geräteneustart ausführen (`RECEIVE_BOOT_COMPLETED`)**  
  Wird benötigt, um Ihre konfigurierten Ansage-Regeln nach einem Neustart des Smartphones automatisch wieder zu aktivieren.
- **Benachrichtigungen senden (`POST_NOTIFICATIONS`)**  
  Wird benötigt, um unter Android 13 (API-Level 33) und höher Benachrichtigungen anzeigen zu können.
- **Ruhezustand deaktivieren (`WAKE_LOCK`)**  
  Wird kurzzeitig benötigt, um das Gerät aus dem Standby-Modus aufzuwecken, damit die Sprachausgabe (Text-to-Speech) zum gewünschten Zeitpunkt abgespielt werden kann.

*Hinweis: Keine dieser Berechtigungen wird verwendet, um Daten auszulesen, zu speichern oder zu übertragen.*

---

## 4. Sprachausgabe (Text-to-Speech / TTS)

Für die akustische Zeitansage nutzt die App die auf Ihrem Android-Gerät vorinstallierte Text-to-Speech-Engine (Android-Systemdienst). Die Umwandlung von Text in Sprache erfolgt lokal auf Ihrem Smartphone. Es werden keine Audiodaten an externe Server gesendet.

---

## 5. Weitergabe von Daten an Dritte / Tracking / Werbung

- **Keine Analyse- oder Tracking-Tools:** Die App verwendet keine Analyse-Frameworks (wie Firebase Analytics, Google Analytics etc.).
- **Keine Werbenetzwerke:** Die App enthält keine Werbung und bindet keine Werbe-SDKs ein.
- **Keine Weitergabe an Dritte:** Es finden keinerlei Datenübertragungen an externe Server oder Drittanbieter statt.

---

## 6. Rechte der Nutzerinnen und Nutzer (DSGVO)

Da die App keine personenbezogenen Daten erhebt oder speichert, verbleibt die vollständige Kontrolle über alle Daten bei Ihnen:
- Sie können Ihre Einstellungen jederzeit innerhalb der App bearbeiten oder löschen.
- Durch die Deinstallation der App werden alle lokal abgelegten Anwendungsdaten vollständig entfernt.

Sollten Sie Fragen zum Datenschutz haben, können Sie sich jederzeit per E-Mail an die oben genannte Adresse wenden.

---

## 7. Änderungen dieser Datenschutzerklärung

Der Entwickler behält sich vor, diese Datenschutzerklärung anzupassen, falls sich rechtliche Vorgaben oder die Funktionalitäten der App ändern. Die jeweils aktuelle Version wird in diesem Dokument bereitgestellt.
