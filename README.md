# Antivirus

Jednoduchý antivirus s GUI psaný v jazyce JAVA.

Používá svojí vlastní databázi uloženou v souboru database.txt, do které lze ručně přidávat další hrozby přímo z GUI.

Skenovat lze jak jednotlivé soubory, tak i celé složky. Pokud skenovaný soubor není ve virové definici, pošle se hash souboru na server http://virustotal.com ke kontrole.

Při nalezení hrozby se soubory zkopírují a zabalí do karantény (.zip soubor).
