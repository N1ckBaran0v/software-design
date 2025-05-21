sleep(5000);
db = db.getSiblingDB('train_tickets_mongo');
db.createUser({
    user: 'train_database_admin',
    pwd: 's3cr3tpassw0rd',
    roles: [{role: 'readWrite', db: 'train_tickets_mongo'}]
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
        'races', 'schedules', 'tickets', 'comments', 'filters'
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
    (function processDatesInSchedule() {
        print("Converting schedule dates...");

        db.schedules.find().forEach(doc => {
            const update = {};

            if (typeof doc.arrival === 'string') {
                update.arrival = new Date(doc.arrival);
            }

            if (typeof doc.departure === 'string') {
                update.departure = new Date(doc.departure);
            }

            if (Object.keys(update).length > 0) {
                db.schedules.updateOne(
                    {_id: doc._id},
                    {$set: update}
                );
            }
        });

        print("Date conversion completed");
    })();
    (function convertDecimals() {
        print("Converting decimal fields...");

        // Для tickets
        db.tickets.find({ "ticket_cost": { $type: "double" } }).forEach(ticket => {
            db.tickets.updateOne(
                { _id: ticket._id },
                { $set: { ticket_cost: NumberDecimal(ticket.ticket_cost) } }
            );
        });

        // Для places
        db.places.find({ "place_cost": { $type: "double" } }).forEach(place => {
            db.places.updateOne(
                { _id: place._id },
                { $set: { place_cost: NumberDecimal(place.place_cost) } }
            );
        });

        print("Decimal conversion completed");
    })();
})();
