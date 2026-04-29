package com.ruoyi.web.controller.common;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.config.ServerConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.constant.S3Properties;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 通用请求处理
 * 
 * @author ruoyi
 */
@Controller
@RequestMapping("/common")
public class CommonController
{
    private static final Logger log = LoggerFactory.getLogger(CommonController.class);

    @Autowired
    private ServerConfig serverConfig;

    private static final String FILE_DELIMETER = ",";

    @Autowired(required = false)
    private S3Client s3Client;

    @Autowired
    private S3Properties props;

    /**
     * 通用下载请求
     * 
     * @param fileName 文件名称
     * @param delete 是否删除
     */
    @GetMapping("/download")
    public void fileDownload(String fileName, Boolean delete, HttpServletResponse response, HttpServletRequest request)
    {
        try
        {
            if (!FileUtils.checkAllowDownload(fileName))
            {
                throw new Exception(StringUtils.format("文件名称({})非法，不允许下载。 ", fileName));
            }
            String realFileName = System.currentTimeMillis() + fileName.substring(fileName.indexOf("_") + 1);
            String filePath = RuoYiConfig.getDownloadPath() + fileName;

            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUtils.setAttachmentResponseHeader(response, realFileName);
            FileUtils.writeBytes(filePath, response.getOutputStream());
            if (delete)
            {
                FileUtils.deleteFile(filePath);
            }
        }
        catch (Exception e)
        {
            log.error("下载文件失败", e);
        }
    }

    /**
     * 通用上传请求（单个）- 直接上传到 S3
     */
    @PostMapping("/upload")
    @ResponseBody
    public AjaxResult uploadFile(MultipartFile file) throws Exception
    {
        try {
            if (s3Client == null || StringUtils.isEmpty(props.getBucket()) || StringUtils.isEmpty(props.getRegion())) {
                return AjaxResult.error("未配置 S3：请设置 aws.s3.region、bucket");
            }
            // 1. 生成唯一文件名
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            String objectKey = "moving/" + fileName;

            // 2. 直接使用 S3Client 上传文件
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(props.getBucket())
                    .key(objectKey)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            // 3. 返回访问URL
            String fileUrl = "https://" + props.getBucket() + ".s3." + props.getRegion() + ".amazonaws.com/" + objectKey;
            
            AjaxResult ajax = AjaxResult.success();
            ajax.put("url", fileUrl);
            ajax.put("fileName", fileName);
            ajax.put("newFileName", FileUtils.getName(fileName));
            ajax.put("originalFilename", file.getOriginalFilename());
            return ajax;
        } catch (Exception e) {
            log.error("S3上传失败", e);
            return AjaxResult.error("上传失败: " + e.getMessage());
        }
    }

    /**
     * 通用上传请求（多个）- 直接上传到 S3
     */
    @PostMapping("/uploads")
    @ResponseBody
    public AjaxResult uploadFiles(List<MultipartFile> files) throws Exception
    {
        try
        {
            if (s3Client == null || StringUtils.isEmpty(props.getBucket()) || StringUtils.isEmpty(props.getRegion())) {
                return AjaxResult.error("未配置 S3：请设置 aws.s3.region、bucket");
            }
            List<String> urls = new ArrayList<String>();
            List<String> fileNames = new ArrayList<String>();
            List<String> newFileNames = new ArrayList<String>();
            List<String> originalFilenames = new ArrayList<String>();
            
            for (MultipartFile file : files)
            {
                // 1. 生成唯一文件名
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                String objectKey = "moving/" + fileName;

                // 2. 直接使用 S3Client 上传文件
                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket(props.getBucket())
                        .key(objectKey)
                        .contentType(file.getContentType())
                        .contentLength(file.getSize())
                        .build();

                s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

                // 3. 构建访问URL
                String url = "https://" + props.getBucket() + ".s3." + props.getRegion() + ".amazonaws.com/" + objectKey;
                urls.add(url);
                fileNames.add(fileName);
                newFileNames.add(FileUtils.getName(fileName));
                originalFilenames.add(file.getOriginalFilename());
            }
            
            AjaxResult ajax = AjaxResult.success();
            ajax.put("urls", StringUtils.join(urls, FILE_DELIMETER));
            ajax.put("fileNames", StringUtils.join(fileNames, FILE_DELIMETER));
            ajax.put("newFileNames", StringUtils.join(newFileNames, FILE_DELIMETER));
            ajax.put("originalFilenames", StringUtils.join(originalFilenames, FILE_DELIMETER));
            return ajax;
        }
        catch (Exception e)
        {
            log.error("S3批量上传失败", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 本地资源通用下载
     */
    @GetMapping("/download/resource")
    public void resourceDownload(String resource, HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        try
        {
            if (!FileUtils.checkAllowDownload(resource))
            {
                throw new Exception(StringUtils.format("资源文件({})非法，不允许下载。 ", resource));
            }
            // 本地资源路径
            String localPath = RuoYiConfig.getProfile();
            // 数据库资源地址
            String downloadPath = localPath + StringUtils.substringAfter(resource, Constants.RESOURCE_PREFIX);
            // 下载名称
            String downloadName = StringUtils.substringAfterLast(downloadPath, "/");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUtils.setAttachmentResponseHeader(response, downloadName);
            FileUtils.writeBytes(downloadPath, response.getOutputStream());
        }
        catch (Exception e)
        {
            log.error("下载文件失败", e);
        }
    }
}
