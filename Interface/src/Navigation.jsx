//import { data } from "./PagesData";
import "./Navigation.css";

const Navigation = ({ indexBegin, setIndexBegin, viewed, setViewed }) => {
  return (
    <div className="nav">
      {indexBegin !== 0 ? (
        <button
          className="active left btn"
          onClick={() => {
            setViewed(viewed.slice(indexBegin - 10, indexBegin));
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
        {viewed.length === 0 ? <b>-</b> : indexBegin / 10 + 1}{" "}
      </p>
      {indexBegin === viewed.length - 10 || viewed.length === 0 ? (
        <button className="locked right btn">{">>"}</button>
      ) : (
        <button
          className="active right btn"
          onClick={() => {
            setViewed(viewed.slice(indexBegin + 10, indexBegin + 20));
            setIndexBegin(indexBegin + 10);
          }}
        >
          {">>"}
        </button>
      )}
      <div className="total">
        <b>
          Total pages:{" "}
          {viewed.length % 10 === 0 ? viewed.length / 10 : viewed.length / 10 + 1}
        </b>
      </div>
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
