package com.fld.central.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.fld.central.file.dto.FileDto;

/**
 * 
 * @description 文件工具
 * @author tanglijun_yunlai
 * @version
 * @date: 2018年6月13日 上午11:49:57
 */
public class FileUtil {

	private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);
	
	/**
	 * 上传文件
	 * 
	 * @param fileBytes
	 *            文件数据
	 * @param fileName
	 *            文件名
	 * @return
	 */
	public static String uploadFile(FileDto fileDto) {
		
		logger.info("BUCKETNAME={},file={}", OSSMgrFactory.ALIYUN_OSS_BUCKETNAME ,JSON.toJSONString(fileDto.getFileName()));
		
		OSSMgrFactory oSSMgrFactory = new OSSMgrFactory();
		
		String key = oSSMgrFactory.upload(OSSMgrFactory.ALIYUN_OSS_BUCKETNAME, fileDto.getFileBytes(), fileDto.getFileName(), fileDto.getRename());

		if (StringUtils.isBlank(key)) {
			return null;
		}

		String src = OSSMgrFactory.ALIYUN_OSS_ACCESSURL.replace("{bucketName}", OSSMgrFactory.ALIYUN_OSS_BUCKETNAME) + "/" + key;

		return src;
	}

	/**
	 * 下载文件
	 * 
	 * @param downloadFilename
	 *            下载文件路径
	 * @param src
	 *            源文件uri
	 * @return
	 */
	public Path downloadFile(String downloadFilename, String src) {
		Path path = Paths.get(downloadFilename);
		InputStream is = null;
		try {
			Files.createFile(path);
			OSSMgrFactory oSSMgrFactory = new OSSMgrFactory();
			is = oSSMgrFactory.getFile(OSSMgrFactory.ALIYUN_OSS_BUCKETNAME, OSSMgrFactory.ALIYUN_OSS_ACCESSURL, src);
			Files.copy(is, path);
		} catch (Exception e) {
			logger.error("[OSS]: 文件下载失败", e);
		} finally {
			closeInputStream(is);
		}

		return path;
	}


	public void downloadFile(String downloadPath,String filename, String src) {
		InputStream is = null;
		try {
			File file = new File(downloadPath,filename);
			OSSMgrFactory oSSMgrFactory = new OSSMgrFactory();
			is = oSSMgrFactory.getFile(OSSMgrFactory.ALIYUN_OSS_BUCKETNAME, OSSMgrFactory.ALIYUN_OSS_ACCESSURL, src);
			FileUtils.copyInputStreamToFile(is,file);

		} catch (Exception e) {
			logger.error("[OSS]: 文件下载失败", e);
		} finally {
			IOUtils.closeQuietly(is);
		}

	}


	/**
	 * 压缩目录
	 * 
	 * @param inputDir
	 *            目录文件
	 * @param outputZipFile
	 *            压缩文件
	 */
	public void zipDirectory(File inputDir, File outputZipFile) {
		try {
			List<File> files = this.listChildFiles(inputDir);
			zipDirectory(files, outputZipFile);
		} catch (IOException e) {
			logger.debug("[文件压缩]: 压缩失败");
		}
	}

	/**
	 * 压缩多文件
	 * 
	 * @param files
	 *            多文件
	 * @param outputZipFile
	 *            压缩文件
	 */
	public void zipDirectory(List<File> files, File outputZipFile) {
		outputZipFile.getParentFile().mkdirs();

		byte[] buffer = new byte[1024];

		FileOutputStream fileOs = null;
		ZipOutputStream zipOs = null;
		try {
			fileOs = new FileOutputStream(outputZipFile);
			zipOs = new ZipOutputStream(fileOs, Charset.forName("UTF-8"));

			for (File file : files) {
				String filePath = file.getAbsolutePath();

				logger.debug("[文件压缩]: Zipping filePath={}", filePath);

				String entryName = file.getName();

				ZipEntry ze = new ZipEntry(entryName);

				zipOs.putNextEntry(ze);

				FileInputStream fileIs = new FileInputStream(filePath);

				int len;
				while ((len = fileIs.read(buffer)) > 0) {
					zipOs.write(buffer, 0, len);
				}
				fileIs.close();
			}
		} catch (IOException e) {
			logger.debug("[文件压缩]: 压缩失败");
		} finally {
			closeOutputStream(zipOs);
			closeOutputStream(fileOs);
		}
	}

	private List<File> listChildFiles(File dir) throws IOException {
		List<File> allFiles = new ArrayList<File>();

		File[] childFiles = dir.listFiles();
		for (File file : childFiles) {
			if (file.isFile()) {
				allFiles.add(file);
			} else {
				List<File> files = this.listChildFiles(file);
				allFiles.addAll(files);
			}
		}
		return allFiles;
	}

	private void closeInputStream(InputStream is) {
		try {
			if (is != null)
				is.close();
		} catch (Exception e) {
		}
	}

	private void closeOutputStream(OutputStream out) {
		try {
			if (out != null)
				out.close();
		} catch (Exception e) {
		}
	}

}
