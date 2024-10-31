CREATE KEYSPACE IF NOT EXISTS chat_app WITH REPLICATION = { 'class' : 'SimpleStrategy',
    'replication_factor': '1'};

CREATE TABLE IF NOT EXISTS chat_app.user (
    userid UUID PRIMARY KEY,
    email TEXT,
    password TEXT,
    date_created_timestamp TIMESTAMP
);

CREATE TABLE IF NOT EXISTS chat_app.message (
    messageid UUID PRIMARY KEY,
    senderid TEXT,
    chatroom UUID,
    message TEXT,
    timestmap TIMESTAMP
);