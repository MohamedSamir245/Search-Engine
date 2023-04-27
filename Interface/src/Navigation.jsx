import { data } from "./PagesData";
import "./Navigation.css"

const Navigation=({indexBegin,setIndexBegin,viewed,setViewed})=>{
    return (
        <div class="nav">
            {indexBegin!==0?
                <button class="active left btn"
                onClick={()=>{
                        setViewed(data.slice(indexBegin-10,indexBegin));
                        setIndexBegin(indexBegin-10);}
                    }
                    >{"<<"}</button>
            :
                <button class="locked left btn">{"<<"}</button>
            }
            <p class="num"> {data.length===0? <b>-</b>:indexBegin/10+1} </p>
            {indexBegin===data.length-10 || data.length===0?
                <button class="locked right btn">{">>"}</button>
            :
                <button class="active right btn"
                onClick={()=>{
                    setViewed(data.slice(indexBegin+10,indexBegin+20));
                    setIndexBegin(indexBegin+10)}
                }
                >{">>"}</button>
            }
            <div class="total">
                <b>Total pages: {
            data.length%10===0?
            data.length/10
            :
            data.length/10+1
            }
            </b>
            </div>
            <div class="programmers">
                <ul>
                    <li>Ismail Shaheen</li>
                    <li>Mohamed Samir</li>
                    <li>Mahmoud Osama</li>
                    <li>Mohamed Taher</li>
                </ul>
            </div>
        </div>
    );
}
export default Navigation;