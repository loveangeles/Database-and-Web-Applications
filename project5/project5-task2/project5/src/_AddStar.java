import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


/**
 * Servlet implementation class add_star
 */
@WebServlet("/_AddStar")
public class _AddStar extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public _AddStar() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
        
		response.setContentType("application/json"); 
        PrintWriter out = response.getWriter();
        
        String name = request.getParameter("name");
        String year = request.getParameter("year");
        
        try {
			Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            DataSource ds = (DataSource) envCtx.lookup("jdbc/write");     
            Connection dbcon = ds.getConnection();
            
            dbcon.close();
            JsonArray jsonArray = new JsonArray();
            if(!name.isEmpty() && name !=null) {
            	    String query = "call add_star( ? ,?);";
            	    PreparedStatement statement = dbcon.prepareStatement(query);
                    statement.setString(1, name);
                    if(year.isEmpty() || year==null) {
                    	statement.setInt(2, -1);
                    }else {
                    	statement.setInt(2, Integer.parseInt(year));
                    }
                    
            	    ResultSet rs = statement.executeQuery();
                    
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("msg", "successfully added new star");
                    jsonArray.add(jsonObject);
                    statement.close();
                    rs.close();
            }else {
            	JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("msg", "Please Provide the name of the star");
                jsonArray.add(jsonObject);
            }
            
            // Iterate through each row of rs
           
            out.write(jsonArray.toString());

            
            
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
