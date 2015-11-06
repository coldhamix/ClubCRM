package com.coldhamix.clubApp.operations;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;

import com.coldhamix.clubApp.entities.Member;
import com.coldhamix.clubApp.entities.Student;
import com.coldhamix.clubApp.util.DbHelper;

public class Members {
	
	private static final String GET_ALL = "SELECT * FROM members WHERE groupId = ?";
	
	public static Student[] getAllStudents(int groupId) {
		try(PreparedStatement stmt = DbHelper.getConnection().prepareStatement(GET_ALL)) {
			stmt.setInt(1, groupId);
			ResultSet result = stmt.executeQuery();
			
			LinkedList<Student> l = new LinkedList<>();
			while(result.next()) {
				l.add( new Student( result.getInt("studentId")) );
			}
			
			return l.isEmpty() ? null : l.toArray(new Student[l.size()]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Member[] getAll(int groupId) {
		try(PreparedStatement stmt = DbHelper.getConnection().prepareStatement(GET_ALL)) {
			stmt.setInt(1, groupId);
			ResultSet result = stmt.executeQuery();
			
			LinkedList<Member> l = new LinkedList<>();
			while(result.next()) {
				Member m = new Member( result.getInt("id"));
				l.add(m);
			}
			
			return l.isEmpty() ? null : l.toArray(new Member[l.size()]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void deleteAll(int groupId) {
		Member[] members = getAll(groupId);
		
		if(members != null) {
			for(Member m: members) {
				m.delete();
			}
		}
	}
	
	public static void addAll(int groupId, Student... members) {
		if(members != null && members.length > 0) {
			for(Student s: members) {
				Member m = new Member();
				m.setGroupId(groupId);
				m.setStudentId( s.getId() );
				m.update();
			}
		}
	}

}
