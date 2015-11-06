package com.coldhamix.clubApp.operations;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.coldhamix.clubApp.entities.Student;
import com.coldhamix.clubApp.util.DbHelper;

public class Students {
	
	private static final String SEARCH_BY_NAME = "SELECT [id] FROM [students] WHERE [name] LIKE ? COLLATE NOCASE";
	private static final String SEARCH_BY_GROUP = "SELECT [studentId] FROM [members] WHERE [groupId] = ?";
	private static final String MEMBERSHIPS_COUNT = "SELECT COUNT(*) FROM [members] WHERE [studentId] = ?";
	private static final String PAYMENTS = "SELECT * FROM [payments] WHERE [studentId] = ?";
	private static final String LESSONS = "SELECT * FROM [visits] WHERE [studentId] = ?";
	
	public static Student[] getAll() {
		return searchByName("");
	}
	
	public static Student[] searchByName(String name) {
		try(PreparedStatement stmt = DbHelper.getConnection().prepareStatement(SEARCH_BY_NAME)) {
			stmt.setString(1, "%" + name + "%");
			ResultSet result = stmt.executeQuery();
			
			ArrayList<Student> st = new ArrayList<Student>();
			while(result.next()) {
				Student cur = new Student(result.getInt("id"));
				st.add(cur);
			}
			return st.toArray(new Student[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Student[] searchByGroup(int groupId) {
		try(PreparedStatement stmt = DbHelper.getConnection().prepareStatement(SEARCH_BY_GROUP)) {
			stmt.setInt(1, groupId);
			ResultSet result = stmt.executeQuery();
			
			ArrayList<Student> st = new ArrayList<Student>();
			while(result.next()) {
				Student cur = new Student(result.getInt("studentId"));
				st.add(cur);
			}
			return st.toArray(new Student[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static int membershipsCount(int studentId) {
		try(PreparedStatement stmt = DbHelper.getConnection().prepareStatement(MEMBERSHIPS_COUNT)) {
			stmt.setInt(1, studentId);
			ResultSet result = stmt.executeQuery();
			
			int count = 0;
			if(result.next()) {
				count = result.getInt(1);
			}
			return count;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public static int totalPayed(int studentId) {
		try(PreparedStatement stmt = DbHelper.getConnection().prepareStatement(PAYMENTS)) {
			stmt.setInt(1, studentId);
			ResultSet result = stmt.executeQuery();
			
			int count = 0;
			while(result.next()) {
				count += result.getInt("payedFor");
			}
			return count;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public static int lessonsVisited(int studentId) {
		try(PreparedStatement stmt = DbHelper.getConnection().prepareStatement(LESSONS)) {
			stmt.setInt(1, studentId);
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
	
	public static int lessonsLeft(int studentId) {
		// TODO: realization
		return totalPayed(studentId) - lessonsVisited(studentId);
	}

}
