package projects.pdpb;

import java.sql.*;
import java.io.*;
import java.net.*;
import java.util.*;
import com.fasterxml.jackson.databind.*;

public class Database {
	
	private Connection c = null;
	private Statement stmt = null;
	private ResultSet rs = null;
	
	private String sql, order = "NAME";
	
	private HashSet<String> set = new HashSet<String>();
	private HashMap<Integer, String> category = new HashMap<Integer, String>();
	
	public Database() throws IOException, SQLException {
		
		String temp[] = {"Mathematics", "Selection", "Repetition", "Arrays", "Strings", "ADTs", "Recursion"};
		
		for (int i = 0; i < 7; i++) category.put(1 << i, temp[i]);
		
		try {
	         Class.forName("org.sqlite.JDBC");
	         c = DriverManager.getConnection("jdbc:sqlite:pdpb.db");
      	} catch ( Exception e ) {
	         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	         System.exit(0);
      	}

//		stmt = c.createStatement();
//		sql = "DROP TABLE PROBLEMS";
//		stmt.executeUpdate(sql);
		
		// initiates the database if it doesn't exist
		stmt = c.createStatement();
        sql = "CREATE TABLE IF NOT EXISTS PROBLEMS (URL TEXT PRIMARY KEY NOT NULL UNIQUE, NAME TEXT NOT NULL, POINTS INT NOT NULL, CATEGORIES INT NOT NULL, COMPLETE INT NOT NULL, FAVORITE INT NOT NULL)"; 
        stmt.executeUpdate(sql);
        
        stmt = c.createStatement();
        sql = "SELECT * FROM PROBLEMS"; 
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
        	set.add(rs.getString("URL"));
//        	System.out.println(rs.getString("URL"));
        }
    	
        
		
//		stmt.close();
//		c.close();
        
	}
	
	/**
	 * Checks if url provided is a valid link to a dmoj problem
	 * 
	 * @param url link to the dmoj problem
	 * @return integer representing the validity of the url
	 */
	public int checkValidURL(String url) {
		try{
			url = url.replace("http://", "https://");
			if (url.indexOf("https://") != 0) url = "https://" + url;
			
			if (url.indexOf("https://dmoj.ca/problem/") != 0) throw new Exception();
			
			if (set.contains(url)) {
//				System.out.println("Problem already exists!");
				return -1;
			}

			URL page = new URL(url);
	        HttpURLConnection connection = (HttpURLConnection)page.openConnection();
	        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
	        
	        if (connection.getResponseCode() != 200) {
	        	System.out.println("bruh"); // debug
	        	throw new Exception();
	        }
			
		} catch (Exception e) {
//			System.out.println("Please enter a valid url: ");
			return 0;
		}
		return 1;
	}
	
	/**
	 * Adds a dmoj problem to the database
	 * 
	 * @param url link to the dmoj problem
	 * @return url
	 * @throws Exception
	 */
	public String addProblem(String url) throws Exception {
		
		url = url.replace("http://", "https://");
		if (!url.contains("https://")) url = "https://" + url;
		
		set.add(url);
		
		URL page = new URL("https://dmoj.ca/api/v2/problem/" + url.substring(24));
        HttpURLConnection connection = (HttpURLConnection)page.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        
        InputStream inputStream = connection.getInputStream();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> json = ((Map<String, Object>) ((Map<String, Object>) mapper.readValue(inputStream, Map.class).get("data")).get("object"));
		
		stmt = c.createStatement();
        sql = "INSERT OR IGNORE INTO PROBLEMS (URL, NAME, POINTS, CATEGORIES, COMPLETE, FAVORITE) VALUES ('" + url + "', '" + ((String)json.get("name")).replace("\'", "\'\'") + "', " + (int)Math.round((double)json.get("points")) + ", 0, 0, 0);"; 
        stmt.executeUpdate(sql);
        
        return url;
	}
	
	/**
	 * Updates the bitset of categories of the current dmoj problem
	 * 
	 * @param url link to the dmoj problem
	 * @param bitset integer representing the categories of the current dmoj problem
	 * @throws Exception
	 */
	public void updateCategories(String url, int bitset) throws Exception {
		
		// prob put in gui class or smth
//		int n = Integer.parseInt(scanner.nextLine()), thing = 0;
//		for (int i = 0; i < n; i++) {
//			int bruh = Integer.parseInt(scanner.nextLine());
//			thing |= 1 << (bruh - 1);
//		}
		stmt = c.createStatement();
        sql = "UPDATE PROBLEMS SET CATEGORIES = " + bitset + " WHERE URL = '" + url + "'";
        stmt.executeUpdate(sql);
	}
	
	/**
	 * Marks current problem as complete/incomplete
	 * 
	 * @param name string name of the dmoj problem
	 * @param complete integer representing whether or not the current problem is complete
	 * @throws Exception
	 */
	public void markComplete(String name, int complete) throws Exception {
		stmt = c.createStatement();
        sql = "UPDATE PROBLEMS SET COMPLETE = " + complete + " WHERE NAME = '" + name + "'";
        stmt.executeUpdate(sql);
	}
	
	/**
	 * Adds or removes current problem from favorites
	 * 
	 * @param url string url of the dmoj problem
	 * @param complete integer representing whether or not the current problem is favorite
	 * @throws Exception
	 */
	public void markFavorite(String url, int favorite) throws Exception {
		stmt = c.createStatement();
        sql = "UPDATE PROBLEMS SET FAVORITE = " + favorite + " WHERE URL = '" + url + "'";
        stmt.executeUpdate(sql);
	}
	
	/**
	 * Deletes the problem from the database
	 * 
	 * @param url link to the dmoj problem
	 * @throws Exception
	 */
	public void deleteProblem(String url) throws Exception {
		set.remove(url);
		stmt = c.createStatement();
        sql = "DELETE FROM PROBLEMS WHERE URL = '" + url + "'"; 
        stmt.executeUpdate(sql);
	}
	
	/**
	 * Sets the command for the order to sort the problems in
	 * 
	 * @param sort integer representing the attribute to sort by
	 * @param reverse whether the list should be reversed or not
	 */
	public void sort(int sort, boolean reverse) {
		if (sort == 0) order = "NAME";
		else order = "POINTS";
		if (reverse) order += " DESC";
	}
	
	/**
	 * Converts the bitset into a string of categories
	 * 
	 * @param bitset integer representing the categories of the current problem
	 * @return string of the categories represented by the bitset
	 */
	public String getCategories(int bitset) {
		String categories = "";
		for (int i = 0; i < 7; i++) {
			if ((bitset & (1 << i)) > 0) categories += (categories.length() == 0 ? "" : ", ") + category.get(1 << i);
		}
//		System.out.println(categories);
		return categories;
	}
	
	/**
	 * Returns the complete list of dmoj problems contained in the database
	 * 
	 * @return the complete list of problems in the database
	 * @throws Exception
	 */
	public ResultSet update() throws Exception {
		stmt = c.createStatement();
        sql = "SELECT * FROM PROBLEMS ORDER BY " + order;
        rs = stmt.executeQuery(sql);
//        while (rs.next()) {
//        	System.out.println(rs.getString("URL") + ", " + rs.getString("NAME") + ", " + rs.getInt("POINTS") + ", " + rs.getInt("CATEGORIES"));
//        }
        return rs;
	}
	
	/**
	 * Returns the url of the dmoj problem given by its name
	 * 
	 * @param name string name of the dmoj problem
	 * @return url to the dmoj problem
	 * @throws Exception
	 */
	public String getURL(String name) throws Exception {
		stmt = c.createStatement();
        sql = "SELECT URL FROM PROBLEMS WHERE NAME = '" + name + "'";
        rs = stmt.executeQuery(sql);
        return rs.getString("URL");
	}
	
	/**
	 * Returns the bitset representing the categories of the current dmoj problem
	 * 
	 * @param url link to the dmoj problem
	 * @return integer representing the categories of the current dmoj problem
	 * @throws Exception
	 */
	public int getBitset(String url) throws Exception {
		stmt = c.createStatement();
        sql = "SELECT CATEGORIES FROM PROBLEMS WHERE URL = '" + url + "'";
        rs = stmt.executeQuery(sql);
        return rs.getInt("CATEGORIES");
	}
	
	/**
	 * Returns the integer representing whether the current dmoj problem is a favorite or not
	 * 
	 * @param url link to the dmoj problem
	 * @return integer representing whether the current dmoj problem is a favorite or not
	 * @throws Exception
	 */
	public int getFavorite(String url) throws Exception {
		stmt = c.createStatement();
        sql = "SELECT FAVORITE FROM PROBLEMS WHERE URL = '" + url + "'";
        rs = stmt.executeQuery(sql);
        return rs.getInt("FAVORITE");
	}
	
	/**
	 * Opens the url to the dmoj problem in new chrome tab
	 * 
	 * @param name string name of the dmoj problem
	 * @throws Exception
	 */
	public void openWebpage(String name) throws Exception {
		Runtime.getRuntime().exec(new String[]{"cmd", "/c","start chrome " + getURL(name)});
	}
}
