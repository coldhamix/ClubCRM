package com.coldhamix.clubApp.entities;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.coldhamix.clubApp.util.DbHelper;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Visit {
	
	private static final String INSERT_QUERY = "INSERT INTO [visits] VALUES (NULL, ?, ?, ?, 0)";
	private static final String UPDATE_QUERY = "UPDATE [visits] SET [studentId] = ?, [groupId] = ?, [timestamp] = ? WHERE [id] = ?";
	private static final String LOAD_QUERY = "SELECT * FROM [visits] WHERE [id] = ?";
	private static final String ARCHIVE_QUERY = "UPDATE [visits] SET [archive] = ? WHERE [id] = ?";
	private static final String DELETE_QUERY = "DELETE FROM [visits] WHERE [id] = ?";
	
	private SimpleIntegerProperty id;
	private SimpleIntegerProperty studentId;
	private SimpleIntegerProperty groupId;
	private SimpleIntegerProperty timestamp;
	private SimpleBooleanProperty archived;
	
	private boolean exists;

	public Visit() {
		this.id.set(0);
		exists = false;
	}

	
	public Visit(int id) {
		this.id.set(id);
		exists = load();
	}
	
	/*
	 * SETTERS & GETTERS
	 */
	public int getId() {
		return id.get();
	}
	
	public int getStudentId() {
		return studentId.get();
	}


	public void setStudentId(int studentId) {
		this.studentId.set(studentId);
	}

	public int getGroupId() {
		return groupId.get();
	}


	public void setGroupId(int groupId) {
		this.groupId.set(groupId);
	}


	public int getTimestamp() {
		return timestamp.get();
	}

	public void setTimestamp(int timestamp) {
		this.timestamp.set(timestamp);
	}

	public boolean isArchived() {
		return archived.get();
	}
	
	public void setArchived(boolean archived) {
		if (!exists) return;
		
		this.archived.set(archived);
		try (PreparedStatement stmt = DbHelper.getConnection().prepareStatement(ARCHIVE_QUERY)) {
			stmt.setBoolean(1, archived);
			stmt.setInt(2, getId());
			stmt.executeUpdate();
		} catch (Exception e) {e.printStackTrace();}

	}
	/*
	 * END SECTION
	 */
	
	/*
	 * PROPERTIES
	 */
	public IntegerProperty studentIdProperty() {
		return studentId;
	}

	public IntegerProperty groupIdProperty() {
		return groupId;
	}
	
	public IntegerProperty timestampProperty() {
		return timestamp;
	}
	
	public BooleanProperty archivedProperty() {
		return archived;
	}
	/*
	 * SECTION END
	 */
	
	/*
	 * CRUD operations
	 */
	private boolean load() {
		try (PreparedStatement stmt = DbHelper.getConnection().prepareStatement(LOAD_QUERY)) {
			stmt.setInt(1, getId());
			ResultSet response = stmt.executeQuery();
			if(response.next()) {
				id.set(response.getInt("id"));
				studentId.set(response.getInt("studentId"));
				groupId.set(response.getInt("groupId"));
				timestamp.set(response.getInt("timestamp"));
				archived.set(response.getBoolean("archive"));
				return true;
			}
		} catch (Exception e) {}
		return false;
	}

	public void delete() {
		try (PreparedStatement stmt = DbHelper.getConnection().prepareStatement(DELETE_QUERY)) {
			stmt.setInt(1, getId());
			stmt.executeUpdate();
			exists = false;
		} catch (Exception e) {e.printStackTrace();}
	}
	
	public void update() {
		try (PreparedStatement stmt = DbHelper.getConnection().prepareStatement(exists ? UPDATE_QUERY : INSERT_QUERY)) {
			if(exists) {
				stmt.setInt(1, getStudentId());
				stmt.setInt(2, getGroupId());
				stmt.setInt(3, getTimestamp());
				stmt.setInt(4, getId());
			} else {
				stmt.setInt(1, getStudentId());
				stmt.setInt(2, getGroupId());
				stmt.setInt(3, getTimestamp());
			}
			stmt.executeUpdate();
			
			if(!exists) {
				ResultSet lastId = stmt.getGeneratedKeys();
				if(lastId.next()) {
					id.set(lastId.getInt(1));
					exists = true;
				}
			}
		} catch (Exception e) {e.printStackTrace();}
	}
	/*
	 * END SECTION
	 */

}
