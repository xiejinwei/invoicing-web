package com.invoicing.utils;


import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件处理类
 * 
 * @author YuYouhua 2013年12月11日
 */
@Component
public class FileUtilFs {
	
	@Autowired
	private FtpUtilFs ftpUtilFs;

	// 复制文件项目路径下
	public void uploadToTemp(String uploadPath,MultipartFile file, String fileName) {
		// 目录分割符File.separator
		File dest = new File(uploadPath+File.separator+fileName);
		if (!dest.getParentFile().exists()) {
			dest.getParentFile().mkdirs();
		}
		if (dest.exists()) {
			dest.delete();
		}
		try {
			FileUtils.copyInputStreamToFile(file.getInputStream(), dest);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// 从指定目录路径上传到ftp
	public String uploadToFtp(String uploadPath,String imgUrl, String ftpPath){
		StringBuffer sbf = new StringBuffer();
		String[] imgs =  imgUrl.split("\\|");
		for(int i=1;i<=imgs.length;i++){
			// 如果当前imgurl部位空并且不是HTTP网络图片
				sbf.append(ftpUtilFs.uploadLocalFile(uploadPath+File.separator+imgs[i-1], ftpPath));
				if(i != imgs.length){
					sbf.append("|");
				}
		}
		System.out.println("+++++    +++++++ "+sbf.toString());
		return sbf.toString();
	}

	// 清空tomcat文件上传的目录
	// String uploadPath =
	// request.getSession().getServletContext().getRealPath("/upload");
	public void clearDirectory(String uploadPath) {
		File file = new File(uploadPath);
		File[] files = file.listFiles();
		if (null == files)
			return;
		for (int i = 0; i < files.length; i++) {
			files[i].delete();
		}
	}
	
	/**   
     * 删除单个文件   
     * @param   fileName    被删除文件的文件名   
     * @return 单个文件删除成功返回true,否则返回false   
     */    
    public boolean deleteFile(String uploadPath, String fileName){     
        File file = new File(uploadPath+File.separator+fileName);     
        if(file.isFile() && file.exists()){ // success
            file.delete();     
            return true;     
        }else{ // faild
            return false;     
        }
    }
	
    public String uploadToFTPService(String path){
    	File file = new File(path);
    	String url = ftpUtilFs.uploadFile(file);
    	return url;
    }
}
