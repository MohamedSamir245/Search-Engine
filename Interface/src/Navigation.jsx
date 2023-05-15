//import { data } from "./PagesData";
import "./Navigation.css";

const Navigation = ({ indexBegin, setIndexBegin, viewed, setViewed ,allPages,setAllPages}) => {
  return (
    <div className="nav">
      {indexBegin !== 0 ? (
        <button
          className="active left btn"
          onClick={() => {
            setViewed(allPages.slice(indexBegin - 10, indexBegin));
            setIndexBegin(indexBegin - 10);
          }}
        >
          {"<<"}
        </button>
      ) : (
        <button className="locked left btn">{"<<"}</button>
      )}
      <p className="num">
        {" "}
        {allPages.length === 0 ? <b>-</b> : Math.trunc(indexBegin / 10) + 1}{" "}
      </p>
      {indexBegin === allPages.length - 10 || allPages.length === 0 || allPages.length <=10 ? (
        <button className="locked right btn">{">>"}</button>
      ) : (
        <button
          className="active right btn"
          onClick={() => {
            setViewed(allPages.slice(indexBegin + 10, indexBegin + 20));
            setIndexBegin(indexBegin + 10);
          }}
        >
          {">>"}
        </button>
      )}
      <div className="total">
        <b>
          Total pages:{" "}
          {allPages.length % 10 === 0 ? allPages.length / 10 : Math.trunc(allPages.length / 10) + 1}
        </b>
      </div>
      <br/>
      <div className="programmers">
        <ul>
          <li>Ismail Shaheen</li>
          <li>Mohamed Samir</li>
          <li>Mahmoud Osama</li>
          <li>Mohamed Taher</li>
        </ul>
      </div>
    </div>
  );
};
export default Navigation;
