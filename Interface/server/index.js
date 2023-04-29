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
  .catch((e) => {
    console.log(e);
  });
//   .then((cols) => console.log("Collections", cols))
//   .finally(() => client.close());

const db = client.db(dbName);
const IndexerCollection = db.collection("IndexerDB");
const PhraseCollection = db.collection("phraseSearchingDB");

app.post("/search", (req, res) => {
  const query = req.body.query;
  if (query === "") {
    console.log("Empty Query");

    res.send("Not Found");
    return;
  }

  const doc = IndexerCollection.find({ Word: query });

  var links;

  console.log("Searching....");
  if (doc) {
    doc.toArray().then((val) => {
      if (val.at(0)) {
        links = val.at(0).URLs;

        console.log(links);

        res.send({ links: links });
      } else {
        console.log("Not Found");

        res.send("Not Found");
      }
    });
  }
});
