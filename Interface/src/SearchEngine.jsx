import "./SearchEngine.css"

const SearchEngine=({setQuerySearch})=>{
    return (
            <form class="searchForm">
                <input class="searchQuery"
                 onChange={(e)=>setQuerySearch(e.target.value)}
                 type="text"
                 placeHolder="search for ..."/>
                <button class="search">search</button> 
            </form>
    );
}
export default SearchEngine;
