#!/bin/bash

#https://github.com/vbabak/docker-mysql-master-slave/blob/master/build.sh

docker-compose up -d --build

until docker exec horizon_db sh -c 'mysql -uroot -proot -e ";"'
do
    echo "Waiting for horizon_db database connection..."
    sleep 4
done

priv_stmt='CREATE USER "replicator"@"%" IDENTIFIED BY "replicator"; GRANT REPLICATION SLAVE ON *.* TO "replicator"@"%"; FLUSH PRIVILEGES;'
docker exec horizon_db sh -c "mysql -uroot -proot -e '$priv_stmt'"

until docker-compose exec horizon_db_replica sh -c 'mysql -uroot -proot -e ";"'
do
    echo "Waiting for horizon_db_replica database connection..."
    sleep 4
done

MS_STATUS=`docker exec horizon_db sh -c 'mysql -uroot -proot -e "SHOW MASTER STATUS"'`
CURRENT_LOG=`echo $MS_STATUS | awk '{print $6}'`
CURRENT_POS=`echo $MS_STATUS | awk '{print $7}'`

start_slave_stmt="CHANGE MASTER TO MASTER_HOST='horizon_db',MASTER_USER='replicator',MASTER_PASSWORD='replicator',MASTER_LOG_FILE='$CURRENT_LOG',MASTER_LOG_POS=$CURRENT_POS,GET_MASTER_PUBLIC_KEY=1; START SLAVE;"
start_slave_cmd='mysql -uroot -proot -e "'
start_slave_cmd+="$start_slave_stmt"
start_slave_cmd+='"'
docker exec horizon_db_replica sh -c "$start_slave_cmd"

docker exec horizon_db_replica sh -c "mysql -uroot -proot -e 'SHOW SLAVE STATUS \G'"
