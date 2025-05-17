db = db.getSiblingDB('train_tickets_mongo');
db.createUser({
    user: 'train_database_admin',
    pwd: 's3cr3tpassw0rd',
    roles: [{ role: 'readWrite', db: 'train_tickets_mongo' }]
});