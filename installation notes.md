Installation af screen recorder systemet
----------------------------------------

#### ffmpeg

Systemet benytter sig af ffmpeg v. 0.11.2 med libx264 og x11grab med følgende configuration:

```bash
ffmpeg version 0.11.2 Copyright (c) 2000-2012 the FFmpeg developers
built on Nov 21 2012 17:49:00 with gcc 4.7.2 
configuration: --prefix=/usr --enable-gpl --enable-libmp3lame 
--enable-libopencore-amrnb --enable-libopencore-amrwb --enable-librtmp 
--enable-libtheora --enable-libvorbis --enable-libvpx --enable-x11grab 
--enable-libx264 --enable-nonfree --enable-version3 
libavutil 51. 54.100 / 51. 54.100 
libavcodec 54. 23.100 / 54. 23.100 
libavformat 54. 6.100 / 54. 6.100 
libavdevice 54. 0.100 / 54. 0.100 
libavfilter 2. 77.100 / 2. 77.100 
libswscale 2. 1.100 / 2. 1.100 
libswresample 0. 15.100 / 0. 15.100 
libpostproc 52. 0.100 / 52. 0.100
```

Med hjælp fra [http://www.adminsehow.com/2009/07/how-to-install-ffmpeg-on-debian-lenny-from-svn/](http://www.adminsehow.com/2009/07/how-to-install-ffmpeg-on-debian-lenny-from-svn/) og [https://trac.ffmpeg.org/wiki/UbuntuCompilationGuide](https://trac.ffmpeg.org/wiki/UbuntuCompilationGuide) hentede og compilede jeg ffmpeg: 

 

```bash
mkdir Downloads 
cd Downloads/ 
git clone --depth 1 git://git.videolan.org/x264 
cd x264/ 
./configure --enable-static 
make 
su cd .. 
git clone --depth 1 git://github.com/mstorsjo/fdk-aac.git 
cd fdk-aac/ 
autoreconf -fiv 
./configure --disable-shared 
make 
su 
cd .. 
git clone --depth 1 http://git.chromium.org/webm/libvpx.git 
cd libvpx/ 
./configure 
make 
su 
cd .. 
wget http://ffmpeg.org/releases/ffmpeg-0.11.2.tar.gz 
tar -zxvf ffmpeg-0.11.2.tar.gz 
cd ffmpeg-0.11.2/ 
ls ./configure --prefix=/usr --enable-gpl --enable-libmp3lame --enable-libopencore-amrnb --enable-libopencore-amrwb --enable-librtmp --enable-libtheora --enable-libvorbis --enable-libvpx --enable-x11grab --enable-libx264 --enable-nonfree --enable-version3 
make 
su
``` 

 

ffmpeg bliver brugt af record scriptet som ser således ud:

```bash
#! /bin/bash 
NAME="/home/netlab/thevoice\_project/tempRecording/\$1\$(date +'%d-%m-%y\_%H.%M')" 
ffmpeg -f alsa -ac 2 -i pulse -f x11grab -r 30 -s \$(xwininfo -root | grep 'geometry'| awk '{print \$2;}') -i :0.0 -acodec pcm\_s16le -vcodec libx264 -preset ultrafast -crf 0 -y \$NAME.mkv \> ffmpeg.log 2\>&1
```
 

#### xdotool

screen recorder systemet benytter sig af [http://www.semicomplete.com/projects/xdotool/](http://www.semicomplete.com/projects/xdotool/) til at bruge mus og tastatur til navigation og lign. Det skal også installeres. Maskinen screen recorder kører på i skrivende stund kører debian og der ligger xdotool i repositoriet.

 

#### Screen recorder

Selve screen recorderen består af to dele. En webapp hvormed al tilgang af den almindelige bruger foregår. Her kan man oprette, ændre og slette scripts og efter afvikling af et script kan man hente sin film ned derfra. En daemon del som står og holder øje med nye scripts og starter dem når tiden er. Dæmonen er single threaded så det øjeblik eet job går igang med at optage blokere det indtil det er færdigt. Der kan jo ligesom kun være bruger på computeren ad gangen, så det er virker acceptabelt.

#### Daemon delen

Dæmonen er rimelig simpel. I klassen Constants peger man på den mappe man vil have tingene til at ligge i. I skrivende stund er det /home/netlab/thevoice\_project/ . De andre variabler i Constants er så mapper der bliver oprettet inde i mappen. Man compiler så koden og sætter den i gang med 

```bash
env DISPLAY=:0 XAUTHORITY=/home/netlab/.Xauthority gnome-terminal -e 
/home/netlab/TheVoiceDaemon/src/run.sh
```

hvor run.sh indeholder

```bash
#!/bin/bash 
set +x 
#RUNNING=\$(ps -ef | grep "src/run.sh" | grep -v "grep" | wc -l) 
echo "Starting Screen Recorder Process..." 
echo "Running..." 
#while [ \$RUNNING -lt 1 ] 
echo "\$DISPLAY" 
echo "Starting daemon on \$DISPLAY" \>\> logging.log 
while [ \$DISPLAY == ":0" ] 
do 
	java Scheduler \>\> logging.log 2\>&1 
	echo "Crashed \$(date)" \>\> logging.log 
done 
set -x
```

Det kan nok godt laves lidt bedre, men det er hvad der kører nu. Det sørger for at vi starter vores dæmon inde i vores X miljø på skærm :0 hvilket gør at vi kan bruge skærm :0 til at optage med. Det er en vigtig detalje at huske. 

record.sh scriptet skal ligge i samme mappe hvorfra dæmonen afvikles. 

Mappen vi angav i Constants.java indeholder så en masse forskellige ting nu. Den indeholder jobs, debug-jobs, de imidlertidige film og de endelig færdige film. Et debug-job er et job hvor de forskellige kommandoer i screen recorder script formatet er blevet udvidet til de endeligt egentlige kommandoer der bliver kaldet til kommando-linjen. Tag f.eks. et klik med musen på koordinat (x,y): det bliver udvidet til "flyt musen til (x,y)" og "klik venstre museknap". Ved at lagre disse kan man se præcis hvad et job gør i hvilken præcis rækkefølge hvis man er i tvivl om noget.

Når record.sh er færdig med at optage bliver filmen flyttet fra tempRecording til finishedJobs. Der er så lavet et symlink fra tomcat serverens webapp mappe til mappen med endelig film. På den måde kan man gennem tomcat serveren både tilgå filmene og servere dem til en bruger.

```bash
netlab@netlab-recorder:/webapps\$ ls -la 
video -\> /home/netlab/thevoice\_project/finishedjobs/
```

 

#### Webapp delen

Koden compiles til en war fil og deployes på en tomcat server. Vores er deployet som ROOT.war og har derfor adgang til mappen webapp/video som ./video. Det er noget der skal ændres hvis den skal deployes anderledes. På vores test server er der kørt med apaches tomcat v. 7.0.33. Man kan nu gå ind på [http://server:](http://server/) port/ og se webappen kører. Når både webappen og dæmonen kører er man parat til at optage et job. 

 

#### Brug af screen recorder

Vi har sat en vnc server op på skærm :1 på vores test maskine. På den måde kan man logge ind på maskinen der skal optage jobbet og browse rundt på hjemmeside lignende i det miljø hvor de bliver optaget. Så får man en følelse for om der mangler et givet plugin (som man måske ikke ville lægge mærke til på sin egen PC hvor det er installeret i forvejen) og hvordan tingene står på skærmen under den givne skærmopløsning. Der skal dog tages højde for at firefox er ikke glad for blive kørt i mere end en instans, så hvis man starter sin VNC adgang lige inden et job kan det godt give nogle problemer. Det er måske noget der skal fikses. Det kan klares med et "killall firefox" inden vi starter firefox gennem dæmonen. 

Der benyttes en simpel java applikation til at vise musekoordinator på skærmen: 

 

 

#### Future work

Vi var igang med at tilføje længde af en given film til oversigten af videoer. Det kan være de kan gives et mere beskrivende navn også.

En anden feature er måske at gøre det muligt at slette film igennem webappen. Lige nu kan det kun gøres gennem ssh eller ved adgang til computeren.

Hvis den skal benyttes med mange jobs over lang tid er det måske også en god ide, at benytte sig af en database til gemme informationer i. Lige nu ligger de i en flad tekst-fil som læses ind hver gang vi opdager en ny (eller manglende) fil i vores jobs mappe.

 

 

 

 

 

 

 

 
