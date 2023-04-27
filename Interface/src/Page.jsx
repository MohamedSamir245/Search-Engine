 import "./Page.css"

const Page=({pageName,pageLink,pageParagraph})=>{
    return(
    <>
    <div class="container">
        <h2>
            {pageName}
        </h2>
        <a href={pageLink}>{pageLink}</a>
        <p class="paragraph">{pageParagraph}</p>
    </div>
    </>
    )
}
export default Page;