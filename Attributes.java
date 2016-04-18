import java.io.*;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
public class Attributes{
		String name;
		String dataType;
		boolean isPrimaryKey;
		Attributes(){

		}
		Attributes(String name,String dataType, boolean isPrimaryKey){
			this.name=name;
			this.dataType=dataType;
			this.isPrimaryKey=isPrimaryKey;
		}
		String getName(){
			return this.name;
		}
		String getDataType(){
			return this.dataType;
		}
		boolean isPrimaryKey(){
			return this.isPrimaryKey;
		}

	public static ArrayList<String> getColumns(String tableName) {
		ArrayList<String> columns = new ArrayList<>();
		try {
			String columnsfileName = "information_schema.columns.tbl";
			File columnsfileObject = new File(columnsfileName);
			long columnsfileLength = columnsfileObject.length();
			RandomAccessFile columnsTableFile = new RandomAccessFile(columnsfileObject, "rw");

			String schema = new String();
			String table = new String();
			String column = new String();
			String type = new String();

			while(true){
				byte schemaLength = columnsTableFile.readByte();
				for(int i = 0; i < schemaLength; i++){
					schema += (char)columnsTableFile.readByte();
				}
				byte tableLength = columnsTableFile.readByte();
				for(int i = 0; i < tableLength; i++){
					table += (char)columnsTableFile.readByte();
				}
				byte columnLength = columnsTableFile.readByte();
				for(int i = 0; i < columnLength; i++){
					if(table.equalsIgnoreCase(tableName))
						column += (char)columnsTableFile.readByte();
					else
						columnsTableFile.readByte();
				}
				int position = columnsTableFile.readInt();

				byte typeLength = columnsTableFile.readByte();
				for(int i = 0; i < typeLength; i++){
					type += (char)columnsTableFile.readByte();
				}
				byte isNullableLength = columnsTableFile.readByte();
				for(int i = 0; i < isNullableLength ; i++){
					columnsTableFile.readByte();
				}

				byte columnkeyLength = columnsTableFile.readByte();
				for(int i = 0; i < columnkeyLength ; i++){
					columnsTableFile.readByte();
				}

				byte isActive = columnsTableFile.readByte();

				if(table.equalsIgnoreCase(tableName) && isActive==01 ){
					columns.add(column);
				}			
				schema="";
				table="";
				column="";
				type="";
			}
		} catch(EOFException e){
			return columns;
		}
		catch(Exception e){
			e.printStackTrace();
			return columns;
		}

	}

