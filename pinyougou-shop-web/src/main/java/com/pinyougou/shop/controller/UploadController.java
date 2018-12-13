package com.pinyougou.shop.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pinyougou.utils.Result;

import util.FastDFSClient;

/**
 * 文件上传
 * @author DJN
 *
 */
@RestController
public class UploadController {
	
	@Value("${FILE_SERVER_URL}")
	private String FILE_SERVER_URL;
	
	@RequestMapping("upload")
	public Result upload(MultipartFile file) {
		//1.先获取文件名
		String originalFilename = file.getOriginalFilename();
		System.out.println("文件名="+originalFilename);
		//拿到文件的后缀
		String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
		try {
			//客户端
			FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");
			//执行文件上传,上传数据采用二进制
			String path = fastDFSClient.uploadFile(file.getBytes(), extName);
			System.out.println("path=" + path);
			//处理文件的完整路径, 用于前端的回显路径
			String url = FILE_SERVER_URL + path;
			return new Result(true, url);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "上传图片失败");
		}
		
	}

}
