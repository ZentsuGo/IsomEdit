package com.zentsugo.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class FileManager {
	
	private FileReader isr;
	private FileWriter osr;
	private BufferedReader br;
	private BufferedWriter bw;
	private File file;
	private File path;
	
	public static void main(String[] args) {
		System.out.println("launched");
		
		FileManager fm = new FileManager(new File(System.getProperty("user.home"), "/Desktop/yo.txt"));
		//fm.write("Hey it's going crazy today!");
		fm.read();
		System.out.println("Writing...");
		fm.write("\r\nAu revoiree !");
		System.out.println("SECOND TURN :");
		fm.read();
	}
	
	public FileManager(File file) {
		this.file = file;
		this.path = new File(file.getAbsolutePath().replace(file.getName(), ""));
	}
	
	public void setup() {
		if (!(path.exists())) {
			path.mkdirs();
		}
		
		if (!(file.exists())) {
			try {
				file.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public Object readObject() {
		try {
			FileInputStream filein = new FileInputStream(file);
			ObjectInputStream objectIn = new ObjectInputStream(filein);
			Object object = objectIn.readObject();
			objectIn.close();
			return object;
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ArrayList<String> read() {
		ArrayList<String> lines = new ArrayList<String>();
        String line;
        
        openReader();
        
        try {
			while ((line = br.readLine()) != null) {
			    lines.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        finally {
        	closeReader();
        }

		return lines;
	}
	
	public String readToString() {
		String content = "";
		String line;
		
		openReader();
		
		try {
			while ((line = br.readLine()) != null) {
				content += line + "\n";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		finally {
			closeReader();
		}
		
		return content;
	}
	
	public void remove() {
		try {
			osr = new FileWriter(file); //without append mode to overwrite all
			bw = new BufferedWriter(osr);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try {
			bw.write("");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isEmpty() {
		return read() == null;
	}
	
	public void writeObject(Object object) {
        try {
        	FileOutputStream fileOut = new FileOutputStream(file, false);
        	ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
			objectOut.writeObject(object);
			objectOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public <K, V> void write(HashMap<K, V> texts) {
		for (Entry<K, V> entries : texts.entrySet()) {
			K key = entries.getKey();
			V value = entries.getValue();
			
			write(key + "," + value + "\n");
		}
	}
	
	public void write(ArrayList<String> texts) {
		for (int i = 0; i < texts.size(); i++) {
			write(texts.get(i));
		}
	}
	
	public void write(String text) {
		openWriter();
		
		try {
			bw.write(text.replaceAll("\n", "\r\n"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		finally {
			closeWriter();
		}
	}
	
	private void openReader() {
		try {
			isr = new FileReader(file);
			br = new BufferedReader(isr);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void closeReader() {
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void openWriter() {
		try {
			osr = new FileWriter(file, true);
			bw = new BufferedWriter(osr);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	private void closeWriter() {
		try {
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void newLine() {
		openWriter();
		
		try {
			bw.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		finally {
			closeWriter();
		}
	}
}
