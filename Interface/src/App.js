import React from 'react'
import Page from './Page';
import {data} from "./PagesData.js";
import SearchEngine from "./SearchEngine";
import Navigation from './Navigation';
import { useState  } from 'react';

function App() {
  const[indexBegin,setIndexBegin]=useState(0);
  const [querySearch,setQuerySearch]=useState("");
  const [viewed,setViewed]=useState(data.slice(0,10));
  const viewPages=viewed.map(({pageName,pageLink,pageParagraph})=>{
      return(<Page pageName={pageName} pageLink={pageLink} pageParagraph={pageParagraph} />)
    })
 
  return (
    <>
    <SearchEngine setQuerySearch={setQuerySearch}/>
    <div>{querySearch}</div>
    {/* {data.length===0?
    <></>  
    :
    <> */}
    {viewPages}
    <Navigation indexBegin={indexBegin} setIndexBegin={setIndexBegin} viewed={viewed} setViewed={setViewed}/>
    {/* </> 
    } */}
    </>
  )
}

export default App
