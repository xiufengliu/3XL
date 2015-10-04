/*
 *
 * Copyright (c) 2011, Xiufeng Liu (xiliu@cs.aau.dk) and the eGovMon Consortium
 * Permission to use, copy, modify, and distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *
 *
 */

package xlclient.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * 
 * @author xiliu
 */
public class Utility {
	
	public static String getParam(String s){
		return substrByCh(s, "?");
	}
	
	public static boolean isParam(String s){
		return s.startsWith("?");
	}
	
	public static String removeNS(String uri) {
		int i = uri.length() - 1;
		while (i >= 0) {
			char c = uri.charAt(i);
			if (c == '#' || c == ':' || c == '/') {
				break;
			}
			i--;
		}
		if (i > 0) {
			return uri.substring(i + 1).toLowerCase();
		} else {
			throw new IllegalArgumentException("'" + uri + "' is not a legal (absolute) URI");
		}
	}

	public static String removeNS2(String uri) {
		int i = uri.length() - 1;
		while (i >= 0) {
			char c = uri.charAt(i);
			if (c == '#' || c == ':' || c == '/') {
				break;
			}
			i--;
		}
		if (i > 0) {
			return uri.substring(i + 1).toLowerCase();
		} else {
			return uri;
		}
	}



	public static String removePgIllegalChar(String str) {
		int sz = str.length();
		char[] chs = new char[sz];
		int count = 0;
		for (int i = 0; i < sz; i++) {
			char ch = str.charAt(i);
			if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || ch >= '0' && ch <= '9') {
				chs[count++] = ch;
			}
		}
		if (count == sz) {
			return str.toLowerCase();
		}
		return new String(chs, 0, count).toLowerCase();
	}
	

	
	public static String substrByCh(String s, String ch) {
		int idx = s.lastIndexOf(ch);
		if (idx > -1) {
			return s.substring(idx + 1);
		} else {
			return s;
		}
	}

public static String getLocalIPAddr(){
	try {
	    InetAddress addr = InetAddress.getLocalHost();

	    // Get IP Address
	   return addr.getHostAddress();
	} catch (UnknownHostException e) {
		return null;
	}
}
	
	public static String hex(byte[] array) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < array.length; ++i) {
			sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).toUpperCase().substring(1, 3));
		}
		return sb.toString();
	}


public static  String[] runCommand(String cmd)  throws IOException
 {  
    // The actual procedure for process execution:
   // runCommand(String cmd);
    
    // Create a list for storing  output.
    ArrayList list = new ArrayList(); 

    // Execute a command and get its process handle
    Process proc = Runtime.getRuntime().exec(cmd); 

    // Get the handle for the processes InputStream
    InputStream istr = proc.getInputStream(); 

    // Create a BufferedReader and specify it reads 
    // from an input stream.
    BufferedReader br = new BufferedReader(new InputStreamReader(istr));
    String str; // Temporary String variable

    // Read to Temp Variable, Check for null then 
    // add to (ArrayList)list
    while ((str = br.readLine()) != null){ 
    	list.add(str);
    }
    // Wait for process to terminate and catch any Exceptions.
    try { 
    	proc.waitFor(); 
    } 
    catch (InterruptedException e) {
      System.err.println("Process was interrupted"); }

    // Note: proc.exitValue() returns the exit value. 
    // (Use if required)

    br.close(); // Done.

    // Convert the list to a string and return
    return (String[])list.toArray(new String[0]); 
 }

	
