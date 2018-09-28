package edu.jsu.mcis;

import java.io.*;
import java.util.*;
import com.opencsv.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class Converter {
    
    /*
    
        Consider the following CSV data:
        
        "ID","Total","Assignment 1","Assignment 2","Exam 1"
        "111278","611","146","128","337"
        "111352","867","227","228","412"
        "111373","461","96","90","275"
        "111305","835","220","217","398"
        "111399","898","226","229","443"
        "111160","454","77","125","252"
        "111276","579","130","111","338"
        "111241","973","236","237","500"
        
        The corresponding JSON data would be similar to the following (tabs and
        other whitespace have been added for clarity).  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings, and which values should be encoded as integers!
        
        {
            "colHeaders":["ID","Total","Assignment 1","Assignment 2","Exam 1"],
            "rowHeaders":["111278","111352","111373","111305","111399","111160",
            "111276","111241"],
            "data":[[611,146,128,337],
                    [867,227,228,412],
                    [461,96,90,275],
                    [835,220,217,398],
                    [898,226,229,443],
                    [454,77,125,252],
                    [579,130,111,338],
                    [973,236,237,500]
            ]
        }
    
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
    
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including example code.
    
    */
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        
        String results = "";
        
        try {
            
            CSVReader reader = new CSVReader(new StringReader(csvString));
            List<String[]> full = reader.readAll();
            Iterator<String[]> iterator = full.iterator();
            
            // INSERT YOUR CODE HERE
            
            // Create Containers
            
            JSONObject jsonContainer = new JSONObject();
            
            ArrayList<String> jsonColHeader = new ArrayList<>();
            ArrayList<String> jsonRowHeader = new ArrayList<>();
            ArrayList<ArrayList<Integer>> jsonData = new ArrayList<>();
            
            // Get Column Headers
            
            String[] colHeaders = full.get(0);
            
            for(String e: colHeaders) {
                jsonColHeader.add(e);
            }
            
            // Get Rows
            
            for (int i = 1; i < full.size(); ++i) {
                
                String[] row = full.get(i);
                
                // Get Row Header
                
                jsonRowHeader.add(row[0]);
                
                // Get Row Data
                
                ArrayList<Integer> dataRow = new ArrayList<>();
                
                for (int j = 1; j < row.length; ++j) {
                    
                    dataRow.add(Integer.parseInt(row[j]));
                    
                }
                
                jsonData.add(new ArrayList(dataRow));
                
                
            }
            
            jsonContainer.put("colHeaders", jsonColHeader);
            jsonContainer.put("rowHeaders", jsonRowHeader);
            jsonContainer.put("data", jsonData);
            
            results = jsonContainer.toJSONString();
            
        }        
            
        catch(Exception e) { return e.toString(); }
        
        return results.trim();
        
    }
    
    public static String jsonToCsv(String jsonString) {
        
        String results;
        
        try {
        
            // Create CSVWriter object, which will use a StringWriter to
            // encode the data, one row at a time, to CSV format.
        
            StringWriter writer = new StringWriter();
            CSVWriter csvWriter = new CSVWriter(writer, ',', '"', '\n');

            // Parse JSON Data to a JSONObject map.
            
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject)parser.parse(jsonString);
            
            // Get JSON data from the JSONObject.  Recall that this data is in
            // lists under the key names "colHeaders", "rowHeaders", and
            // "data", and that the latter contains nested lists of integers
            // which must later be converted back into strings.
            
            JSONArray colHeaders = (JSONArray)jsonObject.get("colHeaders");
            JSONArray rowHeaders = (JSONArray)jsonObject.get("rowHeaders");
            JSONArray data = (JSONArray)jsonObject.get("data");
            
            // Create empty CSV containers in the form of String arrays.
            // These arrays must be sized according to the sizes of the
            // original JSON lists.  We will subsequently copy the data
            // from the JSON lists into these arrays: the column headers
            // will be copied to "colStringArray", the row headers to
            // "rowStringArray", and the row data to "dataStringArray".
            
            String[] colStringArray = new String[colHeaders.size()];
            String[] rowStringArray = new String[rowHeaders.size()];
            String[] dataStringArray = new String[data.size()];
            
            // First, get the column headers, copying each into the
            // "colStringArray" container.

            for (int i = 0; i < colHeaders.size(); i++){
                colStringArray[i] = colHeaders.get(i).toString();
            }
            
            // Next, output the column headers to the CSV writer.  (This
            // is all we need to do with the column headers.)
            
            csvWriter.writeNext(colStringArray);
            
            // Next, get the row headers and row data.  For now, we will
            // store them in separate arrays; later, we will recombine the
            // row headers and row data into a single array.
            //
            // (Note that each list of row data is still a string at this
            // point, like this: "[611,146,128,337]".  We will be parsing
            // these into lists of integers using the json-simple library.)
            
            for (int i = 0; i < rowHeaders.size(); i++){
            
                // Get next row header
            
                rowStringArray[i] = rowHeaders.get(i).toString();
                
                // Get next set of row data
                
                dataStringArray[i] = data.get(i).toString();
                
            }

            // Next, iterate through the lists of row headers and row data.
            // Each will need to be recombined into a single array of strings,
            // called "row".  To do this, we will convert the row data into a
            // JSONArray of integers first.  Then, we will add the row header
            // to the first element of the "row" array, and then copy the row
            // data values into the subsequent elements of the "row" array.
            
            for (int i = 0; i < dataStringArray.length; i++) {
            
                // Parse row data (example: "[611,146,128,337]") into a JSON
                // array called "dataValues".
                                
                JSONArray dataValues = (JSONArray)parser.parse(dataStringArray[i]);
                
                // Create a "row" array for this row, sized to the number of
                // elements in the row.

                String[] row = new String[dataValues.size() + 1];
                
                // Copy the next row header from the array "rowStringArray" to
                // the first element of "row", then copy the elements from
                // "dataValues" into the subsequent elements, converting the
                // elements to strings in the process.
                
                row[0] = rowStringArray[i];
                
                // INSERT CODE HERE
                for(int j = 0; j < dataValues.size(); j++) {
                    row[j+1] = dataValues.get(j).toString();
                }
                
                // Finally, now that the next row has been built, output it to
                // the CSV writer.
                
                csvWriter.writeNext(row);
                
            }
            
            // Output the completed CSV data to a string
            
            results = writer.toString();
            
        }
        
        catch(ParseException e) { return e.toString(); }
        
        return results.trim();
        
    }
    
}
    
