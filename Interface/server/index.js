const express = require("express");
const MongoClient = require("mongodb").MongoClient;
const cors = require("cors");

const app = express();

app.use(cors());
app.use(express.json());

app.listen(3001, () => {
  console.log("The Server is running on port 3001!!");
});

// Connection url
var url = "mongodb+srv://Admin:admin@cluster0.srt79fu.mongodb.net/test";
const client = new MongoClient(url, { useUnifiedTopology: true }); // { useUnifiedTopology: true } removes connection warnings;

const dbName = "MongoDB";

client
  .connect()
  .then(
    console.log("Connected to DB")

    // (client) => client.db(dbName).listCollections().toArray() // Returns a promise that will resolve to the list of the collections
  )
  .catch(() => {
    console.log("Unable to Connect to DB");
  });
//   .then((cols) => console.log("Collections", cols))
//   .finally(() => client.close());

const db = client.db(dbName);
const IndexerCollection = db.collection("IndexerDB");

const doc = IndexerCollection.find();

console.log(
  doc.toArray().then((val) => {
    console.log(val);
  })
);

// console.log(db.databaseName);
