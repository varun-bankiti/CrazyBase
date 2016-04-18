import java.io.*;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * @author Varun Kumar Reddy Bankiti
 * @version 1.0
 */

public class CrazyBase {
	public static String prompt = "CrazyBase>";
	public static String SCHEMA = "information_schema";
	static String tableFile = new String();
	static String indexFile = new String();

	public static void splashScreen() {
		System.out.println(line("*",80));
		System.out.println("Welcome to CrazyBaseLite!\n");
		version();
		System.out.println("Type \"help;\" to display supported commands.");
		System.out.println(line("*",80));
	}
	public static String line(String s,int num) {
		String a = "";
		for(int i=0;i<num;i++) {
			a += s;
		}
		return a;
	}
	public static void help() {
		System.out.println(line("*",80));
		System.out.println();
		System.out.println("\tcreate schema SCHEMA_NAME;	Creates schema SCHEMA_NAME");
		System.out.println("\tuse schema SCHEMA_NAME;		Changes present schema to SCHEMA_NAME");
		System.out.println("\tshow tables;					Shows the tables in present schema");
		System.out.println("\tcreate TABLE TABLE_INFO;		Creates a new table in present schema accroding to TABLE_INFO");
		System.out.println("\tdrop table TABLE_NAME;		Drops the table TABLE_NAME if its present in current schema");
		System.out.println("\tversion;       				Show the program version.");
		System.out.println("\thelp;          				Show this help information");
		System.out.println("\texit;          				Exit the program");
		System.out.println();
		System.out.println();
		System.out.println(line("*",80));
	}	
	public static void version() {
		System.out.println("CrayBaseLite v1.0\n");
	}

