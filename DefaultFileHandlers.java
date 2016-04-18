import java.io.*;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
public class DefaultFileHandlers{
	public static void insertIntoInformationSchemaTables(String table){
		String tablesfileName = "information_schema.table.tbl";
		int rows=0;
		try{
			File tablesfileObject = new File(tablesfileName);
			RandomAccessFile tablesTableFile = new RandomAccessFile(tablesfileObject, "rw");
			long tablesfileLength = tablesfileObject.length();

			tablesTableFile.seek(tablesfileLength);
			tablesTableFile.writeByte(CrazyBase.SCHEMA.length());
			tablesTableFile.writeBytes(CrazyBase.SCHEMA);
			tablesTableFile.writeByte(table.length());
			tablesTableFile.writeBytes(table);
			tablesTableFile.writeLong(0);
			tablesTableFile.writeByte(01);
		}catch(IOException E){
			E.printStackTrace();
		}

	}

	public static void insertIntoInformationSchemaColumns(String tableName, String columnName, Integer position, String datatypeName, String isNullable, String columnKey){

		try{
			String columnsfileName = "information_schema.columns.tbl";
			File columnsfileObject = new File(columnsfileName);
			long columnsfileLength = columnsfileObject.length();
			RandomAccessFile columnsTableFile = new RandomAccessFile(columnsfileObject, "rw");

			columnsTableFile.seek(columnsfileLength);
			columnsTableFile.writeByte(CrazyBase.SCHEMA.length());
			columnsTableFile.writeBytes(CrazyBase.SCHEMA);
			columnsTableFile.writeByte(tableName.length());
			columnsTableFile.writeBytes(tableName);
			columnsTableFile.writeByte(columnName.length());
			columnsTableFile.writeBytes(columnName);
			columnsTableFile.writeInt(position);
			columnsTableFile.writeByte(datatypeName.length());
			columnsTableFile.writeBytes(datatypeName);
			columnsTableFile.writeByte(isNullable.length());
			columnsTableFile.writeBytes(isNullable);
			columnsTableFile.writeByte(columnKey.length());
			columnsTableFile.writeBytes(columnKey);
			columnsTableFile.writeByte(01);
		}catch(IOException E){

		}
	}
	public static void unsetActiveBitInTablestable(String tableName) {
		RandomAccessFile tablesTableFile;
		String schema= new String();
		String table = new String();
		try {
			tablesTableFile = new RandomAccessFile("information_schema.table.tbl", "rw");
			while(true){
				byte schemaLength = tablesTableFile.readByte();
				for(int i = 0; i < schemaLength; i++){
					schema += (char)tablesTableFile.readByte();
				}
				byte tableLength = tablesTableFile.readByte();
				for(int i = 0; i < tableLength; i++){
					table += (char)tablesTableFile.readByte();
				}
				long rows = tablesTableFile.readLong();
				if(table.equals(tableName)){
					tablesTableFile.writeByte(00);
				}
				else{
					tablesTableFile.readByte();
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

	public static void unsetActiveBitInColumnstable(String tableName) {
		try {
			String columnsfileName = "information_schema.columns.tbl";
			File columnsfileObject = new File(columnsfileName);
			long columnsfileLength = columnsfileObject.length();
			RandomAccessFile columnsTableFile = new RandomAccessFile(columnsfileObject, "rw");

			String table = new String();

			while(true){
				byte schemaLength = columnsTableFile.readByte();
				for(int i = 0; i < schemaLength; i++){
					columnsTableFile.readByte();
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
					columnsTableFile.readByte();
				}

				if(table.equalsIgnoreCase(tableName)){
					columnsTableFile.writeByte(00);
				}else{
					columnsTableFile.readByte();
				}

				table = "";
			}
		} catch(EOFException e){
			return;
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}

