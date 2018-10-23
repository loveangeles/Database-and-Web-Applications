

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class MovieList
 */
@WebServlet("/MovieList_G")
public class MovieList_G extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MovieList_G() {
        super();
        // TODO Auto-generated constructor stub
    }
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String loginUser = "root";
        String loginPasswd = "950213gyl";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        String gen = request.getParameter("genre");
        
        try {
            //Class.forName("org.gjt.mm.mysql.Driver");
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            // Declare our statement
            String query = "SELECT  movies.*,r.rating,GROUP_CONCAT(DISTINCT genres.name) AS ge_n, GROUP_CONCAT(DISTINCT st.name) AS star_n " + 
            		"FROM movies JOIN genres_in_movies on genres_in_movies.movieId=movies.id " + 
            		"JOIN genres on genres_in_movies.genreId=genres.id, " + 
            		"stars AS st, stars_in_movies sim, ratings r " + 
            		"WHERE movies.id=sim.movieId AND movies.id=r.movieId AND sim.starId=st.id AND movies.id in (select movies.id " + 
            		"FROM movies, genres_in_movies gimm, genres genn " + 
            		"WHERE genn.name = ? AND  gimm.movieId=movies.id AND gimm.genreId=genn.id) " + 
            		"GROUP BY movies.id, movies.title, movies.year, movies.director,r.rating ";
            
            PreparedStatement preparedStatement = dbcon.prepareStatement(query);
            preparedStatement.setString(1, gen);
            // Perform the query
            ResultSet rs = preparedStatement.executeQuery();
            JsonArray jsonArray = new JsonArray();
            
            // Iterate through each row of rs
            while (rs.next()) {
               
                String movie_title = rs.getString("title");
                String movie_rating = rs.getString("rating");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                String movie_genres = rs.getString("ge_n");
                String movie_stars = rs.getString("star_n");
                
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_rating", movie_rating);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.addProperty("movie_genres", movie_genres);
                jsonObject.addProperty("movie_stars", movie_stars);
                
                jsonArray.add(jsonObject);
            }
            out.write(jsonArray.toString());

            rs.close();
            preparedStatement.close();
            dbcon.close();
        } catch (Exception e) {
            out.println("<HTML>" + "<HEAD><TITLE>" + "MovieDB: Error" + "</TITLE></HEAD>\n<BODY>"
                    + "<P>SQL error in doGet: " + e.getMessage() + "</P></BODY></HTML>");
            return;
        }
        out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
