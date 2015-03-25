about ***.DFS scripting**:

DFS (DroidFlasherScript) is simple way for batch job with fastboot, adb, or mfastboot. 
*.dfs is plaint text file with simple structure, how it's work?
- press "run *.dfs" button and select dfs file
- select working directory (where application will search files), in next window.
- wait while all jobs done.

next example show revert to stock *.dfs file for moto x (plaint text file - xt1052revery.dfs. you can choose *.txt file as well):
~~~
dfs set workdir
fastboot oem fb_mode_set
fastboot flash partition gpt.bin
fastboot flash motoboot motoboot.img
fastboot flash logo logo.bin
fastboot flash boot boot.img
fastboot flash recovery recovery.img
mfastboot flash system system.img
fastboot flash modem NON-HLOS.bin
fastboot erase modemst1
fastboot erase modemst2
fastboot flash fsg fsg.mbn
fastboot erase cache
fastboot erase userdata
fastboot erase customize
fastboot erase clogo
fastboot oem fb_mode_clear
~~~
Just save this as text file, and run from application.

Next example show download and flashing moto x twrp:
~~~
# 1. Download recovery
# 2. Reboot to fastboot
# 3. Flash downloaded recovery

dfs download "http://files.z-lab.me/mobile/Moto X/recovery/openrecovery-twrp-2.8.5.0-ghost.img"
adb reboot bootloader
fastboot flash recovery openrecovery-twrp-2.8.5.0-ghost.img
~~~

On last stage of this dfs, you can use only name or full path to img.

You can added any adb commands, next show reboot to fastboot, before run job:
~~~
adb reboot bootloader
fastboot flash boot boot.img
fastboot flash recovery recovery.img
mfastboot flash system system.img
~~~


or you can do any other work:
~~~
adb devices -l
adb reboot bootloader
fastboot devices
~~~


also any other (fastboot/mfastboot/adb) command work, you can use it for batch work:
~~~
adb push /local /remote
fastboot oem unlock
~~~


Also for:
- "fastboot flash /local/file"
- "fastboot boot /local/file"
- "adb sideload /local/file"
- "adb push /local /remote"
- "adb pull /remote /local"

You can use in dfs file absolute path for local file or just name in working directory (DroidFlasher check if file exist with abs path, if not, will use file with this name from working directory.
For example, next code will show correctly *.dfs file, and work without issue:
~~~
fastboot flash boot boot.img
fastboot flash system /home/zorg/files/system.img
adb push my.apk /sdcard
adb push /home/zorg/downloads/mynext.apk /sdcard
~~~
boot.img and my.apk will used from working directory, system.img and mynext.apk with abs path.

I think to extend dfs scripting in feature:
~~~
dfs radiobox TWRP-2.8.5.0|PhilZ-Touch-6.58.7
fastboot flash RESULTS
dfs checkbox data|cache|dalvik
adb twrp wipe RESULTS
~~~
Also I wont to make DFS repository, for various *.dfs  files, reverting to stock for "all" device, flash cm12, and more, all with 1 click.
