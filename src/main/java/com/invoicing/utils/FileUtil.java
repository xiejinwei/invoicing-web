package com.invoicing.utils;

import java.io.File;
import java.io.IOException;

import javax.annotation.processing.FilerException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;


/**
 * 文件管理工具 功能：文件上传、文件或文件夹删除 此工具类适用于spring MVC框架执行文件上传 上传前需要commons-fileupload
 * 及commons-io两个JAR包的支持
 * 
 * @author xiejinwei
 *
 */
public class FileUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

	// 默认文件上传路径
	private static final String PATH = "/resources/upload/business/";
	// 验证字符串是否为正确路径名的正则表达式
	private static String matches = "[A-Za-z]:\\\\[^:?\"><*]*";
	// 通过 sPath.matches(matches) 方法的返回值判断是否正确
	// sPath 为路径字符串
	private static boolean flag = false;
	private static File file = null;

	/**
	 * 上传文件并返回绝对路径
	 * 
	 * @param request
	 * @param files
	 * @param path
	 *            默认前缀地址/upload/business/+时间戳
	 * @return
	 * @throws FilerException
	 */
	public static String upladFile(HttpServletRequest request, MultipartFile f) {

		if (f.isEmpty()) {
			return null;
		}

		if (f.getSize() > 1048576) {
			throw new RuntimeException("图片这些麦就整小张家点的了嘛。。。注意！上传文件过大，最多只能上传1M文件");
		}
		/** 得到文件保存目录的真实路径* */
		String logoRealPathDir = request.getSession().getServletContext()
				.getRealPath(PATH);
		/** 根据真实路径创建目录* */
		File logoSaveFile = new File(logoRealPathDir);
		// 检查文件夹是否存在
		if (!logoSaveFile.exists())
			logoSaveFile.mkdirs();
		/** 获取文件的后缀* */
		 String suffix = f.getOriginalFilename().substring(
		 f.getOriginalFilename().lastIndexOf("."));
		// /** 使用UUID生成文件名称* */
		// String logImageName = UUID.randomUUID().toString() + suffix;// 构建文件名称
		/** 拼成完整的文件保存路径加文件* */
		String fileName = logoRealPathDir + "\\" +System.currentTimeMillis()+suffix;
		File file = new File(fileName);
		try {
			f.transferTo(file);
			logger.info("上传文件 "+fileName+" 成功!");
		} catch (IllegalStateException e) {
			deleteDirectory(logoRealPathDir);
			logger.info(e.getMessage());
			throw new RuntimeException("保存文件失败");
		} catch (IOException e) {
			deleteDirectory(logoRealPathDir);
			logger.info(e.getMessage());
			throw new RuntimeException("保存文件失败");
		}
		return fileName;
	}

	
	public static String getServerpath(String srcpath){
		if(srcpath==null || srcpath.length()==0)
			throw new RuntimeException("文件路径有错");
		System.out.println(srcpath);
		return (srcpath.substring(srcpath.indexOf("\\resources"), srcpath.length())).replaceAll("\\\\", "/");
	}
	
	
	/**
	 * 根据路径删除指定的目录或文件，无论存在与否
	 *
	 * @param sPath
	 *            要删除的目录或文件
	 * @return 删除成功返回 true，否则返回 false。
	 */

	public static boolean deleteFolder(String sPath) {
		flag = sPath.matches(matches);
		file = new File(sPath);
		// 判断目录或文件是否存在
		if (!file.exists()) { // 不存在返回 false
			return flag;
		} else {
			// 判断是否为文件
			if (file.isFile()) { // 为文件时调用删除文件方法
				return deleteFile(sPath);
			} else { // 为目录时调用删除目录方法
				return deleteDirectory(sPath);
			}
		}
	}

	/**
	 * 删除单个文件
	 * 
	 * @param sPath
	 *            被删除文件的文件名
	 * @return 单个文件删除成功返回true，否则返回false
	 */
	public static boolean deleteFile(String sPath) {
		flag = false;
		file = new File(sPath);
		// 路径为文件且不为空则进行删除
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}

	/**
	 * 删除目录（文件夹）以及目录下的文件
	 * 
	 * @param sPath
	 *            被删除目录的文件路径
	 * @return 目录删除成功返回true，否则返回false
	 */
	public static boolean deleteDirectory(String sPath) {
		// 如果sPath不以文件分隔符结尾，自动添加文件分隔符
		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);
		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		flag = true;
		// 删除文件夹下的所有文件(包括子目录)
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 删除子文件
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag)
					break;
			} // 删除子目录
			else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
		}
		if (!flag)
			return false;
		// 删除当前目录
		if (dirFile.delete()) {
			return true;
		} else {
			return false;
		}
	}
	
	public static String getFileName(MultipartFile file){
		return file.getOriginalFilename().substring(0,file.getOriginalFilename().lastIndexOf("."));
	}

}
