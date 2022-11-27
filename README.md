# DroidFlasher

![alt tag](http://habrastorage.org/files/9fc/3b6/9cc/9fc3b69cc2864b939fe4782dfe6740cd.png)

DroidFlasher - Universal cross platform Adb/Fastboot/Recovery (TWRP) tool for any Android devices and any platforms (Windows, Mac, Linux).

DroidFlasher can:
- DroidFlasher Scripting (DFS) - plain-text format for batch job and advanced features, like automatic download and flash zip from the net.
- push file to the device
- pull file from device
- install single or multiple APK
- uninstall package (keep cache option)
- backup / restore (application / settings / cache) via adb
- backup restore partition with TWRP
- flash recovery, kernel, etc, TWRP or Fastboot
- built-in binaries (adb/fastboot/mfastboot) for Mac, PC, *nix.
- work out of the box, just unpack and run (no need setup adb/fastboot).
- more to come


[Download the binaries for Windows and Mac OS X here.](https://firmware.center/projects/DroidFlasher/build/)

p.s.: More detail about DFS also in ./README-DFS.md
----
DFS Scripting video examples: https://www.youtube.com/playlist?list=PLicko3OCiNlWg2yX1YqAA1220ClAkuWjj
----
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

Another advanced example:
~~~
dfs show exit "Recovery flasher" "Warning!" "If you press OK, recovery partition on %DEVICE_MODEL% - %DEVICE% will be rewrited!"
dfs radiobox "TWRP 2.8.6.0|Philz Touch Recovery 6.58.7" "http://files.z-lab.me/mobile/devices/%DEVICE%/twrp-2.8.6.0-%DEVICE%.img|http://files.z-lab.me/mobile/devices/%DEVICE%/philz_touch_6.58.7-%DEVICE%.img" "Recovery flasher" "Аttention!" "Choice preferred recovery:"
dfs download %RADIOBOX_RESULT%
adb reboot bootloader
dfs sleep 1
fastboot flash recovery %RADIOBOX_RESULT_FILENAME%
dfs sleep 2
dfs radiobox "Reboot to the system|Reboot to bootloader" "reboot|reboot-bootloader" "Recovery flasher" "What next?" "Make a choice:"
fastboot %RADIOBOX_RESULT%
~~~

%dialogtype% = info, error,warning, none, confirmation
~~~
dfs radiobox "txt1|txt2|txt3|txt4" "val1|val2|val3|val4" title header content
dfs show %dialogtype% title header content
~~~

~~~
# Recovery flashing from fastboot with choice and confirm dialog
dfs radiobox "TWRP|CWM Recovery|Phiz Touch Recovery" "http://z-lab.me/twrp/recovery.img|http://z-lab.me/cwm/recovery.img|http://z-lab.me/phiz/recovery.img" "Recovery flashing" "Make a choice" "Select preferred recovery:"
dfs download %RADIOBOXRESULT%
dfs show confirmation "FLASHING" "WARNING" "If you press OK, DroidFlasher will flash new recovery from %RADIOBOXRESULT%"
fastboot flash recovery recovery.img
~~~

Also I wont to make DFS repository, for various *.dfs  files, reverting to stock for "all" device, flash cm12, and more, all with 1 click.
