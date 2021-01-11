package com.xiao.mesh.central.file;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.UUID;

/**
 * 文件上传下载
 * 
 * @author summerrains
 *
 */
public class OSSMgrFactory {
	
	private static final Logger logger = LoggerFactory.getLogger(OSSMgrFactory.class);
	
	public static String ALIYUN_ACCESSKEYID;
	public static String ALIYUN_ACCESSKEYSECRET;
	public static String ALIYUN_OSS_ENDPOINT;
	public static String ALIYUN_OSS_ACCESSURL;
	public static String ALIYUN_OSS_BUCKETNAME;

	private OSSClient ossClient;
	
	public void init(String accessKeyId, String accessKeySecret, String endpoint, String accessUrl, String bucketName) {
		ALIYUN_ACCESSKEYID = accessKeyId;
		ALIYUN_ACCESSKEYSECRET = accessKeySecret;
		ALIYUN_OSS_ENDPOINT = endpoint;
		ALIYUN_OSS_ACCESSURL = accessUrl;
		ALIYUN_OSS_BUCKETNAME = bucketName;
//		Properties props = null;
//		try {
//			Resource[] resources = new PathMatchingResourcePatternResolver().getResources("classpath*:application.yml");
//			logger.debug("扫描文件:resources.size={}", resources.length);
//			props = PropertiesLoaderUtils.loadProperties(resources[(resources.length - 1)]);
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		if (props.containsKey("aliyun.oss.accessKeyId"))
//			ALIYUN_ACCESSKEYID = props.getProperty("aliyun.oss.accessKeyId");
//		else
//			ALIYUN_ACCESSKEYID = null;
//
//		if (props.containsKey("aliyun.oss.accessKeySecret"))
//			ALIYUN_ACCESSKEYSECRET = props.getProperty("aliyun.oss.accessKeySecret");
//		else
//			ALIYUN_ACCESSKEYSECRET = null;
//
//		if (props.containsKey("aliyun.oss.endpoint"))
//			ALIYUN_OSS_ENDPOINT = props.getProperty("aliyun.oss.endpoint");
//		else
//			ALIYUN_OSS_ENDPOINT = null;
//
//		if (props.containsKey("aliyun.oss.accessUrl"))
//			ALIYUN_OSS_ACCESSURL = props.getProperty("aliyun.oss.accessUrl");
//		else
//			ALIYUN_OSS_ACCESSURL = null;
//
//		if (props.containsKey("aliyun.oss.bucketName"))
//			ALIYUN_OSS_BUCKETNAME = props.getProperty("aliyun.oss.bucketName");
//		else
//			ALIYUN_OSS_BUCKETNAME = null;
	}

	public void destroy() {
		if (ossClient != null) {
			ossClient.shutdown();
		}
		ossClient = null;
	}

	/**
	 * 单例获取实例
	 * @return 
	 */
	private OSSClient getOSSClient() {
		if (ossClient == null) {
			synchronized (this) {
				if (ossClient == null) {
					ossClient = new OSSClient(ALIYUN_OSS_ENDPOINT, ALIYUN_ACCESSKEYID, ALIYUN_ACCESSKEYSECRET);
				}
			}
		}
		return ossClient;
	}

	/**
	 * 上传文件
	 * @param bucketName
	 * @param file
	 * @param rename
	 * @return 
	 */
	public String upload(String bucketName, String file, boolean rename) {
		String key = createFileName(file, rename);
		try {
			if (!getOSSClient().doesBucketExist(bucketName)) {
				getOSSClient().createBucket(bucketName);
			}
			getOSSClient().putObject(new PutObjectRequest(bucketName, key, new File(file)));
		} catch (OSSException | ClientException e) {
			logger.error("[OSS]: 上传失败", e);
			return null;
		}
		return key;
	}

