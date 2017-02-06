package com.linchunsen.classscanner;

import java.io.IOException;
import java.net.URISyntaxException;

import com.linchunsen.classscanner.ClassScanner;

/**
 * <hr>
 * <h2>简介</h2> 类扫描器的测试类
 * <hr>
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
public class CMain {

	public static void main(String[] args) throws IOException, URISyntaxException {
		try {
			System.out.println("包下的类");
			for (Class<?> mClass : ClassScanner.findClasses(CMain.class.getPackage())) {
				System.out.println(mClass.getName());
			}
			System.out.println("包下的包");
			for (String pkg : ClassScanner.findPackageNames(CMain.class.getPackage())) {
				System.out.println(pkg);
			}
			System.out.println("包下的所有类");
			for (Class<?> mClass : ClassScanner.findAllClasses(CMain.class.getPackage())) {
				System.out.println(mClass.getName());
			}
			System.out.println("包下的所有包");
			for (String pkg : ClassScanner.findAllPackageNames(CMain.class.getPackage())) {
				System.out.println(pkg);
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
