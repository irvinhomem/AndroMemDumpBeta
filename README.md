# AndroMemDumpBeta
Android App for Dumping Android Process Memory

*NB: Not yet functional. Still a work in progress ...*

<h3>Introduction:</h3>
The aim of this app is to collect process memory dumps, and store them either on disk (SDCard), or transmit them over the network to some remote storage location. Either full process memory or just metadata / features are to be collected.

**Current Features:**
- Listing running processes
    - Android 4.x (All processes)
    - Android 5.x (All processes)
    - Android 6.x (All processes)
    - Android 7.x (Only this app's process :-( ... due to new SELinux restrictions on reading `/proc` in Android 7.x)