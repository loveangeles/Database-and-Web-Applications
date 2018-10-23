

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class _DashBoard
 */
@WebServlet("/_DashBoard")
public class _DashBoard extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public _DashBoard() {
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

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        System.out.println("in backend");
        
        try {
            //Class.forName("org.gjt.mm.mysql.Driver");
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            // Declare our statement
            
            
            String query = "select table_name from information_schema.Tables where table_type = 'BASE TABLE' AND table_schema = 'moviedb'";
            PreparedStatement statement = dbcon.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            
            JsonArray jsonArray = new JsonArray();
            Vector<String> tables = new Vector<String>();
            while (rs.next()) 
                tables.add(rs.getString("table_name"));
            
            for (String table:tables) {
	            query = "SHOW COLUMNS FROM " + table + "";
	            PreparedStatement statement2=dbcon.prepareStatement(query);
	            rs = statement2.executeQuery();
	            String attribute = "";
	            while (rs.next()) 
	        			attribute += rs.getString("Field") + ":" + rs.getString("Type") + ",";
	            JsonObject jsonObject = new JsonObject();
	            jsonObject.addProperty("table", table);
	            jsonObject.addProperty("attributes", attribute);
	            jsonArray.add(jsonObject);
            }
            out.write(jsonArray.toString());
            
            rs.close();
            statement.close();
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
