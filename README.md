# fileAgent-OSS
oss文件上传下载

1. 依赖包
  <dependency>
    <groupId>com.xiaoma.mesh</groupId>
    <artifactId>fileAgent</artifactId>
    <version>1.0.0-SNAPSHOT</version>
  </dependency>

2.配置根路径yml文件中
    aliyun.oss.accessKeyId=
    aliyun.oss.accessKeySecret=
    aliyun.oss.endpoint=
    aliyun.oss.accessUrl=
    aliyun.oss.bucketName=

3.封装的oss对象（注意更改OSSMgrFactory.init中扫描文件是否正确 classpath*:app.properties)）
    bean id="oSSMgrFactory" class="com.****.file.OSSMgrFactory" init-method="init" destroy-method="destroy"/
    
    通过配置@configuration配置@Bean
    Springboot中如何自定义init-method和destroy-method
    @Configuration
    public class AliyunAccessInitConfig {
    
        @Value("${aliyun.oss.accessKeyId}")
        private String accessKeyId;
    
        @Value("${aliyun.oss.accessKeySecret}")
        private String accessKeySecret;
    
        @Value("${aliyun.oss.endpoint}")
        private String endpoint;
    
        @Value("${aliyun.oss.accessUrl}")
        private String accessUrl;
    
        @Value("${aliyun.oss.bucketName}")
        private String bucketName;
    
    
        @Bean(destroyMethod = "destroy")
        public OSSMgrFactory initBean(){
            OSSMgrFactory ossMgrFactory = new OSSMgrFactory();
            ossMgrFactory.init(accessKeyId, accessKeySecret, endpoint, accessUrl, bucketName);
            return ossMgrFactory;
        }
    }
    
    

    
    
4.FileUtil.uploadFile(FileDto fileDto) 文件上传， 只需要fileBytes和fileName文件名
配置   
    @ApiOperation("上传文件-单个")
    @PostMapping(value = "/upload", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Map<String, String>> uploadImage(MultipartFile file) throws SpiException, IOException {
        if (file == null) {
            return Result.error(BaseCode.ERROR, "文件内容为空");
        }
        Map<String, String> data = new HashMap<>();
        FileDto fileDto = new FileDto();
        fileDto.setFileBytes(file.getBytes());
        fileDto.setFileName(file.getOriginalFilename());
        fileDto.setRename(false);
        String src = FileUtil.uploadFile(fileDto);
        data.put("src", src);
        return Result.success(data);
    }
