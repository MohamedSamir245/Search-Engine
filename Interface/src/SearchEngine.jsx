import "./SearchEngine.css";
import Axios from "axios";
import searchimage from "./images/search.png";

const SearchEngine = (props) => {
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
            let dic = {
              pageName: "test",
              pageLink: res.data.links[i],
              pageParagraph: "jklfads;;klafjdsafjkl;ds",
            };
            alldics.push(dic);
          }
          props.setViewed(alldics);
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
