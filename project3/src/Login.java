

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import org.jasypt.util.password.PasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;

/**
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
		System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);
		boolean valid = VerifyUtils.verify(gRecaptchaResponse);
		System.out.println(valid);
		if (!valid) {
			System.out.println("Recaptcha Failed");
		    return;
		}
		System.out.print("Welcome");
		String account = request.getParameter("account");
		System.out.print(account);
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String loginUser = "root";
        String loginPasswd = "950213gyl";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
        
        try
        {
           Class.forName("com.mysql.jdbc.Driver").newInstance();
           Connection dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
           String query;
           Boolean success=false;
           PreparedStatement statement;
           if (account.equals("employee")) {
        	   query = "SELECT * FROM employees WHERE email = ? and password = ?";

   	   		statement = dbcon.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
           }else {
			   query = "SELECT * FROM customers WHERE email = ? ";
			  statement = dbcon.prepareStatement(query);
	           statement.setString(1, username);
	           PasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
	           String encryptedPassword = passwordEncryptor.encryptPassword(password);
	           
	           success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
           }
          
           // Perform the query
           ResultSet rs = statement.executeQuery();
           
           if ((rs.next() || success)){
        	    System.out.println("has account!");
        	   	request.getSession().setAttribute("user", new User(username));
   			JsonObject responseJsonObject = new JsonObject();
   			responseJsonObject.addProperty("status", "success");
   			responseJsonObject.addProperty("account", account);
   			response.getWriter().write(responseJsonObject.toString());
           }else {
        	   	request.getSession().setAttribute("user", new User(username));
        	   	JsonObject responseJsonObject = new JsonObject();
       		responseJsonObject.addProperty("status", "fail");
       		if (account.equals("employee"))
    	   		query =  "SELECT * FROM employees WHERE email = ? ";
   		    else
        	   	query =  "SELECT * FROM customers WHERE email = ? ";
        	   	PreparedStatement statement2=dbcon.prepareStatement(query);
        	   	statement2.setString(1, username);
       			rs = statement2.executeQuery();
        	   	if (! rs.next())
   				responseJsonObject.addProperty("message", "user " + username + " doesn't exist");
   			else
   				responseJsonObject.addProperty("message", "incorrect password");
   			response.getWriter().write(responseJsonObject.toString());
           }
           rs.close();
           statement.close();
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
