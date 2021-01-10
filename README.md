# fileAgent-OSS
oss文件上传下载

2.封装的oss对象（注意更改OSSMgrFactory.init中扫描文件是否正确 classpath*:app.properties)）
    bean id="oSSMgrFactory" class="com.****.file.OSSMgrFactory" init-method="init" destroy-method="destroy"/
    
    通过配置@configuration配置@Bean
    Springboot中如何自定义init-method和destroy-method
    @Configuration
    public class BeanConfig {
        @Bean(destroyMethod = "customDestroy", initMethod = "customInit")
        public SpringLifeCycleBean lifeCycleBean(){
            SpringLifeCycleBean lifeCycleBean = new SpringLifeCycleBean();
            return lifeCycleBean;
        }
    }
    
    
3.配置根路径app.properties
    aliyun.AccessKeyId=
    aliyun.AccessKeySecret=
    aliyun.oss.endpoint=
    aliyun.oss.accessUrl=
    aliyun.oss.bucketName=
    
    
4.FileUtil.uploadFile(FileDto fileDto) 文件上传， 只需要fileBytes和fileName文件名
配置   
