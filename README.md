# AndroMemDumpBeta
Android App for Dumping Android Process Memory

*NB: Partially functional. Still a work in progress ...*

<h3>Introduction:</h3>
The aim of this app is to collect process memory dumps, and store them either on disk (SDCard), or transmit them over the network to some remote storage location. Either full process memory or just metadata / features are to be collected.

<h4>Current Features:</h4>

- Listing running processes
    - Android 4.x (All processes)
    - Android 5.x (All processes)
    - Android 6.x (All processes)
    - Android 7.x (Only this app's process :-( ... due to new SELinux restrictions on reading `/proc` in Android 7.x)
- Transporting the memdump executable as an asset, extracting it, placing it into the  files directory of the AndroMemDump application and setting the appropriate permissions
- Dumping a the memory space of a given process to the internal memory, SD Card, or remote network location through the _memdump_ executable
