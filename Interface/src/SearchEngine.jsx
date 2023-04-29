import "./SearchEngine.css";
import Axios from "axios";

const SearchEngine = (props) => {
  // const cheerio = require("cheerio");

  // async function getTitle(url) {
  //   try {
  //     const response = await Axios.get(url);
  //     const $ = cheerio.load(response.data);
  //     return $("title").text();
  //   } catch (error) {
  //     console.error(error);
  //     return null;
  //   }
  // }

  const handleSearchButton = async () => {
    await Axios.post("http://localhost:3001/search", {
      query: props.query,
    })
      .then((res) => {
        console.log("here");
        console.log(res);
        // props.setViewed(res.data);
      })
      .catch((error) => {
        console.log(error);
        setTimeout(() => {}, 5000);
      });
  };
  return (
    <form className="searchForm">
      <input
        className="searchQuery"
        onChange={(e) => props.setQuerySearch(e.target.value)}
        type="text"
        placeholder="search for ..."
      />
      <button className="search" onClick={handleSearchButton}>
        search
      </button>
    </form>
  );
};
export default SearchEngine;
