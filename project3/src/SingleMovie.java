

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

import com.google.gson.JsonObject;

/**
 * Servlet implementation class SingleMovie
 */
@WebServlet("/SingleMovie")
public class SingleMovie extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SingleMovie() {
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
        String title = request.getParameter("title");
        
        try {
            //Class.forName("org.gjt.mm.mysql.Driver");
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            // Declare our statement
            
            String query = "SELECT movies.*,GROUP_CONCAT(DISTINCT ge.name) AS ge_n, GROUP_CONCAT(DISTINCT st.name) as star_n " +
            		"FROM movies, stars as st, stars_in_movies as sim, genres as ge, genres_in_movies as gim " +
            		"where movies.title = ? AND movies.id=sim.movieId AND sim.starId=st.id " +
            		"AND movies.id = gim.movieId AND gim.genreId=ge.id " +
            		"group by movies.id, movies.title, movies.year, movies.director";
            
            PreparedStatement preparedstatement = dbcon.prepareStatement(query);
            preparedstatement.setString(1, title);
            // Perform the query
            ResultSet rs = preparedstatement.executeQuery();
            
            System.out.println(title);
            // Iterate through each row of rs
            rs.next();
            String movie_title = rs.getString("title");
            String movie_year = rs.getString("year");
            String movie_director = rs.getString("director");
            String movie_genre = rs.getString("ge_n");
            String movie_stars = rs.getString("star_n");
                
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("movie_title", movie_title);
            jsonObject.addProperty("movie_year", movie_year);
            jsonObject.addProperty("movie_director", movie_director);
            jsonObject.addProperty("movie_genre", movie_genre);
            jsonObject.addProperty("movie_stars", movie_stars);

            out.write(jsonObject.toString());

            rs.close();
            preparedstatement.close();
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