	public static ArrayList<Integer> getPositions(String tableName) {
		ArrayList<Integer> positions = new ArrayList<>();
		try {
			String columnsfileName = "information_schema.columns.tbl";
			File columnsfileObject = new File(columnsfileName);
			long columnsfileLength = columnsfileObject.length();
			RandomAccessFile columnsTableFile = new RandomAccessFile(columnsfileObject, "rw");

			String schema = new String();
			String table = new String();
			Integer position = new Integer(0);

			while(true){
				byte schemaLength = columnsTableFile.readByte();
				for(int i = 0; i < schemaLength; i++){
					schema += (char)columnsTableFile.readByte();
				}

				byte tableLength = columnsTableFile.readByte();
				for(int i = 0; i < tableLength; i++){
					table += (char)columnsTableFile.readByte();
				}

				byte columnLength = columnsTableFile.readByte();
				for(int i = 0; i < columnLength; i++){
					columnsTableFile.readByte();
				}

				if(table.equalsIgnoreCase(tableName))
					position = columnsTableFile.readInt();
				else
					columnsTableFile.readInt();

				byte typeLength = columnsTableFile.readByte();
				for(int i = 0; i < typeLength; i++){
					columnsTableFile.readByte();
				}

				byte isNullableLength = columnsTableFile.readByte();

				for(int i = 0; i < isNullableLength ; i++){
					columnsTableFile.readByte();
				}

				byte columnkeyLength = columnsTableFile.readByte();
				for(int i = 0; i < columnkeyLength ; i++){
					columnsTableFile.readByte();
				}

				byte isActive = columnsTableFile.readByte();

				if(table.equalsIgnoreCase(tableName) && isActive==01 ){
					positions.add(position);
				}	

				schema="";
				table="";
			}
		}catch(EOFException e){
			return positions;
		}
		catch(Exception e){
			e.printStackTrace();
			return positions;
		}

	}
	public static ArrayList<String> getDataTypes(String tableName) {
		ArrayList<String> dataTypes = new ArrayList<>();
		try {
			String columnsfileName = "information_schema.columns.tbl";
			File columnsfileObject = new File(columnsfileName);
			long columnsfileLength = columnsfileObject.length();
			RandomAccessFile columnsTableFile = new RandomAccessFile(columnsfileObject, "rw");

			String schema = new String();
			String table = new String();
			String column = new String();
			String type = new String();

			while(true){
				byte schemaLength = columnsTableFile.readByte();
				for(int i = 0; i < schemaLength; i++){
					schema += (char)columnsTableFile.readByte();
				}

				byte tableLength = columnsTableFile.readByte();
				for(int i = 0; i < tableLength; i++){
					table += (char)columnsTableFile.readByte();
				}

				byte columnLength = columnsTableFile.readByte();
				for(int i = 0; i < columnLength; i++){
					columnsTableFile.readByte();
				}

				int position = columnsTableFile.readInt();

				byte typeLength = columnsTableFile.readByte();
				for(int i = 0; i < typeLength; i++){
					if(table.equalsIgnoreCase(tableName))
						type += (char)columnsTableFile.readByte();
					else
						columnsTableFile.readByte();
				}

				byte isNullableLength = columnsTableFile.readByte();
				for(int i = 0; i < isNullableLength ; i++){
					columnsTableFile.readByte();
				}

				byte columnkeyLength = columnsTableFile.readByte();
				for(int i = 0; i < columnkeyLength ; i++){
					columnsTableFile.readByte();
				}

				byte isActive = columnsTableFile.readByte();

				if(table.equalsIgnoreCase(tableName) && isActive==01 ){
					dataTypes.add(type);
				}			
				schema="";
				table="";
				column="";
				type="";
			}
		}
		catch(EOFException e){
			return dataTypes;
		}
		catch(Exception e){
			e.printStackTrace();
			return dataTypes;
		}

	}
	public static ArrayList<String> getNullConstraint(String tableName) {
		ArrayList<String> isNullable = new ArrayList<>();
		try {
			String columnsfileName = "information_schema.columns.tbl";
			File columnsfileObject = new File(columnsfileName);
			long columnsfileLength = columnsfileObject.length();
			RandomAccessFile columnsTableFile = new RandomAccessFile(columnsfileObject, "rw");

			String schema = new String();
			String table = new String();
			String column = new String();
			String type = new String();
			String isNull = new String();

			while(true){
				byte schemaLength = columnsTableFile.readByte();
				for(int i = 0; i < schemaLength; i++){
					schema += (char)columnsTableFile.readByte();
				}

				byte tableLength = columnsTableFile.readByte();
				for(int i = 0; i < tableLength; i++){
					table += (char)columnsTableFile.readByte();
				}

				byte columnLength = columnsTableFile.readByte();
				for(int i = 0; i < columnLength; i++){
					columnsTableFile.readByte();
				}

				int position = columnsTableFile.readInt();

				byte typeLength = columnsTableFile.readByte();
				for(int i = 0; i < typeLength; i++){
					columnsTableFile.readByte();
				}

				byte isNullableLength = columnsTableFile.readByte();
				for(int i = 0; i < isNullableLength ; i++){
					if(table.equalsIgnoreCase(tableName))
						isNull += (char)columnsTableFile.readByte();
					else
						columnsTableFile.readByte();
				}

				byte columnkeyLength = columnsTableFile.readByte();
				for(int i = 0; i < columnkeyLength ; i++){
					columnsTableFile.readByte();
				}

				byte isActive = columnsTableFile.readByte();

				if(table.equalsIgnoreCase(tableName) && isActive==01 ){
					isNullable.add(isNull);
				}			
				schema="";
				table="";
				column="";
				type="";
				isNull="";
			}
		}catch(EOFException e){
			return isNullable;
		}
		catch(Exception e){
			e.printStackTrace();
			return isNullable;
		}

	}

	public static ArrayList<String> getPrimaryKeyConstraint(String tableName) {
		ArrayList<String> columnKey = new ArrayList<>();
		try {
			String columnsfileName = "information_schema.columns.tbl";
			File columnsfileObject = new File(columnsfileName);
			long columnsfileLength = columnsfileObject.length();
			RandomAccessFile columnsTableFile = new RandomAccessFile(columnsfileObject, "rw");

			String schema = new String();
			String table = new String();
			String column = new String();
			String type = new String();
			String isNull = new String();
			String pk = new String();

			while(true){
				byte schemaLength = columnsTableFile.readByte();
				for(int i = 0; i < schemaLength; i++){
					schema += (char)columnsTableFile.readByte();
				}

				byte tableLength = columnsTableFile.readByte();
				for(int i = 0; i < tableLength; i++){
					table += (char)columnsTableFile.readByte();
				}

				byte columnLength = columnsTableFile.readByte();
				for(int i = 0; i < columnLength; i++){
					columnsTableFile.readByte();
				}

				int position = columnsTableFile.readInt();

				byte typeLength = columnsTableFile.readByte();
				for(int i = 0; i < typeLength; i++){
					columnsTableFile.readByte();
				}

				byte isNullableLength = columnsTableFile.readByte();
				for(int i = 0; i < isNullableLength ; i++){
					columnsTableFile.readByte();
				}

				byte columnkeyLength = columnsTableFile.readByte();
				for(int i = 0; i < columnkeyLength ; i++){
					if(table.equalsIgnoreCase(tableName))
						pk += (char)columnsTableFile.readByte();
					else
						columnsTableFile.readByte();
				}

				byte isActive = columnsTableFile.readByte();

				if(table.equalsIgnoreCase(tableName) && isActive==01 ){
					columnKey.add(pk);
				}			
				schema="";
				table="";
				column="";
				type="";
				pk="";
			}
		} catch(EOFException e){
			return columnKey;
		}
		catch(Exception e){
			e.printStackTrace();
			return columnKey;
		}

	}
}