	//Fucntion to create schema command
	private static void createSchema(String userQuery) {
		String[] tempQuery = userQuery.split(" ");
		String schema_name = tempQuery[2];
		String fileName = "information_schema.schemata.tbl";
		File fileObject = new File(fileName);
		long fileLength = fileObject.length();

		if (!fileObject.exists()) {
			System.out.println("Schemata doesnt exists");
		}else{
			RandomAccessFile schemataTableFile;
			try {
				schemataTableFile = new RandomAccessFile("information_schema.schemata.tbl", "rw");
				schemataTableFile.seek(fileLength);
				schemataTableFile.writeByte(schema_name.length());
				schemataTableFile.writeBytes(schema_name);
				schemataTableFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	} 

	//Function to show schemas command
	private static void showSchemas() {
		System.out.println(line("*",35));
		System.out.println("|               Schemas Present             |");
		System.out.println(line("*",35));
		RandomAccessFile schemataTableFile;
		try {
			schemataTableFile = new RandomAccessFile("information_schema.schemata.tbl", "rw");
			while(true){
				byte varcharLength = schemataTableFile.readByte();
				for(int i = 0; i < varcharLength; i++){
					System.out.print((char)schemataTableFile.readByte());}
				System.out.println();
			}
		} catch(EOFException e){
			return;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	//Function to use schema command
	public static void useSchema(String userQuery) {
		String[] tempQuery = userQuery.split(" ");
		String schema = tempQuery[1];

		CrazyBase.SCHEMA = schema;

		System.out.println("Database changed to " + schema);
	}
	
	//Function for show table command
	public static void showTables(String userQuery) {
		System.out.println(line("*",50));
		System.out.println("       Tables in "+ CrazyBase.SCHEMA +" database         ");
		System.out.println(line("*",50));
		RandomAccessFile tablesTableFile;
		String schema= new String();
		String table = new String();
		try {
			tablesTableFile = new RandomAccessFile("information_schema.table.tbl", "r");
			while(true){
				byte schemaLength = tablesTableFile.readByte();
				for(int i = 0; i < schemaLength; i++){
					schema += (char)tablesTableFile.readByte();
				}
				byte tableLength = tablesTableFile.readByte();
				for(int i = 0; i < tableLength; i++){
					if(schema.equalsIgnoreCase(CrazyBase.SCHEMA)){
						table += (char)tablesTableFile.readByte();
					}else{
						tablesTableFile.readByte();
					}
				}

				long rows = tablesTableFile.readLong();
				byte tableactive = tablesTableFile.readByte();
				if(schema.equalsIgnoreCase(CrazyBase.SCHEMA) && tableactive == 01){
					System.out.println(table);
					System.out.println(line("*",50));
				}
				table="";
				schema="";

			}
		} catch(EOFException e){
			return;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	//Function to create table command
	public static void createTable(String userQuery){

		String tableNameStr = userQuery.substring(0, userQuery.indexOf('(')).trim();
		String columnsStr = userQuery.substring(userQuery.indexOf('(')+1, userQuery.length()-1);

		try {
			String tableName = tableNameStr.split(" ")[2].trim();
			DefaultFileHandlers.insertIntoInformationSchemaTables(tableName);
			String[] tempColumns = columnsStr.split(",") ;
			for(int i=0 ; i<tempColumns.length ; i++){
				String columnName = tempColumns[i].trim().split(" ")[0];
				String datatypeName = tempColumns[i].trim().split(" ")[1];
				if((datatypeName.equals("short") || datatypeName.equals("long"))&& tempColumns[i].trim().split(" ").length>2){
					String subdatatype = tempColumns[i].trim().split(" ")[2];
					if(subdatatype.equalsIgnoreCase("int")){
						datatypeName = datatypeName + " " + subdatatype;
					}
				}
				String isNullable = new String();
				if(tempColumns[i].contains("not") && tempColumns[i].contains("null") ){
					isNullable = "no";
				}else{
					isNullable = "yes";
				}
				String columnKey = new String();
				if(tempColumns[i].contains("primary") && tempColumns[i].contains("key") ){
					columnKey = "pri";
					isNullable = "no";
				}else{
					columnKey = "";
				}
				DefaultFileHandlers.insertIntoInformationSchemaColumns(tableName, columnName, i+1, datatypeName, isNullable, columnKey);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Please check the syntax of the query");
			return;
		}
	}
	//Function to insert into table command
	public static void insertIntoTable(String userQuery){
		try{
			String temp = userQuery.substring(userQuery.indexOf('(')+1, userQuery.length()-1);
			ArrayList<String> values = new ArrayList<>();
			ArrayList<String> columns = new ArrayList<>();
			ArrayList<String> datatypes = new ArrayList<>();
			ArrayList<Integer> positions = new ArrayList<>();
			ArrayList<String> isNullable = new ArrayList<>();
			ArrayList<String> columnKey = new ArrayList<>();

			String[] tempValues = temp.split(",");

			String tableName = userQuery.substring(0, userQuery.indexOf('(')-1).split(" ")[2];
			for(int i=0 ; i<tempValues.length ; i++){
				values.add(tempValues[i].trim().replace("\"", "").replace("\'", ""));
			}
			columns = Attributes.getColumns(tableName);
			positions = Attributes.getPositions(tableName);
			datatypes = Attributes.getDataTypes(tableName);
			isNullable = Attributes.getNullConstraint(tableName);
			columnKey = Attributes.getPrimaryKeyConstraint(tableName);

			if(values.size()!=columns.size()){
				System.out.println("The values and column counts do not match");
//				System.out.println(values.size());System.out.println(columns.size());
				return;
			}
			String dataTableFileName = CrazyBase.SCHEMA+"."+tableName+".dat";
			File dataTableFileObject = new File(dataTableFileName);
			long datafileLength = dataTableFileObject.length();
			RandomAccessFile dataTableFile = new RandomAccessFile(dataTableFileObject, "rw");

			Long offset = new Long(0);

			dataTableFile.seek(datafileLength);
			for(int i=0 ; i<columns.size(); i++){

				if(i==0){
					offset = dataTableFile.getFilePointer();
				}
				if((isNullable.get(i).equals("no") || columnKey.get(i).equals("pri")) && values.get(i).equals("null")){
					System.out.println("Cannot insert null value for " + columns.get(i));
					return;
				}
				if(datatypes.get(i).equals("int")){
					dataTableFile.writeInt(Integer.parseInt(values.get(i)));
				}else if(datatypes.get(i).equals("float")){
					dataTableFile.writeFloat(Float.parseFloat(values.get(i)));
				}else if(datatypes.get(i).equals("long") || datatypes.get(i).trim().equals("long int")){
					dataTableFile.writeLong(Long.parseLong(values.get(i)));
				}else if(datatypes.get(i).equals("double")){
					dataTableFile.writeDouble(Double.parseDouble(values.get(i)));
				}else if(datatypes.get(i).contains("varchar") || datatypes.get(i).contains("char")){
					dataTableFile.writeByte(values.get(i).length());
					dataTableFile.writeBytes(values.get(i));
				}else if(datatypes.get(i).equals("short") || datatypes.get(i).trim().equals("short int")){
					dataTableFile.writeShort(Short.parseShort(values.get(i)));     
				}else if(datatypes.get(i).equals("date")){
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
					String dateInString = values.get(i);
					Date date = formatter.parse(dateInString);
					long dateInLong = date.getTime();
					dataTableFile.writeLong(dateInLong);     
				}
				else if(datatypes.get(i).equals("datetime")){
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
					String dateInString = values.get(i);
					Date date = formatter.parse(dateInString);
					long dateInLong = date.getTime();
					dataTableFile.writeLong(dateInLong);     
				}
				else{
					System.out.println(datatypes.get(i));
				}
				IndexFileHandlers.insertIntoIndexFile(datatypes.get(i), values.get(i), columns.get(i), tableName, offset);
			}
			dataTableFile.close();

		}catch(Exception e){
			System.out.println("Please check the syntax of the query.");
			return;
		}
	}
	//Function to select from table command	
	public static void selectFromTable(String userQuery){
		try{
			String[] query = userQuery.split(" ");
			String tableName = query[3];
			String columnName = query[5];
			String operator = query[6];
			String value = query[7];
			String datatype = new String();
			Integer position = new Integer(0);

			ArrayList<String> columns = new ArrayList<>();
			ArrayList<String> datatypes = new ArrayList<>();
			ArrayList<Integer> positions = new ArrayList<>();
			if(value.contains("\'")){
				value = userQuery.substring(userQuery.indexOf("\'")+1, userQuery.length()-1);
			}
			columns = Attributes.getColumns(tableName);
			datatypes = Attributes.getDataTypes(tableName);
			positions = Attributes.getPositions(tableName);
			for(int i=0 ; i<columns.size() ; i++){
				if(columns.get(i).equalsIgnoreCase(columnName)){
					datatype = datatypes.get(i);
					position = positions.get(i);
					break;
				}
			}

			if(columns.size() <= 0){
				System.out.println("The table does not exists");
				return;
			}

			String indexTableFileName = CrazyBase.SCHEMA+"."+tableName+"."+columnName+".ndx";
			Map<Object, ArrayList<Long>> index = new HashMap<Object, ArrayList<Long>>();
			index = IndexFileHandlers.getIndexFileEntries(indexTableFileName, datatype);
			Boolean isValueFound = false;
			Set s = index.entrySet();
			Iterator it1 = s.iterator();
			ArrayList<Long> values = new ArrayList<>();
			Object key = new Object();

			for(int j=0 ; j<columns.size();j++){
				System.out.print(columns.get(j) + " |");
			}
			System.out.println();
			System.out.println(line("*",50));
			while(it1.hasNext()) {
				Map.Entry me2 = (Map.Entry)it1.next();
				key = me2.getKey();

				if(operator.trim().equals("=")){
					if(datatype.equals("int") ||datatype.equals("float") || datatype.equals("double") || datatype.equals("short")
							|| datatype.equals("long") || datatype.equals("short int") || datatype.equals("long int") || datatype.equals("date")){

						Long temp = new Long(0);
						Long val = new Long(0);

						if(datatype.equalsIgnoreCase(("date"))){
							temp = Long.parseLong(key.toString());

							String someDate = value;
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
							Date date = sdf.parse(someDate);
							val = date.getTime();

						}else if(datatype.equalsIgnoreCase(("datetime"))){
							temp = Long.parseLong(key.toString());

							String someDate = value;
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
							Date date = sdf.parse(someDate);
							val = date.getTime();

						}
						else{
							temp = Long.parseLong(key.toString());
							val = Long.parseLong(value);
						}

						if(temp == val){
							values = (ArrayList<Long>) me2.getValue();
							IndexFileHandlers.locateRecordInDatafile(value, key, values, tableName, columns, datatypes, position);
						}
					}

					if(datatype.contains("char") || datatype.contains("varchar")){
						if(value.equals(key.toString())){
							values = (ArrayList<Long>) me2.getValue();
							IndexFileHandlers.locateRecordInDatafile(value, key, values, tableName, columns, datatypes, position);
						}						
					}
				}

				if(operator.trim().equals(">")){
					if(datatype.equals("int") ||datatype.equals("float") || datatype.equals("double") || datatype.equals("short")
							|| datatype.equals("long") || datatype.equals("short int") || datatype.equals("long int") || datatype.equals("date")){
						Long temp = new Long(0);
						Long val = new Long(0);

						if(datatype.equalsIgnoreCase(("date"))){
							temp = Long.parseLong(key.toString());
							String someDate = value;
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
							Date date = sdf.parse(someDate);
							val = date.getTime();

						}else if(datatype.equalsIgnoreCase(("datetime"))){
							temp = Long.parseLong(key.toString());
							String someDate = value;
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
							Date date = sdf.parse(someDate);
							val = date.getTime();

						}
						else{
							temp = Long.parseLong(key.toString());
							val = Long.parseLong(value);
						}

						if(temp > val){
							values = (ArrayList<Long>) me2.getValue();
							IndexFileHandlers.locateRecordInDatafile(value, key, values, tableName, columns, datatypes, position);
						}

					}

					if(datatype.contains("char") || datatype.contains("varchar")){
						System.out.println("Comparison is not defined for the datatype char or varchar");
						return;
					}
				}
				if(operator.trim().equals("<")){
					if(datatype.equals("int") ||datatype.equals("float") || datatype.equals("double") || datatype.equals("short")
							|| datatype.equals("long") || datatype.equals("short int") || datatype.equals("long int") || datatype.equals("date")){

						Long temp = new Long(0);
						Long val = new Long(0);

						if(datatype.equalsIgnoreCase(("date"))){
							temp = Long.parseLong(key.toString());
							String someDate = value;
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
							Date date = sdf.parse(someDate);
							val = date.getTime();

						}else if(datatype.equalsIgnoreCase(("datetime"))){
							temp = Long.parseLong(key.toString());
							String someDate = value;
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
							Date date = sdf.parse(someDate);
							val = date.getTime();

						}
						else{
							temp = Long.parseLong(key.toString());
							val = Long.parseLong(value);
						}

						if(temp < val){
							values = (ArrayList<Long>) me2.getValue();
							IndexFileHandlers.locateRecordInDatafile(value, key, values, tableName, columns, datatypes, position);
						}

					}

					if(datatype.contains("char") || datatype.contains("varchar")){
						System.out.println("Comparison is not defined for the datatype char or varchar");
						return;
					}
				}
			}
		}catch(Exception e){
			System.out.println("Please check the query.");
			System.out.println("There should be space between OPERATOR, VALUE and COLUMN NAME\n");
			e.printStackTrace();
			return;
		}
	}
	
	//Function to drop table command
	private static void dropTable(String userQuery) {
		String tableName = userQuery.split(" ")[2];
		ArrayList<String> columns = Attributes.getColumns(tableName);
		String indexFileName = new String();
		DefaultFileHandlers.unsetActiveBitInTablestable(tableName);
		DefaultFileHandlers.unsetActiveBitInColumnstable(tableName);
		for(int i=0 ; i<columns.size() ; i++){
			indexFileName = CrazyBase.SCHEMA+"."+tableName+"."+columns.get(i)+".ndx";
			File file = new File(indexFileName);
			//System.out.println(indexFileName);
			try{
				if(!file.delete()){
					System.out.println("Delete operation failed.");
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		String dataFile = CrazyBase.SCHEMA+"."+tableName+".dat";
		File file = new File(dataFile);
		if(!file.delete()){
			System.out.println("Table does not exists");
		}
	}
	public static void main(String[] args) {
		/* Display the welcome splash screen */
		splashScreen();
		/* 
		 *  The Scanner class is used to collect user commands from the prompt
		 *  There are many ways to do this. This is just one.
		 *
		 *  Each time the semicolon (;) delimiter is entered, the userCommand String
		 *  is re-populated.
		 */
		Scanner scanner = new Scanner(System.in).useDelimiter(";");
		String userQuery; 

		do {  
			System.out.print(prompt);
			userQuery = scanner.next().trim().toLowerCase();		
			//create schema command
			if(userQuery.startsWith("create schema")){
				createSchema(userQuery);
			}

			// show schemas command
			else if(userQuery.equalsIgnoreCase("show schemas")){
				showSchemas();
			}

			//use schema command
			else if(userQuery.startsWith("use")){
				useSchema(userQuery);
			}

			//show tables command
			else if(userQuery.equalsIgnoreCase("show tables")){
				showTables(userQuery);
			}

			// create table command
			else if(userQuery.startsWith("create table")){
				createTable(userQuery);
			}
			// insert into table command
			else if(userQuery.startsWith("insert")){
				insertIntoTable(userQuery);
			}

			// select from table command
			else if(userQuery.startsWith("select")){
				selectFromTable(userQuery);
			}

			// drop table command
			else if(userQuery.startsWith("drop")){
				dropTable(userQuery);
			}
			// help command
			else if(userQuery.equalsIgnoreCase("help")){
				help();	
			}
			// version command
			else if(userQuery.equalsIgnoreCase("version")){
				version();	
			}

		} while(!userQuery.equalsIgnoreCase("exit"));
		System.out.println("Exiting...");

	}

}
