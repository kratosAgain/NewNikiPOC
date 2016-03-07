package mongopoc;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.DBCursor;

import com.mongodb.ServerAddress;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

class mainClass{
	public static void main(String args[]){
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
		makingmongo database = new makingmongo(mongoClient,args[0],args[1],args[2]);
		database.rollBack();	
		//checkRollBack(mongoClient,args[1]);
	}
	
	public static void checkRollBack(MongoClient client,String databasename){
		DB database = client.getDB(databasename);
		System.out.println("connected to Database");
		DBCollection collection = database.getCollection("Collection3");
		
		BasicDBObject query = new BasicDBObject();
		
		query.put("ID",22);
		DBCursor cursor = collection.find(query);
		//collection.remove(query);
		System.out.println(cursor);
		DBObject obj = cursor.next();
		ArrayList<Integer> siblings = (ArrayList<Integer>) obj.get("siblings");
		for(int sib:siblings){
			System.out.println(sib);
		}
		
		
	}
}
/* document is like this 
 *  {
 *    ID:2	
 *    name: "_c_concept",
 *    type: "concept",
 *    weight:0.6777,
 *    children:[22,24,67],
 *    parents:[2,4,5],
 *    siblings:[54,78,89,34]
 *   }   
 */