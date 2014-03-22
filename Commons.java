package Classes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import javax.xml.parsers.DocumentBuilderFactory;

/**
 * 
 */


/**
 * @author Brandon Bell
 * @version 0.01
 * 3/12/20014
 *
 */
public class Parser {

	/**
	 * @param args
	 * @throws IOException 
	 */
	
	private String website;
	private long fileUpdated;
	private File file;
	HttpResponse resp;
	Context context;
	
	public Parser(Context context, String siteURL){
		website = siteURL;
		this.context = context;
	}
	
	public long lastUpdateLong(){
		return fileUpdated;
	}
	
	public Date lastUpdate(){
		return new Date(fileUpdated);
	}

	public boolean update(){
		int reply = connect();
		Log.d("BRANDON Parser", "Reply: " + reply);
		if(reply == 0){
			read();
		}
		
		if(reply == 0 || reply == 1){
			return true;
		}
		else{
			return false;
		}
	}
	
	/**
	 * @return 
	 * 0 -> File Updated
	 * 1 -> File Is Up-to-date
	 * 2 -> Connection Failed
	 * 3 -> URL is not valid
	 */
	
	private int connect(){
		
		String url = website;
		URL site;
		URLConnection siteConnection;
	
		try {
			site = new URL(website);
			//Open the connection to the website
			try {
				siteConnection = site.openConnection();
				long modified = siteConnection.getLastModified();
				if(fileUpdated == modified){
					return 1; //Returns 1 because file is up-to-date
				}else{
					fileUpdated = modified;
					site = new URL(url); //Set url for web page
					siteConnection = site.openConnection(); //Open the connection to the website
					/*InputStream in = site.openStream();
					file = new File("Menu.xml").getAbsoluteFile();
					FileOutputStream out = new FileOutputStream(file);
					*/
					
					HttpGet uri = new HttpGet(website);    

					DefaultHttpClient client = new DefaultHttpClient();
					resp = client.execute(uri);
					Log.d("BRANDON Parser", "Parser Linked");
					
					

					
					
					
					
					/*
					int read = 0;
					byte[] bytes = new byte[1024];
					while ((read = in.read(bytes)) != -1) {
						out.write(bytes, 0, read);
					}
					out.close();
					*/
					return 0; //Returns 0 because file was updated
				}
			} catch (IOException e) {
				alert("Connecection error. Check internet connection");
				e.printStackTrace();
				return 2;
			} 
			
		} catch (MalformedURLException e1) {
			alert("Connection not possable.");
			e1.printStackTrace();
			return 3;
		} //Set URL for web page
	}
	//send XML to database?
	public void read(){
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		
		
		MenuDbOps mdbo = new MenuDbOps(context);
		mdbo.clear();
		
		try {
			dBuilder = dbFactory.newDocumentBuilder();
				try {
				Document doc = dBuilder.parse(resp.getEntity().getContent());
				NodeList nList = doc.getElementsByTagName("weeklymenu");				
				for (int temp = 0; temp < nList.getLength(); temp++) {					 
					Node nNode = nList.item(temp);			 		 
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {			 
						Element eElement = (Element) nNode;			 
						FoodItem food = new FoodItem(eElement.getAttribute("pkey"), eElement.getAttribute("menudate"), eElement.getAttribute("week"), eElement.getAttribute("day"),
								eElement.getAttribute("dayname"), eElement.getAttribute("meal"), eElement.getAttribute("sort"), eElement.getAttribute("station"),
								eElement.getAttribute("course"), eElement.getAttribute("item_name"), eElement.getAttribute("item_desc"),
								eElement.getAttribute("item_price"), eElement.getAttribute("serv_size"), eElement.getAttribute("veg_type"),
								eElement.getAttribute("bal_type"), eElement.getAttribute("allergens"), eElement.getAttribute("calories"), eElement.getAttribute("fat"),
								eElement.getAttribute("fat_pct_dv"), eElement.getAttribute("calfat"), eElement.getAttribute("satfat"),
								eElement.getAttribute("satfat_pct_dv"), eElement.getAttribute("pufa"), eElement.getAttribute("transfat"), eElement.getAttribute("chol"),
								eElement.getAttribute("chol_pct_dv"), eElement.getAttribute("sodium"), eElement.getAttribute("sodium_pct_dv"),
								eElement.getAttribute("carbo"), eElement.getAttribute("carbo_pct_dv"), eElement.getAttribute("dfib"), eElement.getAttribute("dfib_pct_dv"),
								eElement.getAttribute("sugars"), eElement.getAttribute("protein"), eElement.getAttribute("vita_pct_dv"),
								eElement.getAttribute("vitc_pct_dv"), eElement.getAttribute("calcium_pct_dv"), eElement.getAttribute("iron_pct_dv"),
								eElement.getAttribute("ingredient"));
						mdbo.addFoodItem(food);
						
					}
				}
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				Log.e("BRANDON Parser", "SAXException:" + e);
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.e("BRANDON Parser", "IOException:" + e);
				e.printStackTrace();
			}
			
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			Log.e("BRANDON Parser", "ParserConfigurationException:" + e1);
			e1.printStackTrace();
		}
		
	}
	private static void alert(String massage){
		Log.w("BRANDON Parser", "ALERT:" + massage);
	}

}


