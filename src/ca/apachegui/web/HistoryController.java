package ca.apachegui.web;

import java.io.IOException;
import java.sql.Timestamp;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ca.apachegui.db.LogData;


@RestController
@RequestMapping("/History")
public class HistoryController {
	private static Logger log = Logger.getLogger(HistoryController.class);
    
    @RequestMapping(value="/Current",method=RequestMethod.GET,produces="application/json;charset=UTF-8")
	public String getHistory() throws IOException, InterruptedException {
		int numberEntries=LogData.getNumberOfEntries();
		Timestamp newestTimeStamp=LogData.getNewestTime();
		String newestTime = (newestTimeStamp==null)?"":newestTimeStamp.toString();
		Timestamp oldestTimeStamp=LogData.getOldestTime();
		String oldestTime = (oldestTimeStamp==null)?"":oldestTimeStamp.toString();
		
		JSONObject result = new JSONObject();
		result.put("identifier", "id");
		result.put("label", "name");
		
		JSONArray items = new JSONArray();
		
		JSONObject entries = new JSONObject();
		entries.put("id", Integer.toString(numberEntries));
		entries.put("numHistory", Integer.toString(numberEntries));
		entries.put("newHistory", newestTime);
		entries.put("oldHistory", oldestTime);
		
		items.put(entries);
		result.put("items", items);
		
		return result.toString();
	}
	
	@RequestMapping(method=RequestMethod.GET,params="option=checkIfEnabled",produces="application/json;charset=UTF-8")
	public String checkIfEnabled() throws Exception {
		
		boolean enabled=ca.apachegui.history.History.checkIfEnabled();
		
		JSONObject result = new JSONObject();
		result.put("enabled", enabled);
		
		return result.toString();
	}
	
	@RequestMapping(method=RequestMethod.POST,params="option=enable",produces="application/json;charset=UTF-8")
	public String enable() throws Exception {
	
		ca.apachegui.history.History.enable();
		if(ca.apachegui.server.Control.isServerRunning())
		{	
			String error="";
			try
			{
				error=ca.apachegui.server.Control.restartServer();
				if(!ca.apachegui.server.Control.isServerRunning())
				{
					throw new Exception("The server could not restart");
				}
			}
			catch(Exception e)
			{
				log.error(e.getMessage(), e);
				ca.apachegui.history.History.disable();
				throw new Exception("There was an error while trying to restart the server, the changes were reverted: " + error + " " + e.getMessage());
			}
		}
		
		JSONObject result = new JSONObject();
		result.put("result", "success");
		
		return result.toString();
	}
	
	@RequestMapping(method=RequestMethod.POST,params="option=disable",produces="application/json;charset=UTF-8")
	public String disable() throws Exception {
		
		ca.apachegui.history.History.disable();
		if(ca.apachegui.server.Control.isServerRunning())
		{	
			String error="";
			try
			{
				error=ca.apachegui.server.Control.restartServer();
				if(!ca.apachegui.server.Control.isServerRunning())
				{
					throw new Exception("The server could not restart");
				}
			}
			catch(Exception e)
			{
				log.error(e.getMessage(), e);
				ca.apachegui.history.History.enable();
				throw new Exception("There was an error while trying to restart the server, the changes were reverted: " + error + " " + e.getMessage());
			}
		}
		
		JSONObject result = new JSONObject();
		result.put("result", "success");
		
		return result.toString();
		
	}
}