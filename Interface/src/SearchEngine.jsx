import "./SearchEngine.css";
import Axios from "axios";
import searchimage from "./images/search.png";

const SearchEngine = (props) => {
//   const https = require("https");
  const cheerio = require("cheerio");

  const handleSearchButton = async () => {
    await Axios.post("http://localhost:3001/search", {
      query: props.query,
    })
      .then((res) => {
        // console.log("here");
        console.log(res.data.links);
        if (res.data.links) {
          let alldics = [];
          for (let i = 0; i < res.data.links.length; i++) {
            // const url = res.data.links[i];

            // let title;

            let dic = {
              pageName: "title",
              pageLink: res.data.links[i],
              pageParagraph: "jklfads;;klafjdsafjkl;ds",
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
