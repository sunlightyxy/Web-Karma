package edu.isi.karma.util;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class KarmaStats {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String inputFileName = "";//args[0];
		
		//System.out.println(args[0]);
		if(inputFileName==null)
		{
			System.out.println("Please provide input file.");
			return;
		}
		
		karmaStats(inputFileName);
	}

	public static void karmaStats(String inputFile)
	{
		String fileName = "input.txt"; 
		String statsFile = "karmaStats.json";
		try {
			FileReader filereader = new FileReader(fileName);
			BufferedReader bufferedReader = new BufferedReader(filereader);
			String tmpString = null;
			StringBuffer buf = new StringBuffer();
			
			final String matchDoublequote = "\\\"";
			final String newDoublequote = "\"";
			final String matchSlash = "\\\\";
			final String newSlash = "\\";
			final String pyTransformCommandName = "SubmitPythonTransformationCommand";
			final String setSemanticCommandName = "SetSemanticTypeCommand";
			final String setPropertyCommandName = "SetMetaPropertyCommand";
			final String addLiteralCommandName = "AddLiteralNodeCommand";
			final String addLinkCommandName = "AddLinkCommand";
			final String deleteLinkCommandName = "DeleteLinkCommand";
			final String changeLinkCommandName = "ChangeInternalNodeLinksCommand";
			final String unassignSemeticCommandName = "UnassignSemanticTypeCommand";
			
			boolean isJSON = false;
			int pyTransformCount = 0;
			int SemanticTypeCount = 0;
			int classCount = 0;
			int linkCount = 0; //Except sementic type links
			int filterCount = 0;
			
			while((tmpString = bufferedReader.readLine())!=null)
			{
				if(isJSON)
				{
					if(tmpString.contains("]\"\"\""))
					{
						buf.append(']');
						break;
					}
					buf.append(tmpString);
				}
				else
				{
					if(tmpString.contains("hasWorksheetHistory"))
					{
						//Start appending into buffer which will be JSON String
						buf.append('[');
						isJSON = true;
					}
				}
				
			}

			if(isJSON)
			{
				classCount = countClass(bufferedReader);
				
				String bufString = buf.toString();
				String removedQuote = bufString.replace(matchDoublequote, newDoublequote);
				String removedSlash = removedQuote.replace(matchSlash,newSlash);
				
			    JSONArray commands = new JSONArray(removedSlash);
			    
			    //get SubmitPyTransform commands stats
			    for(int i=0;i<commands.length();i++)
			    {
			    	JSONObject command = (JSONObject)commands.get(i);
			    	String commandName = command.getString("commandName");
			    	System.out.println(command.get("tags"));
			    	System.out.println(commandName + " Commandname");
			    	//System.out.println(tag + " tag");
			    	
			    	if(commandName.equals(pyTransformCommandName))
			    	{
			    		pyTransformCount++;
			    	}
			    	else if(commandName.equals(setSemanticCommandName))
			    	{
			    		SemanticTypeCount++;
			    	}
			    	else if(commandName.equals(setPropertyCommandName))
			    	{
			    		SemanticTypeCount++;
			    	}
			    	else if(commandName.equals(addLinkCommandName))
			    	{
			    		linkCount++;
			    	}
			    	else if(commandName.equals(deleteLinkCommandName))
			    	{
			    		linkCount--;
			    	}		    	
			    	else if(commandName.equals(unassignSemeticCommandName))
			    	{
			    		SemanticTypeCount--;
			    	}
			    }
			}
		    FileWriter out = new FileWriter(statsFile); 
		    BufferedWriter bufferedWriter = new BufferedWriter(out);
		    
		    StringBuffer writeBuffer = new StringBuffer();
		    JSONObject output = new JSONObject();
		    JSONObject modelStat = new JSONObject();
		    modelStat.put("modelName", "inputFile");
		    output.put("pyTransformations", pyTransformCount);
		    output.put("semanticTypes", SemanticTypeCount);
		    output.put("class", classCount);
		    output.put("links", linkCount);
		    output.put("filters", filterCount);
		    modelStat.put("modelStatistics", output);
		    writeBuffer.append(modelStat.toString(4));
		  /* 
		    writeBuffer.append("#################\n");
		    writeBuffer.append("Karma Statistics\n");
		    writeBuffer.append("#################\n\n\n");
		    
		    
				    
		    writeBuffer.append("Python Transformations - ");
		    writeBuffer.append(pyTransformCount);
		    writeBuffer.append("\n");
		    
		    writeBuffer.append("SementicType Count - ");
		    writeBuffer.append(SemanticTypeCount);
		    writeBuffer.append("\n");
		    
		    writeBuffer.append("Class Count - ");
		    writeBuffer.append(classCount);
		    writeBuffer.append("\n");
		    
		    writeBuffer.append("Links Count - ");
		    writeBuffer.append(linkCount);
		    writeBuffer.append("\n");
 		*/		    
		    bufferedWriter.write(writeBuffer.toString());
		    
		    
		    bufferedReader.close();
		    bufferedWriter.close();
		}
		catch(JSONException e) {
			e.printStackTrace();
			System.out.println("Failed to Parse JSON");
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.out.println("Error in reading/writing File");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static int countClass(BufferedReader reader)
	{
		int classCount = 0;
		try
		{
			String tmpString = null;
			while((tmpString = reader.readLine())!=null)
			{
				if(tmpString.contains("rr:class"))
				{
					classCount++;
				}
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		return classCount;
	}
	
}
