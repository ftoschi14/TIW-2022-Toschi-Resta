package it.polimi.tiw.utils;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Serializer {
	
	/**
	 * Returns a JsonObject containing the info of the specified <code>Object</code>.
	 * 
	 * @return <code>JsonObject</code> containing the info of the specified <code>Object</code>.
	 */
	public static JsonObject serialize(Object obj) {
		return new Gson().toJsonTree(obj).getAsJsonObject();
	}
	
	/**
	 * Returns a List of JsonObjects from the given <code>Object</code> list, with the specified name.
	 * 
	 * @return List of<code>JsonObject</code> containing the info of the specified <code>Objects</code>.
	 */
	public static JsonObject serializeAll(List<?> items, String listName) {
		JsonObject jsonObject = new JsonObject();
		JsonArray jsonArr = new JsonArray();
		
		for(Object item : items) {
			jsonArr.add(Serializer.serialize(item));
		}
		
		jsonObject.add(listName, jsonArr);
		return jsonObject;
	}
}
