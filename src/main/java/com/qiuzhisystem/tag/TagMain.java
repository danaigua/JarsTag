package com.qiuzhisystem.tag;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.qiuzhisystem.utils.DbUtil;

public class TagMain {
	private static Logger logger = Logger.getLogger(TagMain.class);
	
	public static void main(String[] args) {
		Connection con = null;
		logger.info("生成tag开始");
		//主要是给百度的蜘蛛看的
		DbUtil dbUtil = new DbUtil();
		try {
			con = dbUtil.getConn();
			logger.info("数据库连接成功");
		}catch(Exception e) {
			logger.error("[异常]:" + e);
			logger.info("数据库连接失败");
		}
		try {
			String sql = "select * from t_jar where tagState = 0";
			PreparedStatement pstmt = con.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				String id = rs.getString("uuid");
				String name = rs.getString("name");
				String[] names = name.replaceAll(".jar", "").split("-");
				for(String n : names) {
					if(n.contains(".")) {
						continue;
					}
					String sql2 = "select * from t_tag where name = ?";
					PreparedStatement pstmt2 = con.prepareStatement(sql2);
					pstmt2.setString(1, n);
					ResultSet rs2 = pstmt2.executeQuery();
					if(!rs2.next()) {
						//不存在，则插入
						String sql3 = "insert into t_tag values (null, ?)";
						PreparedStatement pstmt3 = con.prepareStatement(sql3);
						pstmt3.setString(1, n);
						pstmt3.executeUpdate();
						logger.info("插入标签" + n);
					}
				}
				//更新数据库tagState状态字段 改成1
				String sql4 = "update t_jar set tagState = 1 where uuid = '" + id + "'";
				PreparedStatement pstmt4 = con.prepareStatement(sql4);
				pstmt4.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			dbUtil.closeCon(con);
		} catch (Exception e) {
			logger.error("[异常]:" + e);
		}
		logger.info("生成tag结束");
	}
}
