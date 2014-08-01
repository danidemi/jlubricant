package com.danidemi.jlubricant.logback;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.sql.rowset.serial.SerialException;

import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSystemCache implements Cache {

	private static final Logger log = LoggerFactory.getLogger(FileSystemCache.class);
	
	private MemoryCache cache;
	private File file;
	
	public FileSystemCache() {
		
	}
	
	public void setFile(File file) {
		this.file = file;
	}
	
	public void setFilePath(String filePath){
		this.file = new File(filePath);
	}

	public void cacheEvict(long maxAgeInMillis) {
		ensure();
		cache.cacheEvict(maxAgeInMillis);
		store();
	}

	public Long timestampOfLastOccurence(String message) {
		ensure();
		return cache.timestampOfLastOccurence(message);
	}

	public void put(String message, long timestamp) {
		ensure();
		cache.put(message, timestamp);
		store();
	}

	public int hashCode() {
		ensure();
		return cache.hashCode();
	}

	public int itemsInCache() {
		ensure();
		return cache.itemsInCache();
	}

	public void clear() {
		ensure();
		cache.clear();
		store();
	}

	public void setMaxSize(int i) {
		ensure();
		cache.setMaxSize(i);
		store();
	}

	public boolean equals(Object obj) {
		ensure();
		return cache.equals(obj);
	}

	public String toString() {
		ensure();
		return cache.toString();
	}
	
	private synchronized void store() {
		try(FileOutputStream fileOutputStream = new FileOutputStream(file, false)){
			SerializationUtils.serialize(cache, fileOutputStream);			
		} catch (IOException e) {
			log.warn("A problem occurred accessing the cache file at " + file + ". For this reason, in case of application failure, same messages could be relogged. Cache will keep on working as a memory only cache.", e);
		}
	}
	
	private void ensure(){
		if(cache == null){
			cache = retrieve();
		}
	}
	
	private synchronized MemoryCache retrieve() {
		
		if(file==null){
			throw new IllegalStateException("Please provide a file where cache can be stored.");
		}
		

		
		MemoryCache deserialize = null;
		try(FileInputStream fileInputStream = new FileInputStream(file)){
			deserialize = SerializationUtils.deserialize(fileInputStream);			
			log.debug("Loaded cache from " + file.getAbsolutePath());
		} catch (FileNotFoundException e) {
			log.debug("Cache file " + file.getAbsolutePath() + " not present, so it's a new cache.");
		} catch (EOFException e){
			log.error("New empty file");	
		} catch (IOException e) {
			log.error("A problem occurred accessing the cache file at " + file + ". For this reason, in case of application failure, same messages could be relogged. Cache will keep on working as a memory only cache.", e);
		} catch(SerializationException se){
			Throwable cause = se.getCause();
			try{
				throw cause;			
			} catch (FileNotFoundException e) {
				log.debug("Cache file " + file.getAbsolutePath() + " not present, so it's a new cache.");
			} catch (EOFException e){
				log.error("New empty file");	
			} catch (IOException e) {
				log.error("A problem occurred accessing the cache file at " + file + ". For this reason, in case of application failure, same messages could be relogged. Cache will keep on working as a memory only cache.", e);
			} catch(Throwable t){
				log.error("An error occurred", t);				
			}
		} finally {
			if(deserialize==null) {
				deserialize = new MemoryCache();
			}
		}
		return deserialize;		
	}

}
