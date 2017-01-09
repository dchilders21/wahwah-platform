#!/bin/sh

FROM_DIR=/opt/tomcat/webapps/ROOT/content
TO_DIR=/opt/tomcat/webapps/error/content

if [ -d "$FROM_DIR" ]; then
        echo Copying error files
        rm -rf $TO_DIR
        mkdir -p $TO_DIR
        mv $FROM_DIR/error $TO_DIR/
        mv $FROM_DIR/static $TO_DIR/
        echo Error files copy successful
else
        echo Skipping copying error files. $FROM_DIR already removed.
fi
