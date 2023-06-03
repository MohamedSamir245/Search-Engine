const express = require("express");
const MongoClient = require("mongodb").MongoClient;
const cors = require("cors");
const { ObjectId } = require("mongodb");

const { performance } = require("perf_hooks");

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
// const IndexerCollection = db.collection("IndexerDB");
const searchDB = db.collection("searchDB");
const resultDB = db.collection("resultDB");
const queryCollection = db.collection("queryCollection");

// const PhraseCollection = db.collection("phraseSearchingDB");

app.post("/search", (req, res) => {
  const query = req.body.query;
  if (query === "") {
    console.log("Empty Query");

    res.send("Not Found");
    return;
  }

  const document = { _id: new ObjectId(), Query: query };

  searchDB.insertOne(document, (err, res) => {
    if (err) {
      console.log(err);
    }
    console.log("query inserted");
  });

  const startTime = Date.now();
  waitForData(db, function (doc) {});

  function waitForData(db) {
    // console.log("Inside waitForData");

    // Check if there is data in the collection
    resultDB
      .findOne({ Query: query })
      .then((err, doc) => {
        // console.log("Inside Func of findOne");

        if (err) throw err;

        // If there is data, call the callback function
        if (doc) {
          console.log("Data found!");
        } else {
          console.log("No data found. Waiting...");

          // If there is no data, wait for a short time and check again
          setTimeout(function () {
            waitForData(db);
          }, 250);
        }
      })
      .catch((doc) => {
        // console.log("ggggggggg");
        const endTime = Date.now();
        console.log((endTime - startTime) / 1000);

        console.log("Inside Callback");
        //  doc.then((val) => {
        if (doc !== null) {
          res.send({
            links: doc.URLs,
            titles: doc.Titles,
            descriptions: doc.Descriptions,
            time: (endTime - startTime) / 1000,
            importantWords: doc.ImportantWords,
          });
          resultDB.deleteMany(doc);
        } else {
          console.log("Not Found");

          res.send("Not Found");
        }
        //  });
      });
    // console.log(resultDB.findOne({ Query: query }));
  }
});

app.post("/suggestions", (req, res) => {
  const query = req.body.query;
  if (query === "") {
    res.send("Not Found");
    return;
  }
  let suggestions = [];
  queryCollection
    .findOne({})
    .then(function (err, doc) {
      if (err) throw err;
    })
    .catch((doc) => {
      const myArray = Object.entries(doc);

      myArray.sort(function (a, b) {
        return b[1] - a[1];
      });

      const sortedObject = Object.fromEntries(myArray);

      // console.log(Object.keys(sortedObject));

      const keys = Object.keys(sortedObject);
      for (let i = 0; i < keys.length; i++) {
        if (keys[i].includes(query) && keys[i] !== "_id") {
          suggestions.push({ value: keys[i], label: keys[i] });
        }
      }

      res.send({ suggestions: suggestions });
    });
});
