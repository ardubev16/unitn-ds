#!/usr/bin/env bash

set -euo pipefail

if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <path-to-log-file>"
    exit 1
fi
log_file="$1"

gradle run | tee "$log_file"

javac Check.java
java Check "$log_file"
