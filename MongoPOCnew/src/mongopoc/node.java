package mongopoc;

import java.util.ArrayList;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

class node{
	int ID=0;
	String typeOfRoll = "";
	MongoClient client = null;
	String databasename = "";
	boolean commit = false;
	public node(int ID,MongoClient client,String databasename,boolean isDone){
		this.ID = ID;
		this.client = client;
		this.databasename = databasename;
		this.commit = isDone;
	}
	
	public node(int ID,String type,MongoClient client,String databasename){
		this.ID=ID;
		this.typeOfRoll=type;
		this.client = client;
		this.databasename = databasename;
		
	}
	
	public void rollBack(){
		DB database = client.getDB(databasename);
		System.out.println("connected to Database");
		DBCollection collection = database.getCollection("Collection5");
		
		if(typeOfRoll.equals("")){
			BasicDBObject query = new BasicDBObject();
			
			query.put("ID", ID);
			DBCursor cursor = collection.find(query);
			
//			ArrayList<Integer> parent = cursor.getQuery();
			if(cursor.hasNext()){
			DBObject obj = cursor.next();
			ArrayList<Integer> parent = (ArrayList<Integer>) obj.get("parents");
			ArrayList<Integer> child = (ArrayList<Integer>) obj.get("children");
			ArrayList<Integer> sibling = (ArrayList<Integer>) obj.get("siblings");
			
			for(int id:parent){
				DBObject idObj = new BasicDBObject();
				idObj.put("ID", id);
				obj = new BasicDBObject();				
				obj.put("$pullAll",new BasicDBObject("children",new int[]{ID}));
				collection.update(idObj,obj);
			}
			
			for(int id:child){
				DBObject idObj = new BasicDBObject();
				idObj.put("ID", id);
				obj = new BasicDBObject();				
				obj.put("$pullAll",new BasicDBObject("parents",new int[]{ID}));
				collection.update(idObj,obj);
			}
			for(int id:sibling){
				DBObject idObj = new BasicDBObject();
				idObj.put("ID", id);
				obj = new BasicDBObject();				
				obj.put("$pullAll",new BasicDBObject("siblings",new int[]{ID}));
				collection.update(idObj,obj);
			}
			collection.remove(query);
			BasicDBObject q = new BasicDBObject();
			q.put("ID", ID);
			DBCursor cur = collection.find(q);
			System.out.println("this is after deletion  " +  cur);
			
			System.out.println("rolled back "+ID);
			}
		}
	}
	
	public void commitDone(){
		commit = true;
	}
	
	public boolean isCommitDone(){
		return commit; 
	}
	
	public void uncommit(){
		commit = false;
	}
}