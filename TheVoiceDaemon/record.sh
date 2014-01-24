#! /bin/bash

set +x

NAME="/home/netlab/thevoice_project/tempRecording/$1$(date +'%m-%d-%y_%H.%M')"
ffmpeg -f alsa -ac 2 -i pulse -f x11grab -r 30 -s $(xwininfo -root | grep 'geometry'| awk '{print $2;}') -i :0.0 -acodec pcm_s16le -vcodec libx264 -preset ultrafast -crf 0 -y $NAME.mkv > ffmpeg.log 2>&1
set -x


#set +x
#NAME="$1$(date +'%m%d%H%M')"
#ffmpeg -f alsa -ac 2 -i pulse -f x11grab -r 30 -s $(xwininfo -root | grep 'geometry'| awk '{print $2;}') -i :0.0 -acodec pcm_s16le -vcodec libx264 -vpre lossless_ultrafast -threads 0 -y $NAME.mkv > ffmpeg.log 2>&1
#set -x
