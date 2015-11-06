package com.coldhamix.clubApp.entities;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.coldhamix.clubApp.operations.Groups;
import com.coldhamix.clubApp.util.DbHelper;

public class Group {
	
	private static final String INSERT_QUERY = "INSERT INTO [groups] VALUES (NULL, ?, 0)";
	private static final String UPDATE_QUERY = "UPDATE [groups] SET [lessonId] = ? WHERE [id] = ?";
	private static final String LOAD_QUERY = "SELECT groups.id, groups.lessonId, groups.archive, lessons.title FROM (SELECT * FROM groups WHERE id = ?) AS groups INNER JOIN lessons ON lessons.id=groups.lessonId";
	private static final String ARCHIVE_QUERY = "UPDATE [groups] SET [archive] = ? WHERE [id] = ?";
	private static final String DELETE_QUERY = "DELETE FROM [groups] WHERE [id] = ?";
	
	private SimpleIntegerProperty id = new SimpleIntegerProperty();
	private SimpleIntegerProperty lessonId = new SimpleIntegerProperty();
	private SimpleBooleanProperty archived = new SimpleBooleanProperty();
	private SimpleStringProperty title = new SimpleStringProperty();
	private SimpleIntegerProperty count = new SimpleIntegerProperty();
	
	private boolean exists;

	public Group() {
		this.id.set(0);
		exists = false;
	}
	
	public Group(int id) {
		this.id.set(id);
		exists = load();
	}
	/*
	 * PROPERTIES
	 */
	public IntegerProperty lessonId() {
		return lessonId;
	}
	
	public BooleanProperty archived() {
		return archived;
	}
	
	public StringProperty title() {
		return title;
	}
	
	public IntegerProperty count() {
		return count;
	}
	
	/*
	 * SECTION END
	 */

	/*
	 * SETTERS & GETTERS
	 */
	public int getId() {
		return id.get();
	}
	
	public int getLessonId() {
		return lessonId.get();
	}

	public void setLessonId(int lessonId) {
		this.lessonId.set(lessonId);
	}
	
	public String getTitle() {
		return title.get();
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
				id.set(response.getInt("groups.id"));
				lessonId.set(response.getInt("groups.lessonId"));
				archived.set(response.getBoolean("groups.archive"));
				title.set(response.getString("lessons.title"));
				
				count.set(Groups.membersCount(getId()));
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
				stmt.setInt(1, getLessonId());
				stmt.setInt(2, getId());
			} else {
				stmt.setInt(1, getLessonId());
			}
			stmt.executeUpdate();
			
			if(!exists) {
				ResultSet lastId = stmt.getGeneratedKeys();
				if(lastId.next()) {
					id.set(lastId.getInt(1));
					exists = true;
				}
			}
			
			load();
		} catch (Exception e) {e.printStackTrace();}
	}
	/*
	 * SECTION END
	 */

}
