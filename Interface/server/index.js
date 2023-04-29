const express = require("express");
const MongoClient = require("mongodb").MongoClient;
const cors = require("cors");
const { ObjectId } = require("mongodb");

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
const searchDB = db.collection("searchDB");
const resultDB = db.collection("resultDB");
const PhraseCollection = db.collection("phraseSearchingDB");

app.post("/search", (req, res) => {
  const query = req.body.query;
  if (query === "") {
    console.log("Empty Query");

    res.send("Not Found");
    return;
  }

  const document = { _id: new ObjectId(), Query: query };

  searchDB
    .insertOne(document, (err, res) => {
      if (err) {
        console.log(err);
      }
    })
    .then(() => {
      console.log("query inserted");
    })
    .then(() => {
      resultDB
        .find()
        .toArray((err, documents) => {
          if (err) throw err;

          return documents;
        })
        .then((documents) => {
          if (documents.length === 0) {
            console.log("No documents found.");
          } else {
            res.send({ links: documents[0].URLs });
            resultDB.deleteMany(documents[0]);
          }
        });
      //   const resultDB = db.collection("resultDB");

      //   const waitForDocument = (callback) => {
      // resultDB.findOne({}, (err, document) => {
      //   if (err) throw err;
      //
      //   if (document) {
      // callback(document);
      //   } else {
      // setTimeout(() => {
      //   waitForDocument(callback);
      // }, 1000);
      //   }
      // });
      //   };

      //   waitForDocument((document) => {
      // console.log(document);
      // client.close();
      //   });
      //   console.log("fififi");

      //   let f = 0;
      //   //   while (f === 0) {
      //   resultDB
      //     .find()
      //     .toArray((err, result) => {
      //       if (err) throw err;

      //       //   setTimeout(() => {
      //       return result;
      //       //   }, 5000);

      //       //   return result;
      //     })
      //     //   eslint-disable-next-line no-loop-func
      //     .then((result) => {
      //       var links;

      //       console.log(result);
      //       if (result.length > 0) {
      //         links = result[0].URLs;
      //         res.send({ links: links });

      //         resultDB.deleteMany(result[0]);
      //         //   f = 1;
      //       }
      //     }, 5000);
      //   }
    });

  //   while (true) {
  //   const doc = resultDB.findOne();

  //   if (doc) {
  // doc.toArray().then((val) => {
  // console.log(doc);
  //   if (val.at(0)) {
  // links = val.at(0).URLs;
  // console.log(val.at(0).URLs);

  // res.send("");
  //   } else {
  // console.log("Not Found");

  // res.send("Not Found");
  //   }
  // });
  //   }
  //   }
  //   const doc = IndexerCollection.find({ Word: query });

  //   var links;

  //   console.log("Searching...");

  //   if (doc) {
  //     doc.toArray().then((val) => {
  //       if (val.at(0)) {
  //         links = val.at(0).URLs;

  //         console.log(links);

  //         res.send({ links: links });
  //       } else {
  //         console.log("Not Found");

  //         res.send("Not Found");
  //       }
  //     });
  //   }
});
