package com.coldhamix.clubApp.operations;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import com.coldhamix.clubApp.entities.Group;
import com.coldhamix.clubApp.util.DbHelper;

public class Groups {
	
	private static final String GET_ALL = "SELECT groups.id, groups.lessonId, groups.archive, lessons.title FROM groups INNER JOIN lessons ON lessons.id=groups.lessonId AND lessons.archive=0";
	private static final String MEMBERS = "SELECT id FROM (SELECT DISTINCT studentId FROM members WHERE groupId = ? AND archive = 0) as members INNER JOIN students ON students.id=members.studentId AND students.archive=0";
	
	public static Group[] getAll() {
		try(Statement stmt = DbHelper.getConnection().createStatement()) {
			ResultSet result = stmt.executeQuery(GET_ALL);
			
			ArrayList<Group> st = new ArrayList<>();
			while(result.next()) {
				Group cur = new Group(result.getInt("id"));
				st.add(cur);
			}
			return st.toArray(new Group[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static int membersCount(int groupId) {
		try(PreparedStatement stmt = DbHelper.getConnection().prepareStatement(MEMBERS)) {
			stmt.setInt(1, groupId);
			ResultSet result = stmt.executeQuery();
			
			int members = 0;
			while(result.next()) {
				members++;
			}
			return members;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;

	}

}
