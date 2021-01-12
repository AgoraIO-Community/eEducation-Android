#!/bin/zsh
echo $1
git commit -a -m $1
git push origin android-sdk:android-sdk
