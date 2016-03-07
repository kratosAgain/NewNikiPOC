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

public class makingmongo{
	HashMap<String,ArrayList<Integer>> parentMap = new HashMap();  //HashMap to keep parents mapping weight in format [element]=[name#weight,..,.];
	HashMap<String,ArrayList<Integer>> childMap = new HashMap();	//HashMap to keep children mapping in format [element] = [name1,name2,..]
	ArrayList<ArrayList<Integer>> siblings = new ArrayList<ArrayList<Integer>>(); //List of Siblings
	HashMap<String,Double> weightMap = new HashMap(); //Map for weight
	HashMap<String,Integer> idMap = new HashMap(); //giving elements ID
	int idCount=0;
	Stack<node> nodeStack = new Stack();

	public makingmongo(MongoClient client,String filename,String databaseName,String collectionName){
		jsonParsing jsonParser = new jsonParsing(filename);
		parentMap = jsonParser.parentMap;
		childMap = jsonParser.childMap;
		siblings = jsonParser.siblings;
		weightMap = jsonParser.weightMap;
		idMap = jsonParser.idMap;
		fillMongoDatabase(client,databaseName,collectionName);
		//System.out.println(parentMap+" \n\n"+childMap+"\n\n"+siblings);
	}
	/*find siblings for a given element*/
	public  ArrayList<Integer> getSiblings(String key){
		ArrayList<Integer> list = new ArrayList();
		HashSet<Integer> set = new HashSet();
		for(ArrayList<Integer> sibs:siblings){
			if(sibs.contains(jsonParsing.getId(key))){
				set.addAll(sibs);
				set.remove(key);
			}
		}
		list.addAll(set);
		return list;
	}
	
	/*
	 * Add document in the database with the given values
	 */
	public void addElementInMongo(MongoClient client,String databasename,int ID,String name,String type,double weight
			,ArrayList<Integer> parent,ArrayList<Integer> child,ArrayList<Integer> siblings,String collectionName)
	{
		DB database = client.getDB(databasename);
		System.out.println("connected to Database");
		DBCollection collection = database.getCollection(collectionName);
		BasicDBObject doc = new BasicDBObject();
		doc.put("ID", ID);
		doc.put("name", name);
		doc.put("type", type);
		doc.put("weight", weight);
		doc.put("parents", parent);
		doc.put("children", child);
		doc.put("siblings", siblings);
		nodeStack.push(new node(ID,client,databasename,true));
		collection.insert(doc);
		DBObject obj;
		for(int id:parent){
			DBObject idObj = new BasicDBObject();
			idObj.put("ID", id);
			obj = new BasicDBObject();				
			obj.put("$push",new BasicDBObject("children",ID));
			collection.update(idObj,obj);
		}
		
		for(int id:child){
			DBObject idObj = new BasicDBObject();
			idObj.put("ID", id);
			obj = new BasicDBObject();				
			obj.put("$push",new BasicDBObject("parents",ID));
			collection.update(idObj,obj);
		}
		for(int id:siblings){
			DBObject idObj = new BasicDBObject();
			idObj.put("ID", id);
			obj = new BasicDBObject();				
			obj.put("$push",new BasicDBObject("siblings",ID));
			collection.update(idObj,obj);
		}	
		
	}
	
	


	/* Driver function to fill mongoDatabase */
	public  void fillMongoDatabase(MongoClient client,String databasename,String collectionName){

		DB database = client.getDB(databasename);
		System.out.println("connected to Database");
		try{
		DBCollection mainCollection = database.getCollection(collectionName);
		//putting values in database by taking it key by key in parentMap
		for(String key:parentMap.keySet()){
			BasicDBObject document = new BasicDBObject();
			document.put("ID",jsonParsing.getId(key));
			document.put("name",key); //putting in name
			String type="";
			//entering type
			if(key.contains("_c_")){
				document.put("type", "concept");
				type = "concept";
			}else
				if(key.contains("_e_")){
					document.put("type","entity");
					type = "entity";
				}else{
					document.put("type","phrase");
					type = "phrase";
				}
			document.put("weight", weightMap.get(key)); //entering weight
			document.put("parents", parentMap.get(key)); //entering parent list	
			if(childMap.get(key)==null) {document.put("children", new ArrayList<Integer>());}
			else {document.put("children",childMap.get(key));}  //entering children list			  	
			ArrayList<Integer> keySiblings = new ArrayList();
			keySiblings = getSiblings(key);
			//System.out.println(keySiblings);
			document.put("siblings",keySiblings); //entering sibling list
			nodeStack.push(new node(jsonParsing.getId(key),client,databasename,false));
			nodeStack.peek().commit=true;
			mainCollection.insert(document);	
		}
		for(String key:idMap.keySet()){
			if(parentMap.containsKey(key)){
				continue;
			}else{
				BasicDBObject document = new BasicDBObject();
				document.put("ID",jsonParsing.getId(key));
				document.put("name",key); //putting in name
				String type="";
				//entering type
				if(key.contains("_c_")){
					document.put("type", "concept");
					type = "concept";
				}else
					if(key.contains("_e_")){
						document.put("type","entity");
						type = "entity";
					}else{
						document.put("type","phrase");
						type = "phrase";
					}
				document.put("weight", weightMap.get(key)); //entering weight
				document.put("parents", parentMap.get(key)); //entering parent list	
				if(childMap.get(key)==null) {document.put("children", new ArrayList<Integer>());}
				else {document.put("children",childMap.get(key));}  //entering children list			  	
				ArrayList<Integer> keySiblings = new ArrayList();
				keySiblings = getSiblings(key);
				//System.out.println(keySiblings);
				document.put("siblings",keySiblings); //entering sibling list
				nodeStack.push(new node(jsonParsing.getId(key),client,databasename,false));
				nodeStack.peek().commit=true;
				mainCollection.insert(document);	
			}
		}
		}catch(Exception e){
			System.out.println(e);
			this.rollBack();
		}
		
		
//		DBCursor cursor = mainCollection.find();
//		try{
//			PrintWriter write = new PrintWriter("elastic.json");
//			while(cursor.hasNext()){
//				write.println(cursor.next());
//			}
//			
//		}catch(Exception e){
//			System.out.print("problem in file");
//		}
	}
	/*
	 * rollback changes according to times specified
	 */
	public void rollBack(){
		while(nodeStack.isEmpty()==false && nodeStack.peek().commit==false){
			node n = nodeStack.pop();
			n.rollBack();
		}
	}

}