	/**
	 * 上传文件 
	 * @param bucketName
	 * @param input
	 * @param originalName
	 * @param rename
	 * @return 
	 */
	public String upload(String bucketName, InputStream input, String originalName, boolean rename) {
		String key = createFileName(originalName, rename);
		try {
			if (!getOSSClient().doesBucketExist(bucketName)) {
				getOSSClient().createBucket(bucketName);
			}
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentType(contentType(originalName));
			getOSSClient().putObject(bucketName, key, input, metadata);
		} catch (OSSException | ClientException e) {
			logger.error("[OSS]: 上传失败", e);
			return null;
		}
		return key;
	}

	/**
	 * 上传文件
	 * @param bucketName
	 * @param fileBytes
	 * @param originalName
	 * @param rename
	 * @return 
	 */
	public String upload(String bucketName, byte[] fileBytes, String originalName, boolean rename) {
		String key = createFileName(originalName, rename);
		try {
			if (!getOSSClient().doesBucketExist(bucketName)) {
				getOSSClient().createBucket(bucketName);
			}
			getOSSClient().putObject(bucketName, key, new ByteArrayInputStream(fileBytes));
		} catch (OSSException | ClientException e) {
			logger.error("[OSS]: 上传失败", e);
			return null;
		}
		return key;
	}

	/**
	 * 获取协议类型
	 * @param originalName
	 * @return 
	 */
	private String contentType(String originalName) {
		int index = originalName.lastIndexOf(".");
		String ext = originalName.substring(index + 1);
		if (ext.equalsIgnoreCase("bmp")) {
			return "image/bmp";
		}
		if (ext.equalsIgnoreCase("gif")) {
			return "image/gif";
		}
		if (ext.equalsIgnoreCase("jpeg") || ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("png")) {
			return "image/jpeg";
		}
		if (ext.equalsIgnoreCase("html")) {
			return "text/html";
		}
		if (ext.equalsIgnoreCase("txt")) {
			return "text/plain";
		}
		if (ext.equalsIgnoreCase("vsd")) {
			return "application/vnd.visio";
		}
		if (ext.equalsIgnoreCase("pptx") || ext.equalsIgnoreCase("ppt")) {
			return "application/vnd.ms-powerpoint";
		}
		if (ext.equalsIgnoreCase("docx") || ext.equalsIgnoreCase("doc")) {
			return "application/msword";
		}
		if (ext.equalsIgnoreCase("xml")) {
			return "text/xml";
		}
		return "text/html";
	}

	/**
	 * 创建文件名称
	 * @param originalName
	 * @param rename
	 * @return 
	 */
	private String createFileName(String originalName, boolean rename) {
		String fileName = null;
		if (rename) {
			int index = originalName.lastIndexOf(".");
			String ext = originalName.substring(index + 1);
			fileName = UUID.randomUUID().toString() + "." + ext;
		} else {
			fileName = UUID.randomUUID().toString() + "/" + originalName;
		}
		return fileName;
	}

	/**
	 * 删除文件
	 * @param bucketName
	 * @param accessUrl
	 * @param src
	 * @throws Exception 
	 */
	public void delete(String bucketName, String accessUrl, String src) throws Exception {
		accessUrl = accessUrl.replace("{bucketName}", bucketName) + "/";
		String key = src.replace(accessUrl, "");
		ossClient = getOSSClient();
		ossClient.doesObjectExist(bucketName, key);

		getOSSClient().deleteObject(bucketName, key);
	}

	/**
	 * 获取文件
	 * @param bucketName
	 * @param accessUrl
	 * @param src
	 * @return 
	 */
	public InputStream getFile(String bucketName, String accessUrl, String src) {
		accessUrl = accessUrl.replace("{bucketName}", bucketName) + "/";
		String key = src.replace(accessUrl, "");

		InputStream is = null;
		try {
			OSSObject object = getOSSClient().getObject(bucketName, key);
			is = object.getObjectContent();
		} catch (Exception e) {
			logger.error("[OSS]: 获取文件失败", e);
		}
		return is;
	}

}
