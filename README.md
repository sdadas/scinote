# SciNote
SciNote is a personal bibliography manager and paper recommendation engine.
It is a web based application which utilises several publicly available APIs for searching, indexing, and organizing collections of scientific papers.

[Microsoft Academic Knowledge API](https://www.microsoft.com/en-us/research/project/academic-knowledge/) key is required to run this project since both search and recommendation functionalities use the API. 
Microsoft offers a free quota of 10,000 requests per month which should be enough even for heavy personal use.

### Features
* Creating collections of scientific articles (Projects)
* Advanced search capabilities. Fetching article metadata from the Internet based on arXiv ID, DOI, publication title, or URL. 
* A recommendation engine which suggests new articles for the collection. The algorithm is based on citation statistics - it recommends papers that are either referenced by, or reference articles that are already in the collection.
* Exporting whole collection or specific paper to BibTeX.
* Visualization of citation graph in the collection.
* Possibility to add notes and tags to articles. Searching and filtering papers inside the collection.
* Uploading PDF files and adding them to collection. This is an optional feature which uses  [Science-parse V2 server](https://github.com/allenai/spv2).

### Getting started
1\. Build docker image:
```sh
docker build --tag scinote .
```
2\. Run docker:
```sh
docker run -d -e academic.search.secret=[YOUR_MICROSOFT_API_KEY] -v $(pwd)/data:/root/data -v $(pwd)/files:/root/files -p 8080:8080 -t scinote scinote
```
3\. Open [http://localhost:8080](http://localhost:8080) in the browser.
