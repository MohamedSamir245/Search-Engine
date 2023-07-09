import React from "react";
import Page from "./Page";
//import { data } from "./PagesData.js";
import SearchEngine from "./SearchEngine";
import Navigation from "./Navigation";
import { useState } from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Home from "./Home";
import ITSO from "./images/ITSO.png";


function App() {
  const [indexBegin, setIndexBegin] = useState(0);
  const [querySearch, setQuerySearch] = useState("");
  const [viewed, setViewed] = useState([]);
  const [allPages, setallPages] = useState([]);
  const viewPages = viewed.map(
    ({ pageName, pageLink, pageParagraph, importantWord }) => {
      return (
        <Page
          pageName={pageName}
          pageLink={pageLink}
          pageParagraph={pageParagraph}
          importantWord={importantWord}
          query={querySearch}
          key={pageName}
        />
      );
    }
  );

  return (
    <BrowserRouter>
      <Routes>
        <Route
          exact
          path="/"
          element={
            <>
              <div style={{display:"flex", }}>
                <img src={ITSO} alt="ITSO" style={{width:150,height:40,marginTop:30,marginLeft:100}}/>
                <SearchEngine
                  setQuerySearch={setQuerySearch}
                  setViewed={setViewed}
                  setAllPages={setallPages}
                  query={querySearch}
                />
              </div>
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
          }
        ></Route>
        <Route exact path="/search" element={<Home />}></Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
