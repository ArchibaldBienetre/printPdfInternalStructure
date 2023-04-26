#!/bin/bash

set -euo pipefail
IFS=$'\n\t'

if (( "$#" < 1 )); then
  ./gradlew :runWithJavaExec
else
  # TODO Unsolved: how to deal with spaces in input - "$@" works in shell, but not here
  ./gradlew :runWithJavaExec --args "$@"
fi
