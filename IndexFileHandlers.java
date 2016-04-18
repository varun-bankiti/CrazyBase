import java.io.*;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
public class IndexFileHandlers{
	public static void insertIntoIndexFile(String datatype, String value, String columnName, String tableName, long offset) {
		Map<Object, ArrayList<Long>> index = new HashMap<Object, ArrayList<Long>>();
		Map<Object, ArrayList<Long>> result = new HashMap<Object, ArrayList<Long>>();
		try{
			String indexTableFileName = CrazyBase.SCHEMA+"."+tableName+"."+columnName+".ndx";
			File indexTableFileObject = new File(indexTableFileName);
			long indexfileLength = indexTableFileObject.length();
			RandomAccessFile indexTableFile = new RandomAccessFile(indexTableFileObject, "rw");
			boolean isValuePresent = false;
			if(indexfileLength>0){
				index = getIndexFileEntries(indexTableFileName, datatype);
				Set s = index.entrySet();
				Iterator it1 = s.iterator();
				Set set1 = index.entrySet();
				Iterator iterator1 = set1.iterator();
				while(iterator1.hasNext()) {
					Map.Entry me2 = (Map.Entry)iterator1.next();
					Object key = me2.getKey();
					ArrayList<Long> temp = new ArrayList<Long>();
					if(key.toString().equals(value)){
						isValuePresent = true;
						temp = (ArrayList<Long>) me2.getValue();
						long frequency = temp.get(0);
						frequency++;
						temp.set(0, frequency);
						temp.add(offset);
						index.put(key, temp);
						break;
					}
				}
				if(isValuePresent == false){
					ArrayList<Long> temp = new ArrayList<Long>();
					temp.add(Long.parseLong("01"));
					temp.add(offset);
					Object key = value; 
					index.put(key,temp);
					Set set2 = index.entrySet();
					Iterator iterator2 = set2.iterator();
					writeMapToIndexFile(tableName, columnName, datatype, index);
				}else{
					Set set = index.entrySet();
					Iterator iterator = set.iterator();
					writeMapToIndexFile(tableName,columnName, datatype, index);
				}

			}else{
				if(datatype.equals("int")){
					indexTableFile.writeInt(Integer.parseInt(value));
				}else if(datatype.equals("float")){
					indexTableFile.writeFloat(Float.parseFloat(value));
				}else if(datatype.equals("long") || datatype.equals("long int")){
					indexTableFile.writeLong(Long.parseLong(value));
				}else if(datatype.equals("double")){
					indexTableFile.writeDouble(Double.parseDouble(value));
				}else if(datatype.contains("varchar")){
					indexTableFile.writeByte(value.length());
					indexTableFile.writeBytes(value);
				}else if(datatype.equals("short") || datatype.equals("short int")){
					indexTableFile.writeShort(Short.parseShort(value));     
				}else if(datatype.equals("date")){
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
					String dateInString = value;
					Date date = formatter.parse(dateInString);
					long dateInLong = date.getTime();
					indexTableFile.writeLong(dateInLong);     
				}else if(datatype.equals("date")){
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
					String dateInString = value;
					Date date = formatter.parse(dateInString);
					long dateInLong = date.getTime();
					indexTableFile.writeLong(dateInLong);     
				}else{
					System.out.println(datatype);
				}
				indexTableFile.writeLong(Long.parseLong("01"));
				indexTableFile.writeLong(offset);
			}

		}catch(Exception e){
			e.printStackTrace();
		}

	}

