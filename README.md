# AndroMemDumpBeta
Android App for Dumping Android Process Memory

*NB: Not yet functional. Still a work in progress ...*

<h3>Introduction:</h3>
The aim of this app is to collect process memory dumps store them either on disk (SDCard) or transmit them over the network to some remote storage location. Either full process memory or just metdata /features are to be collected.

**Current Features:**
- Listing running processes
    - Android 4.x (All processes)
    - Android 5.x, 6.x (All user processes. SELinux restricts system processes)
    - Android 7.x (Only this app's process :-( ... due to SELinux restrictions on /proc))