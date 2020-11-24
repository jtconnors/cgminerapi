#!/bin/bash

#
# Move to the directory containing this script so we can source the env.sh
# properties that follow
#
cd `dirname $0`

#
# Common properties shared by scripts
#
. env.sh

CGMINERHOST=49er
CGMINERPORT=4028

exec_cmd "target/Samples -cgminerHost:$CGMINERHOST -cgminerPort:$CGMINERPORT"
