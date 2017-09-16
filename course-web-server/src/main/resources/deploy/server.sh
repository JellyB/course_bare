#!/bin/bash
cd `dirname $0`
ls /mnt/f
case "$1" in
    start)
        ../${server_name}.jar start
    ;;
    stop)
        ../${server_name}.jar stop
    ;;
    restart)
        ../${server_name}.jar restart
    ;;
    restart)
        ../${server_name}.jar restart
    ;;
    dump)
        ./dump.sh
    ;;
    *)
        echo "Usage ${0} <start|stop|restart|dump>"
        exit 1
    ;;
esac
