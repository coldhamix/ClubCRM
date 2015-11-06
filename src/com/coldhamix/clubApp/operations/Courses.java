package com.coldhamix.clubApp.operations;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.coldhamix.clubApp.entities.Course;
import com.coldhamix.clubApp.util.DbHelper;

public class Courses {

	private static final String SEARCH_BY_NAME = "SELECT [id] FROM [lessons] WHERE [title] LIKE ? COLLATE NOCASE";
	private static final String GROUPS = "SELECT * FROM [groups] WHERE [lessonId] = ?";
	private static final String MEMBERS = "SELECT DISTINCT studentId FROM (SELECT * FROM groups WHERE lessonId = ?) AS myTable INNER JOIN members ON myTable.id=members.groupId";
	
	public static Course[] getAll() {
		return searchByName("");
	}
	
	public static Course[] searchByName(String title) {
		try(PreparedStatement stmt = DbHelper.getConnection().prepareStatement(SEARCH_BY_NAME)) {
			stmt.setString(1, "%" + title + "%");
			ResultSet result = stmt.executeQuery();
			
			ArrayList<Course> st = new ArrayList<Course>();
			while(result.next()) {
				Course cur = new Course(result.getInt("id"));
				st.add(cur);
			}
			return st.toArray(new Course[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static int totalGroups(int lessonId) {
		try(PreparedStatement stmt = DbHelper.getConnection().prepareStatement(GROUPS)) {
			stmt.setInt(1, lessonId);
			ResultSet result = stmt.executeQuery();
			
			int count = 0;
			while(result.next()) {
				count++;
			}
			return count;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public static int totalStudents(int lessonId) {
		try(PreparedStatement stmt = DbHelper.getConnection().prepareStatement(MEMBERS)) {
			stmt.setInt(1, lessonId);
			ResultSet result = stmt.executeQuery();
			
			int count = 0;
			while(result.next()) {
				count++;
			}
			return count;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
}
