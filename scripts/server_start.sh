##!/usr/bin/env bash
#cd /home/ec2-user/server
#sudo java -jar -Dserver.port=80 \
#    *.jar > /dev/null 2> /dev/null < /dev/null &
#!/bin/bash
source ~/.bashrc
# To kill the process incase of redeployment
kill -9 $(lsof -i :8080)
cd /home/ec2-user/my-web-server/build/libs/
nohup java -jar TravelChat.jar &> webserverlog.log &