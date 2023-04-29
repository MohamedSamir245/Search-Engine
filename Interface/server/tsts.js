const MongoClient = require("mongodb").MongoClient;

// Connection url
var url = "mongodb+srv://Admin:admin@cluster0.srt79fu.mongodb.net/test";
const client = new MongoClient(url, { useUnifiedTopology: true }); // { useUnifiedTopology: true } removes connection warnings;

const dbName = "MongoDB";

client
  .connect()
  .then(
    (client) => client.db(dbName).listCollections().toArray() // Returns a promise that will resolve to the list of the collections
  )
  .then((cols) => console.log("Collections", cols))
  .finally(() => client.close());
