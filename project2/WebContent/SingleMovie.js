function handleStarResult(resultData) {
	console.log("handleSingleMovieResult: populating the movie table from resultData");
	var star_array = resultData["movie_stars"].split(',');
	var genre_array = resultData["movie_genre"].split(',');
	var starHTML = "";
	var genreHTML = "";
	for (var i=0; i<star_array.length; ++i)
		starHTML += "<a href=/project2-login-example/SingleStar.html?name=" + encodeURIComponent(star_array[i]) + ">" + star_array[i] + "</a>, ";
	for (var i=0; i<genre_array.length;++i)
		genreHTML +="<a href=/project2-login-example/MovieList.html?genre=" + encodeURIComponent(genre_array[i]) + "&page=1&limit=25" + ">" + genre_array[i] +"</a>, ";
	// populate the star table
	var starTableBodyElement = jQuery("#single_movie_table_body");	
	var rowHTML = "";
	rowHTML += "<tr>";
	
	rowHTML += "<th>" + resultData["movie_title"] + "</th>";
	rowHTML += "<th>" + resultData["movie_year"] + "</th>";
	rowHTML += "<th>" + resultData["movie_director"] + "</th>";
	rowHTML += "<th>" + genreHTML + "</th>";
	rowHTML += "<th>" + starHTML + "</th>";
	rowHTML += "<th><input type=\"button\" onclick=\"window.location.href='/project2-login-example/ShoppingCart.html?title="+encodeURIComponent(resultData["movie_title"])+"'\" value=\"Add to Cart\">";
	rowHTML += "</tr>";
	
	starTableBodyElement.append(rowHTML);
}


var url_string =  window.location.href;
var url = new URL(url_string);
var title_name = url.searchParams.get("title");
// makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
	  dataType: "json",
	  method: "GET",
	  data: { title: title_name}, 
	  url: "/project2-login-example/SingleMovie",
	  success: (resultData) => handleStarResult(resultData)
});