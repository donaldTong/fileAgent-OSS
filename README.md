# fileAgent-OSS
oss文件上传下载

<!-- 封装的oss对象 -->
<bean id="oSSMgrFactory" class="com.****.file.OSSMgrFactory" init-method="init" destroy-method="destroy"/>


配置根路径app.properties
aliyun.AccessKeyId=

aliyun.AccessKeySecret=

aliyun.oss.endpoint=

aliyun.oss.accessUrl=

aliyun.oss.bucketName=
