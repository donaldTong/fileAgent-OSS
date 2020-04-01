package com.fld.central.file.dto;

import java.io.InputStream;

/** 
 * 文件Dto
 * @Title: FileDto.java 
 * @Package com.fld.central.file.dto 
 * @author donald 
 * @date 2020年1月7日 下午2:53:00 
 * @version V1.0 
 */
public class FileDto {
	
	private byte[] fileBytes;
	
	private String fileName; 
	
	private Boolean rename;
	
	private InputStream input;

	public byte[] getFileBytes() {
		return fileBytes;
	}

	public void setFileBytes(byte[] fileBytes) {
		this.fileBytes = fileBytes;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Boolean getRename() {
		return rename;
	}

	public void setRename(Boolean rename) {
		this.rename = rename;
	}

	public InputStream getInput() {
		return input;
	}

	public void setInput(InputStream input) {
		this.input = input;
	}
	
}