	public static void writeMapToIndexFile(String tableName, String columnName, String datatype, Map<Object, ArrayList<Long>> index) {
		Map<Object, ArrayList<Long>> sorted_index = new TreeMap<Object, ArrayList<Long>>();
		
		Set set = index.keySet();
		Iterator iterator = set.iterator();
		while(iterator.hasNext()) {
			Object key = iterator.next();
			sorted_index.put(key.toString(), index.get(key));
		}		
		try{
			String indexTableFileName = CrazyBase.SCHEMA+"."+tableName+"."+columnName+".ndx";
			File indexTableFileObject = new File(indexTableFileName);
			long indexfileLength = indexTableFileObject.length();
			RandomAccessFile indexTableFile = new RandomAccessFile(indexTableFileObject, "rw");
			indexTableFile.setLength(0);
			Object key = new Object();
			Long frequency;
			Long offset = new Long(0);
			Long increment = new Long(0);
			set = sorted_index.entrySet();
			iterator = set.iterator();
			while(iterator.hasNext()) {
				indexTableFile.seek(offset);
				Map.Entry me2 = (Map.Entry)iterator.next();
				key = me2.getKey();
				if(datatype.equals("int")){
					int a = Integer.parseInt(key.toString());
					indexTableFile.writeInt(a);
				}else if(datatype.equals("float")){
					indexTableFile.writeFloat(Float.parseFloat(key.toString()));
				}else if(datatype.equals("long") || datatype.equals("long int")){
					indexTableFile.writeLong(Long.parseLong(key.toString()));
				}else if(datatype.equals("double")){
					indexTableFile.writeDouble(Double.parseDouble(key.toString()));
				}else if(datatype.contains("varchar")){
					String temp = (String)key;
					indexTableFile.writeByte(temp.length());
					indexTableFile.writeBytes(temp);
				}else if(datatype.equals("short") || datatype.equals("short int")){
					indexTableFile.writeShort(Short.parseShort(key.toString()));
				}else if(datatype.equals("date")){
					if(key.toString().trim().contains("-")){
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
						String dateInString = key.toString();
						Date date = formatter.parse(dateInString);
						long dateInLong = date.getTime();
						indexTableFile.writeLong(dateInLong);
					}else if(datatype.equals("datetime")){
						if(key.toString().trim().contains("-")){
							System.out.println("date " + key.toString());
							SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
							String dateInString = key.toString();
							Date date = formatter.parse(dateInString);
							long dateInLong = date.getTime();
							indexTableFile.writeLong(dateInLong);
						}else{
							indexTableFile.writeLong(Long.parseLong(key.toString()));
						}
					}else{
						indexTableFile.writeLong(Long.parseLong(key.toString()));
					}
				}else{
					System.out.println(datatype);
				}

				ArrayList<Long> value = (ArrayList<Long>) me2.getValue();
				frequency = value.get(0);
				indexTableFile.writeLong(frequency);
				for(int i=1 ; i<value.size() ; i++){
					indexTableFile.writeLong(value.get(i));
				}
				offset = indexTableFile.getFilePointer();
			}		
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static Map<Object, ArrayList<Long>> getIndexFileEntries(String indexTableFileName, String datatype) {
		Map<Object, ArrayList<Long>> index = new HashMap<Object, ArrayList<Long>>();
		try{
			File indexTableFileObject = new File(indexTableFileName);
			long indexfileLength = indexTableFileObject.length();
			RandomAccessFile indexTableFile = new RandomAccessFile(indexTableFileObject, "rw");
			Object key = new Object();
			Long frequency = new Long(0);
			ArrayList<Long> value = new ArrayList<Long>();
			while(true){
				if(datatype.equals("int")){
					key = indexTableFile.readInt();
				}else if(datatype.equals("float")){
					key = indexTableFile.readFloat();
				}else if(datatype.equals("long") || datatype.equals("long int")){
					key = indexTableFile.readLong();
				}else if(datatype.equals("double")){
					key = indexTableFile.readDouble();
				}else if(datatype.contains("varchar")){
					int length = indexTableFile.readByte();
					String temp = new String();
					for(int i=0 ; i<length ; i++){
						temp += (char)indexTableFile.readByte();
					}
					key = temp;
				}else if(datatype.equals("short") || datatype.equals("short int")){
					key = indexTableFile.readShort();
				}else if(datatype.equals("date")){
					key = indexTableFile.readLong();
				}else{
				}

				frequency = indexTableFile.readLong();
				value.add(frequency);
				for(long i=0 ; i<frequency ; i++){
					value.add(indexTableFile.readLong());
				}
				index.put(key, value);
				value= new ArrayList<Long>();
			}
		}
		catch(EOFException e){
			return index;
		}
		catch(Exception e){
			return index;
		}
	}

	public static long getByteSize(String datatype, String value){
		if(datatype.equals("int")){
			return 4;
		}else if(datatype.equals("float")){
			return 4;
		}else if(datatype.equals("long") || datatype.equals("long int")){
			return 8;
		}else if(datatype.equals("double")){
			return 4;
		}else if(datatype.contains("varchar")){
			return (value.length()+1);
		}else if(datatype.equals("short") || datatype.equals("short int")){
			return 2;
		}else if(datatype.equals("date")){
			return 8;
		}else{
			return 0;
		}
	}
		public static void locateRecordInDatafile(String value, Object key, ArrayList<Long> location, String tableName,
			ArrayList<String> columns, ArrayList<String> datatypes, Integer position) {
		try{
			String dataFileName = CrazyBase.SCHEMA+"."+tableName+"."+"dat";
			File datafileObject = new File(dataFileName);
			RandomAccessFile dataTableFile = new RandomAccessFile(datafileObject, "rw");
			Long frequency = location.get(0);
			for(int i=1 ; i <= frequency ; i++){
				dataTableFile.seek(location.get(i));
				for(int j=0 ; j<columns.size();j++){
					String datatype = datatypes.get(j);
					if(datatype.equals("int")){
						key = dataTableFile.readInt();
						System.out.print(" " + key + " |");
					}else if(datatype.equals("float")){
						key = dataTableFile.readFloat();
						System.out.print(" " + key + " |");
					}else if(datatype.equals("long") || datatype.equals("long int")){
						key = dataTableFile.readLong();
						System.out.print(" " + key + " |");
					}else if(datatype.equals("double")){
						key = dataTableFile.readDouble();
						System.out.print(" " + key + " |");
					}else if(datatype.contains("varchar")){
						int length = dataTableFile.readByte();
						String temp = new String();
						for(int k=0 ; k<length ; k++){
							temp += (char)dataTableFile.readByte();
						}
						key = temp;
						System.out.print(" " + key + " |");
					}else if(datatype.equals("short") || datatype.equals("short int")){
						key = dataTableFile.readShort();
						System.out.print(" " + key + " |");     
					}else if(datatype.equals("date")){
						key = dataTableFile.readLong();
						String x = key.toString();
						DateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
						long milliSeconds= Long.parseLong(x);
						Calendar calendar = Calendar.getInstance();
						calendar.setTimeInMillis(milliSeconds);
						System.out.print("  " + formatter.format(calendar.getTime())+ " |"); 
					}else{
						System.out.println(datatype);
					}		
				}
				System.out.println();
				System.out.println(CrazyBase.line("*",50));
			}
		}catch(Exception e){
			e.printStackTrace();

		}
	}
}
