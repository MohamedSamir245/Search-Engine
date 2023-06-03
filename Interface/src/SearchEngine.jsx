import "./SearchEngine.css";
import Axios from "axios";
// import api from './api/axiosConfig'
import searchimage from "./images/search.png";
// import arr from "./SearchedQueriesData.js";
import { useState } from "react";
// import { Autocomplete } from "@material-ui/lab";
// import { TextField } from "@material-ui/core";
import Select from "react-select";

const SearchEngine = (props) => {
  const [value, setValue] = useState("");

  const [suggestions, setSuggestions] = useState([]);

  const handleSuggestions = async () => {
    await Axios.post("http://localhost:3001/suggestions", {
      query: props.query,
    }).then((res) => {
      // console.log(res.data.suggestions)
      if (res.data.suggestions) setSuggestions(res.data.suggestions);
    });
  };

  const handleSearchButton = async (val) => {
    // console.log(value);
    await Axios.post("http://localhost:3001/search", {
      query: val,
    })
      .then((res) => {
        // console.log(props.query)
        // console.log("here");
        // console.log(res.data.links);
        // console.log(res.data)
        if (res.data.links) {
          const time = res.data.time;

          console.log(time);
          let alldics = [];
          for (let i = 0; i < res.data.links.length; i++) {
            const url = res.data.links[i];
            const title = res.data.titles[i];
            const discription = res.data.descriptions[i];
            const importantWord = res.data.importantWords[i];


            let dic = {
              pageName: title,
              pageLink: url,
              pageParagraph: discription,
              importantWord: importantWord,
            };
            alldics.push(dic);
          }
          props.setAllPages(alldics);
          props.setViewed(alldics.slice(0, 10));
          // console.log(alldics.length)
        } else {
          props.setAllPages([]);
          props.setViewed([]);
        }
      })
      .catch((error) => {
        console.log(error);
        setTimeout(() => {}, 5000);
      });
    // handleSearchButton2();
    // setValue(props.query)
  };

  function handleChange(selectedOption) {
    setValue(selectedOption.label);
  }

  return (
    <div className="searchForm">
      <Select
        options={suggestions}
        className="searchQueryList"
        onInputChange={(inputValue) => {
          if (inputValue !== "") {
            setValue(inputValue);
            props.setQuerySearch(value);
            // console.log(inputValue);
            handleSuggestions();
          } else {
            // setValue(inputValue);
            // setValue("")
            props.setQuerySearch(value);
            setSuggestions([]);
          }
        }}
        onChange={handleChange}
        value={{ label: value }}
        placeholder="Search"
      />
      <button
        className="search"
        onClick={() => {
          handleSearchButton(value);
        }}
      >
        <img src={searchimage} alt="search button" id="searchimg" />
      </button>
    </div>
  );
};
export default SearchEngine;