public static void reverse(char[] b) {
	   int left  = 0;          // index of leftmost element
	   int right = b.length-1; // index of rightmost element
	  
	   while (left < right) {
	      // exchange the left and right elements
	      char temp = b[left]; 
	      b[left]  = b[right]; 
	      b[right] = temp;
	     
	      // move the bounds toward the center
	      left++;
	      right--;
	   }
	}//endmethod reverse


	public static String md5(String source) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] bytes = md.digest(source.getBytes());
			return hex(bytes);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static int hashCode(String source) {
		return source.hashCode();
	}

	public static String binaryString(String source) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] bytes = md.digest(source.getBytes());
			String str = "";
			for (int i = 0; i < bytes.length; ++i)
				str += String.format("\\%03o", bytes[i] & 0xff);
			return str;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static byte[] toBinary(String source) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] bytes = md.digest(source.getBytes());
			return bytes;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Set<String> mp2Set(String mpvalue) {
		Set<String> mpSet = new HashSet<String>();
		if (mpvalue != null) {
			String strValue = (mpvalue.replace("{", "")).replace("}", "");
			String[] values = strValue.split(",");
			for (int i = 0; i < values.length; ++i) {
				if (!"".equals(values[i]))
					mpSet.add(values[i]);
			}
		}
		return mpSet;
	}

	
	public static List<String> mp2List1(Object mpvalue) {
		if (mpvalue != null) {
			List<String> mpSet = new ArrayList<String>();
			String strMpValue = String.valueOf(mpvalue);
			String strValue = (strMpValue.replace("{", "")).replace("}", "");
			String[] values = strValue.split(",");
			for (int i = 0; i < values.length; ++i) {
				if (!isEmpty(values[i]) && !mpSet.contains(values[i]))
					mpSet.add(values[i]);
			}
			if (mpSet.size()>0)
				return mpSet;
		} 
		return null;
	
	}
	
	
	public static <E> String list2StrByDelim(List<E> list, String delim) {
		String str = "";
		if (list == null || list.size() == 0)
			return str;
		else {
			for (E e : list) {
				if (!isEmpty(String.valueOf(e))) {
					str += e + delim;
				}
			}
			return rtrim(str, delim);
		}
	}

	public static String array2StrByDelim(String[] array, String delim) {
		StringBuilder strBuilder = new StringBuilder();
		int len = 0;
		if (array == null || (len = array.length) == 0)
			return "";
		else {
			for (int i = 0; i < len; ++i) {
				if (array[i] != null && array[i].length() > 0) {
					strBuilder.append(array[i]);
					if (i != len - 1) {
						strBuilder.append(delim);
					}
				}
			}
			return strBuilder.toString();
		}
	}

	
	public static String rtrim(String str, String ch) {
		if (str == null)
			return "";
		int idx = str.lastIndexOf(ch);
		if (idx != -1)
			return str.substring(0, idx);
		else
			return str;
	}

	public static String stripLeadingAndTrailingQuotes(String str)
	  {
	      while (str.startsWith("\"") || str.startsWith("\'"))
	      {
	          str = str.substring(1, str.length());
	      }
	      while (str.endsWith("\"") || str.endsWith("\'"))
	      {
	          str = str.substring(0, str.length() - 1);
	      }
	      return str;
	  }

	
	
	  public static String trim(String s, String c){
		    int length = s.length();
		    if (c == null){
		      return s;
		    }
		    int cLength = c.length();
		    if (c.length() == 0){
		      return s;
		    }
		    int start = 0;
		    int end = length;
		    boolean found; // trim-able character found.
		    int i;
		    // Start from the beginning and find the
		    // first non-trim-able character.
		    found = false;
		    for (i=0; !found && i<length; i++){
		      char ch = s.charAt(i);
		      found = true;
		      for (int j=0; found && j<cLength; j++){
		        if (c.charAt(j) == ch) found = false;
		      }
		    }
		    // if all characters are trim-able.
		    if (!found) return "";
		    start = i-1;
		    // Start from the end and find the
		    // last non-trim-able character.
		    found = false;
		    for (i=length-1; !found && i>=0; i--){
		      char ch = s.charAt(i);
		      found = true;
		      for (int j=0; found && j<cLength; j++){
		        if (c.charAt(j) == ch) found = false;
		      }
		    }
		    end = i+2;
		    return s.substring(start, end);
		  }
	
	 public static String stripLineBreaks(String string, String replaceWith) {


	      int len = string.length();
	      StringBuffer buffer = new StringBuffer(len);
	      for (int i = 0; i < len; i++) {
	          char c = string.charAt(i);

	          // skip \n, \r, \r\n
	          switch (c) {
	              case '\n':
	              case '\r': // do lookahead
	                  if (i + 1 < len && string.charAt(i + 1) == '\n') {
	                      i++;
	                  }

	                  buffer.append(replaceWith);
	                  break;
	              default:
	                  buffer.append(c);
	          }
	      }

	      return buffer.toString();
	  }
	
	 
	 public static String removeSpecialChar(Object str, char[] chars) {
		if (str==null) return null;
		String string = String.valueOf(str);
		int len = string.length();
		StringBuffer buffer = new StringBuffer(len);
		boolean toremove = false;
		for (int i = 0; i < len; i++) {
			char c = string.charAt(i);
			toremove = false;
			for (char cc: chars){
				if (c==cc)  {
					toremove = true;
					break;
				}
			}
			if (!toremove)
				buffer.append(c);
		}
		return buffer.length()==0? null: buffer.toString();
	  }
	
	 public static String rtrimSpecialChar(String str, char[] chars) {
			if (str==null) return null;
			int i = str.length()-1;
			for (; i>=0; --i){
				if (!contains(chars, str.charAt(i))){
					break;
				}
			}
			return str.substring(0, i+1);
		  }

	 
	 public static String trimSpecialChar(String str, char[] chars) {
			if (str==null) return null;
			String subStr = null;
			for (int i=0; i<str.length(); ++i){
				if (contains(chars, str.charAt(i))){
					continue;
				}
				subStr = str.substring(i);
				break;
			}
			return rtrimSpecialChar(subStr, chars);
		  }
	 
	 
	 public static boolean contains(char[] chars, char ch){
		 for (int c : chars){
			 if (ch==c) return true;
		 }
		 return false;
	 }
	 
	public static boolean isEmpty(String str) {
		return (str == null || "null".equalsIgnoreCase(str.trim()) || "".equals(str.trim()));
	}

	public static String mapEmpty2Null(String str) {

		if (str == null || "null".equalsIgnoreCase(str.trim()) || "".equals(str.trim()))
			return null;
		else
			return str;
	}

	public static String mapNull2Empty(String str) {
		if (str == null || "null".equalsIgnoreCase(str.trim())) {
			return "";
		} else {
			return str;
		}
	}

	public static String mapEmpty2Str(String src, String str) {
		if (src == null || "null".equalsIgnoreCase(src.trim()) || "".equals(src.trim()))
			return str;
		else
			return src;
	}

	public static String pad(String s, int length) {
		String padding = "                                                                                      ";
		return (s.length() < length ? s + padding : s).substring(0, length);
	}

	public static void checkFile(File f) throws XLException {
		IOException problem = null;
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				throw new XLException("Create file exception", problem);
			}
		}
	}

	public static void checkFile(String name) throws XLException {
		File file = new File(name);
		checkFile(file);
	}

	public static long toMilliseconds(long seconds, long nanos) {
		long result = seconds * 1000;
		result += nanos / 1000000;
		long temp = nanos % 1000000;
		if (temp > 0)
			result++;

		return result;
	}

	public static long toNanoseconds(long sec, long nanos) {
		long result = nanos;
		result = result + 1000000000 * sec;
		return result;
	}

	public static void closeNoExceptions(Reader reader) {
		if (null == reader)
			return;

		try {
			reader.close();
		} catch (IOException e) {
			; // ignore exceptions
		}
	}

	public static void closeNoExceptions(BufferedReader reader) {
		if (null == reader)
			return;

		try {
			reader.close();
		} catch (IOException e) {
			; // this method is supposed to ignore exceptions
		}
	}

	public static void closeNoExceptions(InputStream istream) {
		if (null == istream)
			return;

		try {
			istream.close();
		} catch (IOException e) {
			; // this method is supposed to ignore exceptions
		}
	}

	public static String readFile(String name) throws IOException {
		FileReader reader = null;

		try {
			reader = new FileReader(name);
			StringWriter sw = new StringWriter(1024);
			for (int c = reader.read(); c != -1; c = reader.read()) {
				sw.write(c);
			}

			return sw.toString();
		} catch (FileNotFoundException e) {
			return null;
		} finally {
			Utility.closeNoExceptions(reader);
		}
	}

	public static void writeFile(String name, String contents) throws IOException {
		FileWriter writer = null;

		try {
			writer = new FileWriter(name);
			writer.write(contents);
		} finally {
			Utility.closeNoExceptions(writer);
		}
	}

	public static void closeNoExceptions(FileWriter writer) {
		if (null == writer)
			return;

		try {
			writer.close();
		} catch (IOException e) {
			; // the point of this method is to avoid throwing exceptions
		}
	}

	public static Object toValueIgnoreCase(Object[] values, String s) {
		Object result = matchIgnoreCase(values, s);

		if (null == result) {
			String msg = s + " does not matching any tag name";
			throw new RuntimeException(msg);
		}

		return result;
	}

	public static Object matchIgnoreCase(Object[] values, String s) {
		for (Object tag : values) {
			if (s.equalsIgnoreCase(tag.toString()))
				return tag;
		}

		return null;
	}

	public static void closeNoExceptions(OutputStream ostream) {
		try {
			ostream.close();
		} catch (IOException e) {
			;
		}
	}

	static class byValueComparator implements Comparator<Object> {

		Map<String, Float> base_map;

		public byValueComparator(Map<String, Float> base_map) {
			this.base_map = base_map;
		}

		public int compare(Object arg0, Object arg1) {
			if (!base_map.containsKey(arg0) || !base_map.containsKey(arg1)) {
				return 0;
			}

			if (base_map.get(arg0) < base_map.get(arg1)) {
				return 1;
			} else if (base_map.get(arg0) == base_map.get(arg1)) {
				return 0;
			} else {
				return -1;
			}
		}
	}

	public static List<String> getSortedKeysByValue(Map<String, Float> base_map) {
		byValueComparator bvc = new byValueComparator(base_map);
		List<String> keys = new ArrayList<String>(base_map.keySet());
		Collections.sort(keys, bvc);
		return keys;
	}
	


	public static StringBuilder append(StringBuilder sb,
			Iterable<? extends Object> iterable, String prefix, String suffix,
			String sep) {
		Iterator<? extends Object> it = iterable.iterator();
		while (it.hasNext()) {
			if (prefix != null)
				sb.append(prefix);
			sb.append(it.next());
			if (suffix != null)
				sb.append(suffix);
			if (it.hasNext() && sep != null)
				sb.append(sep);
		}
		return sb;
	}

	  /** Creates deep copy of Map.
	    * All items will be cloned. Used internally in this object.
	    */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static HashMap deepMap(HashMap map) {
		HashMap newone = (HashMap) map.clone();
		Iterator it = newone.keySet().iterator();
		while (it.hasNext()) {
			Object newkey = it.next();
			Object deepobj = null, newobj = newone.get(newkey);
			if (newobj instanceof String)
				deepobj = (Object) new String((String) newobj);
			else  if (newobj instanceof ArrayList)
				deepobj = ((ArrayList) newobj).clone();
			newone.put(newkey, deepobj);
		}
		return newone;
	}
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List deepList(List list) {
		List clone = new ArrayList(list.size());
		for (Object item : list) {
			Object deepobj = null;
			if (item instanceof String)
				deepobj = (Object) new String((String) item);
			else if (item instanceof ArrayList)
				deepobj = deepList((ArrayList) item);
			clone.add(deepobj);
		}
		return clone;
	}
	
	


	

	
    public static int size(final Object o) throws IOException {
        if(o==null) {
            return 0;
        }
        ByteArrayOutputStream buf = new ByteArrayOutputStream(4096);
        ObjectOutputStream out = new ObjectOutputStream(buf);
        out.writeObject(o);
        out.flush();
        buf.close();

        return buf.size();
    }


    public static Object copy(final Object o) throws IOException, ClassNotFoundException {
        if(o==null) {
            return null;
        }

        ByteArrayOutputStream  outbuf = new ByteArrayOutputStream(4096);
        ObjectOutput out = new ObjectOutputStream(outbuf);
        out.writeObject(o);
        out.flush();
        outbuf.close();

        ByteArrayInputStream  inbuf = new ByteArrayInputStream(outbuf.toByteArray());
        ObjectInput  in = new ObjectInputStream(inbuf);
        return in.readObject();
    }

    public static void invokeClass(Class clasz, String name, String[] args)
    throws ClassNotFoundException,
           NoSuchMethodException,
           InvocationTargetException
{
    ClassLoader classLoader = clasz.getClassLoader();
    Class c = classLoader.loadClass(name);
    
    Method m = c.getMethod("main", new Class[] { args.getClass() });
    m.setAccessible(true);
    int mods = m.getModifiers();
    if (m.getReturnType() != void.class || !Modifier.isStatic(mods) ||
        !Modifier.isPublic(mods)) {
        throw new NoSuchMethodException("main");
    }
    try {
        m.invoke(null, new Object[] { args });
    } catch (IllegalAccessException e) {
        // This should not happen, as we have disabled access checks
    }
}
    
    /**
     * List directory contents for a resource folder. Not recursive.
     * This is basically a brute-force implementation.
     * Works for regular files and also JARs.
     * 
     * @author Greg Briggs
     * @param clazz Any java class that lives in the same place as the resources you want.
     * @param path Should end with "/", but not start with one.
     * @return Just the name of each member item, not the full paths.
     * @throws URISyntaxException 
     * @throws IOException 
     */
    public static String[] getResourceListing(Class clazz, String path) throws URISyntaxException, IOException {
        URL dirURL = clazz.getClassLoader().getResource(path);
        if (dirURL != null && dirURL.getProtocol().equals("file")) {
          /* A file path: easy enough */
          return new File(dirURL.toURI()).list();
        } 

        if (dirURL == null) {
          /* 
           * In case of a jar file, we can't actually find a directory.
           * Have to assume the same jar as clazz.
           */
          String me = clazz.getName().replace(".", "/")+".class";
          dirURL = clazz.getClassLoader().getResource(me);
        }
        
        if (dirURL.getProtocol().equals("jar")) {
          /* A JAR path */
          String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")); //strip out only the JAR file
          JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
          Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
          Set<String> result = new HashSet<String>(); //avoid duplicates in case it is a subdirectory
          while(entries.hasMoreElements()) {
            String name = entries.nextElement().getName();
            if (name.startsWith(path)) { //filter according to the path
              String entry = name.substring(path.length());
              int checkSubdir = entry.indexOf("/");
              if (checkSubdir >= 0) {
                // if it is a subdirectory, we just return the directory name
                entry = entry.substring(0, checkSubdir);
              }
              result.add(entry);
            }
          }
          return result.toArray(new String[result.size()]);
        } 
          
        throw new UnsupportedOperationException("Cannot list files for URL "+dirURL);
    }
    

	
    public static String unescapeString(String s) {
        int backSlashIdx = s.indexOf('\\');

        if (backSlashIdx == -1) {
                // No escaped characters found
                return s;
        }

        int startIdx = 0;
        StringBuffer buf = new StringBuffer(s.length());

        while (backSlashIdx != -1) {
                buf.append(s.substring(startIdx, backSlashIdx));
                char c = s.charAt(backSlashIdx + 1);

                if (c == 't') {
                        buf.append('\t');
                        startIdx = backSlashIdx + 2;
                }
                else if (c == 'r') {
                        buf.append('\r');
                        startIdx = backSlashIdx + 2;
                }
                else if (c == 'n') {
                        buf.append('\n');
                        startIdx = backSlashIdx + 2;
                }
                else if (c == '"') {
                        buf.append('"');
                        startIdx = backSlashIdx + 2;
                }
                else if (c == '\\') {
                        buf.append('\\');
                        startIdx = backSlashIdx + 2;
                }
                else if (c == 'u') {
                        // \\uxxxx
                        String xx = s.substring(backSlashIdx + 2, backSlashIdx + 6);
                        c = (char)Integer.parseInt(xx, 16);
                        buf.append( (char)c );

                        startIdx = backSlashIdx + 6;
                }
                else if (c == 'U') {
                        // \\Uxxxxxxxx
                        String xx = s.substring(backSlashIdx + 2, backSlashIdx + 10);
                        c = (char)Integer.parseInt(xx, 16);
                        buf.append( (char)c );

                        startIdx = backSlashIdx + 10;
                }
                else {
                        throw new IllegalArgumentException("Unescaped backslash in N-Triples string: " + s);
                }

                backSlashIdx = s.indexOf('\\', startIdx);
        }

        buf.append( s.substring(startIdx) );

        return buf.toString();
}

	
	
	
	public static void main(String[] args) {

		//System.out.println(Utility.removeSpecialChar("\"W/$''M'TAtODA4RC1\"DMTI$1NzIxQjAw\"MzJBRUQ$5LUMxMjU3MjhDMDAzQUU2ODktMA=='''", new char[]{'\'', '"'}));

		try{
			String value = "'abcd"; //"{'http://adfs?, dasfdk', 'dfsdk#kkdfal', 'fdasfs'}";
			int len = 0;
			if (value == null || (len = value.length()) == 0)
				return;
			char[] ch = value.toCharArray();
			if (len > 1 && ch[0] == '{' && ch[len - 1] == '}') {
				if ((len-2)==0) return;
				char[] mp = new char[len - 2];
				int cnt = 0, j=1;
				while( j < len-1) {
					if (ch[j] == '\'') {
						j++;
						while (ch[j] != '\'') {
							mp[cnt++] = ch[j++];
							if (j== len-1) {
								throw new Exception("Malformate!");
							}
						}
						System.out.printf("1. %s \n", new String(mp, 0, cnt));
						cnt = 0;
						j++;
					} else if (ch[j] != ',') {
						mp[cnt++] = ch[j++];
						if (j== len-1) {
							System.out.printf("2. %s \n", new String(mp, 0, cnt));
						}
					} else if (ch[j] == ',' && cnt > 0) {
						System.out.printf("3. %s \n", new String(mp, 0, cnt));
						j++;
						cnt = 0;
					} else {
						j++;
					}
				}
			} else {
				System.out.printf("4. %s \n", value);
			}
		} catch (Exception e){
			e.printStackTrace();
		}

		
	}
	
}



