import "./Page.css";
import { React } from "react";

const Page = ({ pageName, pageLink, pageParagraph, query }) => {
  const viewDescription = () => {
    const terms = query.split(" ");
    const para = pageParagraph
    
    for (let i = 0; i < terms.length; i++) {
      if (para.includes(terms[i])) {
        const parts = pageParagraph.split(terms[i]);
        return (
          <p className="paragraph">
            {parts.map((part, index) => {
              if (index === parts.length - 1) {
                return <span key={index}>{part}</span>;
              }
              return (
                // <React.Fragment key={index}>
                <span key={index}>
                  <span>{part}</span>
                  <strong>{terms[i]}</strong>
                </span>

                // </React.Fragment>
              );
            })}
          </p>
        );
      }
    }
  };
  return (
    <>
      <div className="container">
        <a href={pageLink} className="links" target="_blank" rel="noopener">
          <h3 style={{ margin: 0 }}>{pageName}</h3>
        </a>
        <span className="linkSpan">{pageLink}</span>
        {/* <p className="paragraph">{pageParagraph}</p> */}
        {viewDescription()}
      </div>
    </>
  );
};
export default Page;
