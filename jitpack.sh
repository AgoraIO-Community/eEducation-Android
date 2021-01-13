#!/bin/zsh
commitMsg=$1
releaseTag=$2
if [ -z "$commitMsg" ]
  then
    echo "commitMsg is empty!"
    exit 0
fi
if [ -z "$releaseTag" ]
  then
    echo "releaseTag is empty!"
    exit 0
fi
# shellcheck disable=SC2006
echo "-----push code to `git branch --show-current` branch----"
git commit -a -m commitMsg
git push origin android-sdk:android-sdk
echo "-----push code success----"
echo "-----create new release->$releaseTag----"
# shellcheck disable=SC2086
git tag -a $releaseTag -m "$commitMsg"
# shellcheck disable=SC2086
git push origin $releaseTag
echo "-----create release->$releaseTag success!----"
