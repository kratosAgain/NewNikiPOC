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
import java.util.Stack;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

class parser{
	
	String filename = "";
	BufferedReader br = null;
	
	public parser(String TestFileName){
		filename = TestFileName;
		try{
		br =new BufferedReader(new FileReader(filename));
		}catch(Exception e){
			System.out.println(e);
		}		
	}
	
}