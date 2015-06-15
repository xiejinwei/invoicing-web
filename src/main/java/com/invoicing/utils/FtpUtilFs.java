package com.invoicing.utils;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FtpUtilFs {
	private static final Integer FTP_PORT = 21;

	@Value(value="${file.ftp.host}")
	private	String ftpHost;

	@Value(value="${file.ftp.user}")
	private String ftpUser;
	
	@Value(value="${file.ftp.password}")
	private String ftpPassword;
	
	@Value(value="${file.http.prefix}")
	private String fileHttpPrefix;
		
    private static FTPClient ftp;

	private void createFTPConnect(boolean isUpload){
		ftp = new FTPClient();
		try {
			//ftp.setControlEncoding("UTF-8");
			ftp.connect(ftpHost, FTP_PORT); // 连接
			ftp.login(ftpUser, ftpPassword); // 登录				
			// 查看连接状态
//			int reply = ftp.getReplyCode();
//			if (!FTPReply.isPositiveCompletion(reply)) {
//				ftp.disconnect();
//				System.err.println("FTP服务器拒绝连接。");
//			}
			ftp.enterLocalPassiveMode(); // 设置它为被动模式
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
		} catch (IOException e) {
			System.err.println("登录ftp服务器【" + ftpHost + "】失败");
			e.printStackTrace();
		}
		//log.info("ftp连接成功");
	}
	
	private void closeFTPConnect(){
		 if (null != ftp && ftp.isConnected()) {  
	            try {  
	                boolean reuslt = ftp.logout();// 退出FTP服务器  
	                if (reuslt) {  
	                   // log.info("成功退出服务器");  
	                }  
	            } catch (IOException e) {  
	                e.printStackTrace();  
	                //log.warn("退出FTP服务器异常！" + e.getMessage());  
	            } finally {  
	                try {  
	                   ftp.disconnect();// 关闭FTP服务器的连接  
	                } catch (IOException e) {  
	                    e.printStackTrace();  
	                   // log.warn("关闭FTP服务器的连接异常！");  
	                }  
	            }  
	        }  
	}
	
	
	
	public boolean downloadFile(String remoteFileName, String localDires,  
            String remoteDownLoadPath) {  
		createFTPConnect(false);
		
        String strFilePath = localDires + remoteFileName;  
        BufferedOutputStream outStream = null;  
        boolean success = false;  
        try {  
            ftp.changeWorkingDirectory(remoteDownLoadPath);  
            outStream = new BufferedOutputStream(new FileOutputStream(  
                    strFilePath));  
            //log.info(remoteFileName + "开始下载....");  
            success = ftp.retrieveFile(remoteFileName, outStream);  
            if (success == true) {  
                //log.info(remoteFileName + "成功下载到" + strFilePath);  
                return success;  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
           // log.error(remoteFileName + "下载失败");  
        } finally {  
            if (null != outStream) {  
                try {  
                    outStream.flush();  
                    outStream.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
        if (success == false) {  
          //  log.error(remoteFileName + "下载失败!!!");  
        }  
        closeFTPConnect();
        return success;  
    }  

    //上传pdf到FTP目录
	public String uploadReportPdf(String pdfURL, String serviceOrder,
			String sampleNO,boolean isSupermarket) {
		createFTPConnect(false);
		try {
			InputStream is = ftp.retrieveFileStream(pdfURL);
//			closeFTPConnect();
			createFTPConnect(true);
			//log.info( "开始上传...."); 
			String httpPath=null;
			/**
			 * SEVERE: Servlet.service() for servlet [rest] in context with path [] threw exception 
			 * [Request processing failed; nested exception is java.lang.ArrayIndexOutOfBoundsException: 2] 
			 * with root cause 
			 * java.lang.ArrayIndexOutOfBoundsException: 2
			 * BUG：数组越界的错误
			 * 修复：设定如果pdfURL拆分成的数组长度小于3，则取最后一个，如果大于或等于3则去第三个
			 */
			String[] pdfURLArr = pdfURL.split("/");
			String source=pdfURLArr.length>2?pdfURLArr[2]:pdfURLArr[pdfURLArr.length-1];
			
			String[] postfix=pdfURL.split("\\.");
			String path="/report/"+source+"/"+serviceOrder+"/";
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			changePath(path, ftp);
			if(isSupermarket){
				ftp.storeFile(sampleNO+"-report"+"."+postfix[postfix.length-1], is);
				httpPath=path+sampleNO+"-report"+"."+postfix[postfix.length-1];
			}else{
				ftp.storeFile(sampleNO+"."+postfix[postfix.length-1], is);	
				httpPath=path+sampleNO+"."+postfix[postfix.length-1];
			}
          //  log.info("上传成功"); 
            return httpPath;
		} catch (IOException e) {
			e.printStackTrace();
			//log.error("上传失败");  
			return null;		
		}finally{
			closeFTPConnect();			
		}
	}
	//切换目录
	public void changePath(String path, FTPClient ftp){
		String[] arraypath = path.split("/");
		int i=0;
		while(i < arraypath.length){
			try {
				ftp.makeDirectory(arraypath[i]);
				ftp.changeWorkingDirectory(arraypath[i]);
			} catch (IOException e) {
				e.printStackTrace();
			}
			i++;
		}
	}
	
   //上传本地图片
	public String uploadLocalFile(String imgUrl, String ftpPath) {
		try {
			System.out.println("+++++++++++++++++ "+imgUrl);
			FileInputStream in = new FileInputStream(new File(imgUrl));  
			//后缀名
			createFTPConnect(true);
			String fileName = imgUrl.substring(imgUrl.lastIndexOf(File.separator)+1);
			//log.info( "开始上传...."); 
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			changePath("/http/portal/img/"+ftpPath+"/", ftp);
			ftp.storeFile(fileName, in);
          //  log.info("上传成功"); 
			System.out.println("+++++++++++++++++++上传成功    " + FTPClient.BINARY_FILE_TYPE);
            closeFTPConnect();
            return fileHttpPrefix+"/"+ftpPath+"/"+fileName;
		} catch (IOException e) {
			e.printStackTrace();
		//	log.error("上传失败");  
		}
		closeFTPConnect();
		return null;		
	}
	
	//上传文件
	public String uploadFile(File file) {
		try {
			FileInputStream in = new FileInputStream(file);
			//后缀名
			createFTPConnect(true);
			String fileName = file.getName();
			//log.info( "开始上传...."); 
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftp.storeFile(fileName, in);
          //  log.info("上传成功"); 
            closeFTPConnect();
            return fileHttpPrefix+"/"+fileName;
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}finally{
			closeFTPConnect();
		}
	}
	
    //上传图片
	public String uploadProductImg(String imgUrl, String barcode, int i) {
		createFTPConnect(false);
		try {
			InputStream is = ftp.retrieveFileStream(imgUrl);
			if (is == null) {
				return null;
			}
		//	closeFTPConnect();
			createFTPConnect(true);
			//log.info( "开始上传...."); 
			
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			changePath("/img/product/"+barcode+"/", ftp);
			ftp.storeFile(barcode+"-"+i+".jpg", is);
           // log.info("上传成功"); 
            return "http://211.151.134.74:8080/portal/img/product/"+barcode+"/"+barcode+"-"+i+".jpg";
		} catch (IOException e) {
			e.printStackTrace();
			//log.error("上传失败");  
			return null;
		}finally{
			closeFTPConnect();			
		}
				
	}

	//下载文件
	public InputStream downloadFileStream(String remotePath) {
		InputStream input = null;
		FTPClient ftpClient = new FTPClient(); 
		ftpClient.setControlEncoding("GBK");
        try { 
        	ftpClient.connect(ftpHost, FTP_PORT);
        	ftpClient.login("gettec", "gettec");
            ftpClient.setBufferSize(1024); 
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE); 
            input = ftpClient.retrieveFileStream(remotePath);  
        } catch (IOException e) { 
            e.printStackTrace(); 
            throw new RuntimeException("FTP client error！", e); 
        }finally { 
            try { 
                ftpClient.disconnect(); 
            } catch (IOException e) { 
                e.printStackTrace(); 
            } 
        }
		return input;
	}
	
	public void deleteFtpFile(String url){
		createFTPConnect(false);
		try {
			ftp.dele(url);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

}
