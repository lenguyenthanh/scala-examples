#!/bin/sh

REMOTE=$1
REMOTE_DIR="/home/lila-search-ingestor"

echo "Deploy to server $REMOTE:$REMOTE_DIR"

RSYNC_OPTIONS=" \
  --archive \
  --no-o --no-g \
  --force \
  --delete \
  --progress \
  --compress \
  --checksum \
  --verbose \
  --exclude RUNNING_PID \
  --exclude '.git/'"

include="export-studies"
rsync_command="rsync $RSYNC_OPTIONS $include $REMOTE:$REMOTE_DIR"
echo "$rsync_command"
$rsync_command
echo "rsync complete"

echo "Deploy complete"

