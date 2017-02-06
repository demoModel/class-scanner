package com.linchunsen.classscanner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * <hr>
 * <h2>简介</h2>类扫描器，能够实现的对class的扫描(jar包内也可以)
 * <hr>
 * Copyright © 2017 www.yuanqitec.com All Rights Reserved. <br>
 * 青岛元启智能机器人科技有限公司 版权所有<br>
 * <hr>
 * <table border="1" cellspacing="0" cellpadding="2">
 * <caption><b>文件修改记录</b></caption>
 * <tr>
 * <th>修改日期</th>
 * <th>修改人</th>
 * <th>修改内容</th>
 * </tr>
 * <tbody>
 * <tr>
 * <td>2017年1月22日</td>
 * <td>linchunsen</td>
 * <td>新建文件，并实现基本功能</td>
 * </tr>
 * </tbody>
 * </table>
 */
public class ClassScanner {
	/**
	 * 当前的类路径
	 */
	public static String basePath = System.getProperty("java.class.path").split(":")[0];
	public static File basePathFile = new File(basePath);

	/**
	 * 获取指定包下的类，不包含子包、内部类
	 * 
	 * @return 查找到的类
	 * @param pkg
	 *            目标包
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * 
	 */
	public static List<Class<?>> findClasses(Package pkg) throws ClassNotFoundException, IOException {
		List<Class<?>> classes = new ArrayList<>();
		String pkgName = pkg.getName();
		String pkgPath = pkgName.replace(".", File.separator);
		if (basePathFile.isDirectory()) {// 在文件夹中
			File dir = new File(basePath + File.separator + pkgPath);
			if (dir.exists() && dir.isDirectory()) {
				String[] fileNames = dir.list();
				for (String fileName : fileNames) {
					if (fileName.endsWith(".class") && fileName.indexOf("$") < 0) {
						classes.add(Class.forName(pkgName + "." + fileName.replace(".class", "")));
					}
				}
			}
		} else if (basePathFile.isFile()) {// 位于jar包内
			JarFile jarFile = new JarFile(basePathFile);
			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				JarEntry jarEntry = entries.nextElement();
				String name = jarEntry.getName();
				if (name.startsWith(pkgPath) && name.endsWith(".class") && name.indexOf("$") < 0) {
					String className = name.replace(File.separator, ".");
					className = className.substring(0, className.length() - 6);
					String classShortName=className.replaceFirst(pkgName+".", "");
					if (classShortName.indexOf(".")<0) {//类直接位于要查询的包下
						classes.add(Class.forName(className));
					}
				}
			}
			jarFile.close();
		}
		return classes;
	}

	/**
	 * 获取指定包下的包的包名,不包含子包中的包
	 * 
	 * @return 查找到的包的包名
	 * @param pkg
	 *            目标包
	 * @author linchunsen
	 * @throws IOException
	 */
	public static List<String> findPackageNames(Package pkg) throws IOException {
		List<String> packages = new ArrayList<>();
		String pkgName = pkg.getName();
		String pkgPath = pkgName.replace(".", File.separator);
		if (basePathFile.isDirectory()) {// 在文件夹中
			File dir = new File(basePath + File.separator + pkgPath);
			String[] fileNames = dir.list();
			for (String fileName : fileNames) {
				File file = new File(basePath + File.separator + pkgPath + File.separator + fileName);
				if (file.isDirectory()) {
					packages.add(pkgName + "." + fileName);
				}
			}
		} else if (basePathFile.isFile()) {// 位于jar包内
			JarFile jarFile = new JarFile(basePathFile);
			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				JarEntry jarEntry = entries.nextElement();
				String name = jarEntry.getName();
				if (name.startsWith(pkgPath)&&!name.equals(pkgPath+"/") && name.indexOf(".") < 0) {
					// 去除末尾的"/"
					String packageName = name.substring(0, name.length() - 1);
					// 将"/"转换为"."
					packageName = packageName.replace(File.separator, ".");
					String packageShortName=packageName.replaceFirst(pkgName+".", "");
					if (packageShortName.indexOf(".")<0) {//包直接位于要查询的包下
						packages.add(packageName);
					}
				}
			}
			jarFile.close();
		}
		return packages;
	}

	/**
	 * 获取指定包名下的类，包含子包、不包含内部类
	 * 
	 * @return 查找到的类
	 * @param pkgName
	 *            目标包名
	 * @throws ClassNotFoundException
	 * @throws IOException 
	 * 
	 */
	public static List<Class<?>> findAllClasses(String pkgName) throws ClassNotFoundException, IOException {
		List<Class<?>> classes = new ArrayList<>();
		String pkgPath = pkgName.replace(".", File.separator);
		File dir = new File(basePath + File.separator + pkgPath);
		if (basePathFile.isDirectory()) {// 在文件夹中
			String[] fileNames = dir.list();
			for (String fileName : fileNames) {
				File file = new File(basePath + File.separator + pkgPath + File.separator + fileName);
				if (file.isDirectory()) {
					classes.addAll(findAllClasses(pkgName + "." + fileName));
				} else if (fileName.endsWith(".class") && fileName.indexOf("$") < 0) {
					classes.add(Class.forName(pkgName + "." + fileName.replace(".class", "")));
				}
			}
		} else if (basePathFile.isFile()) {// 位于jar包内
			JarFile jarFile = new JarFile(basePathFile);
			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				JarEntry jarEntry = entries.nextElement();
				String name = jarEntry.getName();
				if (name.startsWith(pkgPath) && name.endsWith(".class") && name.indexOf("$") < 0) {
					String className = name.replace(File.separator, ".");
					className = className.substring(0, className.length() - 6);
					classes.add(Class.forName(className));
				}
			}
			jarFile.close();
		}
		return classes;
	}

	/**
	 * 获取指定包下的所有类，包含子包，但不包含内部类
	 * 
	 * @return 查找到的类
	 * @param pkg
	 *            目标包
	 * @throws ClassNotFoundException
	 * @throws IOException 
	 * 
	 */
	public static List<Class<?>> findAllClasses(Package pkg) throws ClassNotFoundException, IOException {
		List<Class<?>> classes = new ArrayList<>();
		String pkgName = pkg.getName();
		classes.addAll(findAllClasses(pkgName));
		return classes;
	}

	/**
	 * 获取指定包名下的所有包的包名，包含子包中的包
	 * 
	 * @return 查找到的包名
	 * @param pkgName
	 *            目标包名
	 * @throws IOException 
	 */
	public static List<String> findAllPackageNames(String pkgName) throws IOException {
		List<String> packages = new ArrayList<>();
		String pkgPath = pkgName.replace(".", File.separator);
		File dir = new File(basePath + File.separator + pkgPath);
		if (dir.exists() && dir.isDirectory()) {
			String[] fileNames = dir.list();
			for (String fileName : fileNames) {
				File file = new File(basePath + File.separator + pkgPath + File.separator + fileName);
				if (file.isDirectory()) {
					packages.add(pkgName + "." + fileName);
					packages.addAll(findAllPackageNames(pkgName + "." + fileName));
				}
			}
		}else if (basePathFile.isFile()) {// 位于jar包内
			JarFile jarFile = new JarFile(basePathFile);
			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				JarEntry jarEntry = entries.nextElement();
				String name = jarEntry.getName();
				if (name.startsWith(pkgPath) &&!name.equals(pkgPath+"/") && name.indexOf(".") < 0) {
					// 去除末尾的"/"
					String packageName = name.substring(0, name.length() - 1);
					// 将"/"转换为"."
					packageName = packageName.replace(File.separator, ".");
					packages.add(packageName);
				}
			}
			jarFile.close();
		}
		return packages;
	}

	/**
	 * 获取指定包下的所有包的包名,包含子包中的包
	 * 
	 * @return 查找到的包的包名
	 * @param pkg
	 *            目标包
	 * @author linchunsen
	 * @throws IOException 
	 */
	public static List<String> findAllPackageNames(Package pkg) throws IOException {
		List<String> packages = new ArrayList<>();
		String pkgName = pkg.getName();
		packages.addAll(findAllPackageNames(pkgName));
		return packages;
	}
}
