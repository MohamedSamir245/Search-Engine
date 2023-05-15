import React from "react";
import Page from "./Page";
//import { data } from "./PagesData.js";
import SearchEngine from "./SearchEngine";
import Navigation from "./Navigation";
import { useState } from "react";

function App() {
  const [indexBegin, setIndexBegin] = useState(0);
  const [querySearch, setQuerySearch] = useState("");
  const [viewed, setViewed] = useState([]);
  const [allPages, setallPages] = useState([]);
  const viewPages = viewed.map(({ pageName, pageLink, pageParagraph }) => {
    return (
      <Page
        pageName={pageName}
        pageLink={pageLink}
        pageParagraph={pageParagraph}
        query={querySearch}
        key={pageName}
      />
    );
  });

  return (
    <>
      <SearchEngine
        setQuerySearch={setQuerySearch}
        setViewed={setViewed}
        setAllPages={setallPages}
        query={querySearch}
      />
      {viewPages}
      <Navigation
        indexBegin={indexBegin}
        setIndexBegin={setIndexBegin}
        viewed={viewed}
        setViewed={setViewed}
        allPages={allPages}
        setAllPages={setallPages}
      />
    </>
  );
}

export default App;
