package mongopoc;

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

public class jsonParsing{
	 HashMap<String,ArrayList<Integer>> parentMap = new HashMap();  //HashMap to keep parents mapping weight in format [element]=[name#weight,..,.];
	 HashMap<String,ArrayList<Integer>> childMap = new HashMap();	//HashMap to keep children mapping in format [element] = [name1,name2,..]
	 ArrayList<ArrayList<Integer>> siblings = new ArrayList<ArrayList<Integer>>(); //List of Siblings
	 HashMap<String,Double> weightMap = new HashMap(); //Map for weight
	 static HashMap<String,Integer> idMap = new HashMap(); //giving elements ID
	 int idCount=0;
	JSONParser parser = null;
	
	public jsonParsing(String filename){		
		populateParentChildMaps(filename);		
	}
	
	
	/* returns ID of a key from idMap */
	public static int getId(String key){
		return idMap.get(key);
	}
	
	/*Function to add values to parentMap from JSON
	 * A helper function to populateParentChildMaps*/
	public void addToParentMap(String child,String parent){
		if(parentMap.containsKey(child)){
			ArrayList<Integer> temp = parentMap.get(child);
			temp.add(getId(parent));
			parentMap.put(child, temp);
			//System.out.println("two parents");
			}
		else{
			ArrayList<Integer> temp = new ArrayList();
			temp.add(getId(parent));
			parentMap.put(child,temp);
			//System.out.println("one parents");
		}
	}
	
	/*Function to add values to siblings from JSON
	 * A helper function to populateParentChildMaps*/
	public void addSiblingsToSiblingList(HashSet<String> set){
		ArrayList<Integer> list = new ArrayList();
		for(String str:set){
			list.add(getId(str));
		}
		siblings.add(list);
	}
	
	/*Function to add values to childMap from JSON
	 * A helper function to populateParentChildMaps*/
	public void addChildrenToChildMap(String key,ArrayList<Integer> list){
		if(childMap.containsKey(key)){
			ArrayList<Integer> temp = childMap.get(key);
			temp.addAll(list);
			childMap.put(key, temp);
		}else{
			childMap.put(key, list);
		}
	}
	
	public void putIdInMap(String key){
		if(idMap.containsKey(key))return;
		else {idMap.put(key, idCount);  idCount++;}
	}
	
	
	public void populateParentChildMaps(String JsonFileName){
		JSONParser parser = new JSONParser();
		parentMap = new HashMap();
		childMap = new HashMap();
		siblings = new ArrayList();
		weightMap = new HashMap();
		try{
		Object obj = parser.parse(new FileReader(JsonFileName));
		JSONObject jsonObj = (JSONObject)obj;
		for(Object key:jsonObj.keySet()){	
			
			String str = key.toString();
			putIdInMap(str);
			//String[] key = str.split("{");
			//System.out.println(jsonObj.get(key));			
			JSONObject outer = (JSONObject)jsonObj.get(key);
			ArrayList<String> parentValueList = new ArrayList();
			ArrayList<Integer> childValueList = new ArrayList();
			HashSet<String> siblingsSet = new HashSet();
			Random ran = new Random();
			weightMap.put(key.toString(), ran.nextDouble());
			for(Object innerkey:outer.keySet()){
				putIdInMap(innerkey.toString());
				siblingsSet.add(innerkey.toString());
				String value = key.toString();
						//"#"+outer.get(innerkey).toString();				
				childValueList.add(getId(innerkey.toString()));
				addToParentMap(innerkey.toString(),key.toString());	
				weightMap.put(innerkey.toString(), ran.nextDouble());
			}
			addSiblingsToSiblingList(siblingsSet);
			addChildrenToChildMap(key.toString(),childValueList);
			//System.out.println(siblingsSet);		
		}
//		System.out.println("hallla boll   " +childMap);
//		System.out.println("\n \n"+parentMap);
//		System.out.println("\n \n"+siblings);
//		System.out.println(("\n \n "+weightMap));
		
		}catch(Exception e){
			System.out.println(e);
		}		
	}
}