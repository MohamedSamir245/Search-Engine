import "./Home.css";
import ITSO from "./images/ITSO.png";
import SearchEngine from "./SearchEngine";
import { useState } from "react";

const Home = () => {
  const [indexBegin, setIndexBegin] = useState(0);
  const [querySearch, setQuerySearch] = useState("");
  const [viewed, setViewed] = useState([]);
  const [allPages, setallPages] = useState([]);

  return (
    <div className="homeContainer">
      <img src={ITSO} className="logo"></img>
      <SearchEngine
        setQuerySearch={setQuerySearch}
        setViewed={setViewed}
        setAllPages={setallPages}
        query={querySearch}
      />
    </div>
  );
};

export default Home;