class FastByteArrayInputStream extends InputStream {
    /**
     * Our byte buffer
     */
    protected byte[] buf = null;

    /**
     * Number of bytes that we can read from the buffer
     */
    protected int count = 0;

    /**
     * Number of bytes that have been read from the buffer
     */
    protected int pos = 0;

    public FastByteArrayInputStream(byte[] buf, int count) {
        this.buf = buf;
        this.count = count;
    }

    public final int available() {
        return count - pos;
    }

    public final int read() {
        return (pos < count) ? (buf[pos++] & 0xff) : -1;
    }

    public final int read(byte[] b, int off, int len) {
        if (pos >= count)
            return -1;

        if ((pos + len) > count)
            len = (count - pos);

        System.arraycopy(buf, pos, b, off, len);
        pos += len;
        return len;
    }

    public final long skip(long n) {
        if ((pos + n) > count)
            n = count - pos;
        if (n < 0)
            return 0;
        pos += n;
        return n;
    }

}


class FastByteArrayOutputStream extends OutputStream {
    /**
     * Buffer and size
     */
    protected byte[] buf = null;
    protected int size = 0;

    /**
     * Constructs a stream with buffer capacity size 5K
     */
    public FastByteArrayOutputStream() {
        this(1 * 1024);
    }

    /**
     * Constructs a stream with the given initial size
     */
    public FastByteArrayOutputStream(int initSize) {
        this.size = 0;
        this.buf = new byte[initSize];
    }

    /**
     * Ensures that we have a large enough buffer for the given size.
     */
    private void verifyBufferSize(int sz) {
        if (sz > buf.length) {
            byte[] old = buf;
            buf = new byte[Math.max(sz, 2 * buf.length )];
            System.arraycopy(old, 0, buf, 0, old.length);
            old = null;
        }
    }

    public int getSize() {
        return size;
    }

    /**
     * Returns the byte array containing the written data. Note that this
     * array will almost always be larger than the amount of data actually
     * written.
     */
    public byte[] getByteArray() {
        return buf;
    }

    public final void write(byte b[]) {
        verifyBufferSize(size + b.length);
        System.arraycopy(b, 0, buf, size, b.length);
        size += b.length;
    }

    public final void write(byte b[], int off, int len) {
        verifyBufferSize(size + len);
        System.arraycopy(b, off, buf, size, len);
        size += len;
    }

    public final void write(int b) {
        verifyBufferSize(size + 1);
        buf[size++] = (byte) b;
    }

    public void reset() {
        size = 0;
    }

    /**
     * Returns a ByteArrayInputStream for reading back the written data
     */
    public InputStream getInputStream() {
        return new FastByteArrayInputStream(buf, size);
    }

}

