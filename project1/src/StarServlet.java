


import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
// this annotation maps this Java Servlet Class to a URL
@WebServlet("/movies")
public class StarServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public StarServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // change this to your own mysql username and password
        String loginUser = "root";
        String loginPasswd = "team8";
        String loginUrl = "jdbc:mysql://18.188.52.177:3306/moviedb";
		
        // set response mime type
        response.setContentType("text/html"); 

        // get the printwriter for writing response
        PrintWriter out = response.getWriter();

        out.println("<html>");
        out.println("<head><title>Fabflix</title></head>");
        
        
        try {
        		Class.forName("com.mysql.jdbc.Driver").newInstance();
        		// create database connection
        		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        		// declare statement
        		Statement statement = connection.createStatement();
        		// prepare query
        		String query = 
        				"SELECT * FROM " +
        				"	((SELECT title, GROUP_CONCAT(gname) as Genress,GROUP_CONCAT(sname) as namess FROM\n " + 
        				"			(SELECT movies.title,movies.year,movies.director,genres.name as gname,stars.name as sname,ratings.rating FROM movies,genres_in_movies,genres,stars_in_movies,stars,ratings\n " + 
        				"									WHERE movies.id = stars_in_movies.movieId AND stars.id = stars_in_movies.starId \n " + 
        				"									AND movies.id = genres_in_movies.movieId AND genres.id = genres_in_movies.genreId \n " + 
        				"									AND movies.id = ratings.movieId \n " + 
        				"			ORDER BY ratings.rating DESC limit 183) AS BIGSB\n " + 
        				"			GROUP BY title) AS TB1\n " + 
        				"        INNER JOIN\n " + 
        				"		(SELECT * FROM movies, ratings\n " + 
        				"		WHERE  movies.id = ratings.movieId ORDER BY ratings.rating  DESC limit 20) AS TB2\n " + 
        				"       ON TB1.title = TB2.title) ";
        
        		
        		//execute query
        		ResultSet resultSet = statement.executeQuery(query);        		
        		out.println("<body>");
        		out.println("<h1><center>MovieDB TOP 20 rated Movies</center></h1>");
        		
        		out.println("<table border align=\"center\">");
        		
        		// add table header row
        		out.println("<tr  align=\"center\">");
        		out.println("<td>Title</td>");
        		out.println("<td>Year</td>");
        		out.println("<td>Director</td>");
        		out.println("<td>Genre</td>");
        		out.println("<td>Stars name</td>");
        		out.println("<td>Rating</td>");
        		out.println("</tr>");
        		
        		// add a row for every star result
        		while (resultSet.next()) {
        			// get a star from result set
        			String movieTitle = resultSet.getString("title");
        			String movieYear = resultSet.getString("year");
        			String movieDirector = resultSet.getString("director");
        			String genreName = resultSet.getString("genress");
        			String starName = resultSet.getString("namess");
        			String movieRating = resultSet.getString("rating");
        			List<String> Genres = new ArrayList<String>(Arrays.asList(genreName.split(","))); 
        			Set<String> xiyiji = new HashSet<>();
        			xiyiji.addAll(Genres);
        			Genres.clear();
        			Genres.addAll(xiyiji);
        			xiyiji.clear();
        			
        			genreName = String.join("<br>", Genres);
        			List<String> Stars = new ArrayList<String>(Arrays.asList(starName.split(","))); 
        			xiyiji.addAll(Stars);
        			Stars.clear();
        			Stars.addAll(xiyiji);
        			starName = String.join("<br>", Stars);
        			out.println("<tr align=\"center\">");
        			out.println("<td>" + movieTitle + "</td>");
        			out.println("<td>" + movieYear + "</td>");
        			out.println("<td>" + movieDirector + "</td>");
        			out.println("<td>" + genreName + "</td>");
        			out.println("<td>" + starName + "</td>");
        			out.println("<td>" + movieRating + "</td>");
        			out.println("</tr>");
        		}
        		
        		out.println("</table>");
        		
        		out.println("</body>");
        		
        		resultSet.close();
        		statement.close();
        		connection.close();
        		
        } catch (Exception e) {
        		/*
        		 * After you deploy the WAR file through tomcat manager webpage,
        		 *   there's no console to see the print messages.
        		 * Tomcat append all the print messages to the file: tomcat_directory/logs/catalina.out
        		 * 
        		 * To view the last n lines (for example, 100 lines) of messages you can use:
        		 *   tail -100 catalina.out
        		 * This can help you debug your program after deploying it on AWS.
        		 */
        		e.printStackTrace();
        		
        		out.println("<body>");
        		out.println("<p>");
        		out.println("Exception in doGet: " + e.getMessage());
        		out.println("</p>");
        		out.print("</body>");
        }
        
        out.println("</html>");
        out.close();
        
	}


}
