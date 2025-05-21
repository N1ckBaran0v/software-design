sleep(5000);
db = db.getSiblingDB('train_tickets_mongo');
db.createUser({
    user: 'train_database_admin',
    pwd: 's3cr3tpassw0rd',
    roles: [{ role: 'readWrite', db: 'train_tickets_mongo' }]
});

async function importCollection(collectionName) {
    const filePath = `/data/${collectionName}.json`;
    print(`Processing ${collectionName}...`);
    try {
        const fileContent = fs.readFileSync(filePath, 'utf8');
        const docs = EJSON.parse(fileContent);
        if (docs.length === 0) {
            print(`No documents found in ${filePath}`);
        } else {
            const result = await db[collectionName].insertMany(docs);
            print(`Successfully inserted ${result.insertedCount} documents into ${collectionName}`);
        }
    } catch (e) {
        print(`ERROR importing ${collectionName}: ${e}`);
    }
}

(async function main() {
    const collections = [
        'users', 'railcars', 'places', 'trains',
        'races', 'schedule', 'tickets', 'comments', 'filters'
    ];
    for (const col of collections) {
        const filePath = `/data/${col}.json`;
        if (!fs.existsSync(filePath)) {
            print(`Skipping ${col} - file not found`);
            continue;
        }
        await importCollection(col);
    }
    print("Initialization completed successfully!");
    print(`Total collections: ${db.getCollectionNames().length}`);
})();