import "./Page.css";

const Page = ({ pageName, pageLink, pageParagraph }) => {
  return (
    <>
      <div className="container">
        <h2>{pageName}</h2>
        <a href={pageLink}>{pageLink}</a>
        <p className="paragraph">{pageParagraph}</p>
      </div>
    </>
  );
};
export default Page;
