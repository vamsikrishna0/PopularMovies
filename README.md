# PopularMovies
* Here is a fully functional and colorful android app which I made from scratch for Android Developer Nanodegree program. This app reveals the power of adaptive UI both for phone and tablet devices.
* It queries [TMDB](https://www.themoviedb.org/documentation/api)(The Movie DB) API to retrieve data about most popular movies.
* Main page lists all the movies returned by the query. Details pane for each movie which includes more details like poster, release date,  synopsis etc. An option for viewing both popular and top rated movies.

### Features:
With the app, you can:
* Discover the most popular, the most rated or the highest rated movies
* Save favorite movies locally to view them even when offline
* Watch trailers
* Read reviews

### Note: Steps to make the app work
* Get an API Key(I used version 3 of the Api) from [TMDB](https://www.themoviedb.org/account/signup) website.
* Add the below code to your gradle.properties file.
```
MyTMDBApiKeyV3=<API Key>
```
