import "./SearchEngine.css";
import Axios from "axios";
// import api from './api/axiosConfig'
import searchimage from "./images/search.png";

const SearchEngine = (props) => {
//   const https = require("https");
  // const cheerio = require("cheerio");
  // const http = require("http");
  // const { JSDOM } = require("jsdom");




  // const handleSearchButton2 = async () => {
  //   fetch(`http://localhost:8080/api/search/${props.query}`,{mode:'no-cors'}).then((res) => {
  //   console.log(res)
  // });
    
  // }

  const handleSearchButton = async () => {
    await Axios.post("http://localhost:3001/search", {
      query: props.query,
    })
      .then((res) => {
        // console.log("here");
        // console.log(res.data.links);
        // console.log(res.data)
        if (res.data.links) {
          let alldics = [];
          for (let i = 0; i < res.data.links.length; i++) {
            const url = res.data.links[i];
            const title = res.data.titles[i];
            const discription = res.data.descriptions[i];
            // console.log(discription)

            // http
            //   .get(url, (response) => {
            //     let html = "";

            //     response.on("data", (chunk) => {
            //       html += chunk;
            //     });

            //     response.on("end", () => {
            //       const dom = new JSDOM(html);
            //       const pageTitle = dom.window.document.title;
            //       console.log(pageTitle);
            //       title=pageTitle;
            //     });
            //   })
            //   .on("error", (error) => {
            //     console.error(error);
            //   });

            let dic = {
              pageName: title,
              pageLink: url,
              pageParagraph: discription,
            };
            alldics.push(dic);
          }
          props.setViewed(alldics);
        } else {
          props.setViewed([]);
        }
      })
      .catch((error) => {
        console.log(error);
        setTimeout(() => {}, 5000);
      });
    // handleSearchButton2();
  };
  return (
    <div className="searchForm">
      <input
        className="searchQuery"
        id="searchfield"
        onChange={(e) => props.setQuerySearch(e.target.value)}
        type="text"
        placeholder="search for ..."
      ></input>
      <button className="search" onClick={handleSearchButton}>
        <img src={searchimage} alt="search button" id="searchimg" />
      </button>
    </div>
  );
};
export default SearchEngine;
