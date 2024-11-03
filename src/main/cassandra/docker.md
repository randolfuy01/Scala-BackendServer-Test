*** Running the cassandra db
THe cassandra db is pulled from docker

1. docker pull cassandra:latest
2. docker run --name messenger_database -d -p 9042:9042 cassandra:latest
3. navigate into cassandra folder and run: docker cp schema.cql messenger_database:/schema.cql
4. docker exec -it messenger_database cqlsh -f /schema.cql
5. Verify by running the cql shell (docker exec -it messenger_database cqlsh): USE chat_app; SELECT * FROM user; SELECT * FROM message;
6. Deleting the container: docker stop messenger_database , docker rm messenger_database