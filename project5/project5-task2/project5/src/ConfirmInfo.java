import java.time.ZoneId;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.*;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.Vector;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
/**
 * Servlet implementation class ConformInfo
 */
@WebServlet("/ConfirmInfo")
public class ConfirmInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ConfirmInfo() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		response.setContentType("application/json");
		
		String first_name = request.getParameter("fn");
		String last_name = request.getParameter("ln");
		String ccid = request.getParameter("ccid");
		String exp_date = request.getParameter("exp_date");
		
		String loginUser = "root";
        String loginPasswd = "team8";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
		
        try
        {
           Class.forName("com.mysql.jdbc.Driver").newInstance();
           Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
String query = "SELECT * FROM creditcards WHERE firstName = ? AND lastName = ? AND id = ? AND expiration = ? ";
           
           PreparedStatement preparestatement = dbcon.prepareStatement(query);
           preparestatement.setString(1, first_name);
           preparestatement.setString(2, last_name);
           preparestatement.setString(3, ccid);
           preparestatement.setString(4, exp_date);
           // Perform the query
           ResultSet rs = preparestatement.executeQuery();
           if (rs.next()){
        	   		
        	   		HttpServletRequest httpRequest = (HttpServletRequest) request;
        	   		User user = (User) httpRequest.getSession().getAttribute("user");
        	   		String user_email= user.getUsername();
        	   		/*get user id*/
        	   		String ID_query="SELECT id FROM customers WHERE email = ? ";
        	   		PreparedStatement preparedstatement2 = dbcon.prepareStatement(ID_query);
        	   		preparedstatement2.setString(1, user_email);
        	   		ResultSet cust_id=preparedstatement2.executeQuery();
        	   		
        	   		if(cust_id.next())
        	   		{
        	   			String customerid = cust_id.getString("id");
        	   		
        	   			/*get date*/
        	   			Date date = new Date();
        	   			LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        	   			int year  = localDate.getYear();
        	   			int month = localDate.getMonthValue();
        	   			int day   = localDate.getDayOfMonth();
            	   		String today=year+"-"+month+"-"+day;
            	   		
        			Vector<CartItem> cart = user.getCart();
        			for(int i=0; i < cart.size(); ++ i) {
        				
        				/*get movie id*/
        				String movie_tt=cart.get(i).getTitle();
        				String movie_query="SELECT id FROM movies WHERE title = ? ";
        				PreparedStatement preparedstatement3= dbcon.prepareStatement(movie_query);
        				preparedstatement3.setString(1, movie_tt);
        				ResultSet movie_id=preparedstatement3.executeQuery();
            	   		if(movie_id.next())
            	   		{
            	   			String movieid = movie_id.getString("id");
            	   		
            	   			String sub_query = "INSERT INTO sales (customerId, movieid, saleDate) VALUES (?,?,?)";
            	   			PreparedStatement preparedstatement4= dbcon.prepareStatement(sub_query);
            	   			preparedstatement4.setString(1, customerid);
            	   			preparedstatement4.setString(2, movieid);
            	   			preparedstatement4.setString(3, today);
            	   			preparedstatement4.executeUpdate();
    						
            	   		}
        			
        				
        			}
        	   		
        	   		}
        	   		
        	   		JsonObject responseJsonObject = new JsonObject();
   				responseJsonObject.addProperty("status", "success");
   				responseJsonObject.addProperty("message", "success");
   				response.getWriter().write(responseJsonObject.toString());
           }
           else {
        	   		JsonObject responseJsonObject = new JsonObject();
        	   		responseJsonObject.addProperty("status", "fail");
   				responseJsonObject.addProperty("message", "provided info is incorrect");
   				response.getWriter().write(responseJsonObject.toString());
           }
           rs.close();
           
           dbcon.close();
         }
     catch (SQLException ex) {
           while (ex != null) {
                 System.out.println ("SQL Exception:  " + ex.getMessage ());
                 ex = ex.getNextException ();
             }  // end while
         }  // end catch SQLException

     catch(java.lang.Exception ex)
         {
             return;
         }	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
