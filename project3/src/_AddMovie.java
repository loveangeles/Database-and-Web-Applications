

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
 * Servlet implementation class add_movie
 */
@WebServlet("/_AddMovie")
public class _AddMovie extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public _AddMovie() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		String loginUser = "root";
        String loginPasswd = "950213gyl";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

        response.setContentType("application/json"); // Response mime type

        String title = request.getParameter("title");
        String year = request.getParameter("year");
        String director = request.getParameter("director");
        String star_n = request.getParameter("star_n");
        String genre_n = request.getParameter("genre_n");

        
        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        try {
            //Class.forName("org.gjt.mm.mysql.Driver");
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            // Declare our statement
            
            JsonArray jsonArray = new JsonArray();
          
           
            	
            	if(!title.isEmpty() && !year.isEmpty() && !director.isEmpty() && !star_n.isEmpty() && !genre_n.isEmpty())
            	{
            	    String query = "call add_movie(? , ? , ? , ? , ? )";
                    PreparedStatement statement=dbcon.prepareStatement(query);
                    statement.setString(1, title);
                    statement.setInt(2, Integer.parseInt(year));
                    statement.setString(3, director);
                    statement.setString(4, star_n);
                    statement.setString(5, genre_n);
                    ResultSet rs = statement.executeQuery();
                    
              
                        String msg_from_sql = rs.getString("msgs");
                      
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("msg", "successfully added");
                        jsonArray.add(jsonObject);
                        
  
            	}     	else{
           		 JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("msg", "Please Provide full information to add new info to the moive");
                    jsonArray.add(jsonObject);
           	    }
          
//            		 JsonObject jsonObject2 = new JsonObject();
//                     jsonObject2.addProperty("msg", "update new info");
//                     jsonArray.add(jsonObject2);
//            		String query = "call add_movie('"+title+"',"+year+",'"+director+"','"+star_n+"','"+genre_n+"');";
//                    ResultSet rs = statement.executeQuery(query);
//                    while (rs.next()) {
//                        String msg_from_sql = rs.getString("msgs");
//      
//                        JsonObject jsonObject = new JsonObject();
//                        jsonObject.addProperty("msg", msg_from_sql);
//                        jsonArray.add(jsonObject);
//                    }
//                	rs.close();
 
            		      	
 
            
            // Iterate through each row of rs
           
            out.write(jsonArray.toString());

            
//            statement.close();
            dbcon.close();
        }catch (Exception e) {
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
