package com.coldhamix.clubApp.entities;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.coldhamix.clubApp.util.DbHelper;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author Vadim A. Khamzin
 * @version 0.1
 * @since 0.1
 */
public class Course {
	
	public static final Course COURSE = new Course(-1);
	
	/**
	 * SQL template for course insertion
	 */
	private static final String INSERT_QUERY = "INSERT INTO [lessons] VALUES (NULL, ?, ?, 0)";

	/**
	 * SQL template for updating a course
	 */
	private static final String UPDATE_QUERY = "UPDATE [lessons] SET [title] = ?, [price] = ? WHERE [id] = ?";

	/**
	 * SQL template for loading a course
	 */
	private static final String LOAD_QUERY = "SELECT * FROM [lessons] WHERE [id] = ?";
	
	/**
	 * SQL template for archiving a course
	 */
	private static final String ARCHIVE_QUERY = "UPDATE [lessons] SET [archive] = ? WHERE [id] = ?";
	
	/**
	 * SQL template for deleting a course
	 */
	private static final String DELETE_QUERY = "DELETE FROM [lessons] WHERE [id] = ?";

	/**
	 * Unique course identifier
	 */
	private SimpleIntegerProperty id = new SimpleIntegerProperty();
	
	/**
	 * Course title
	 */
	private SimpleStringProperty title = new SimpleStringProperty();
	
	/**
	 * A price of one lesson of a course (in Russian rubble)
	 */
	private SimpleFloatProperty price = new SimpleFloatProperty();
	
	/**
	 * Course's archived state
	 */
	private SimpleBooleanProperty archived = new SimpleBooleanProperty();

	/**
	 * Indicates whether this is a newly created course
	 */
	private boolean exists;

	/**
	 * Creates a new course
	 */
	public Course() {
		this.id.set(0);
		exists = false;
	}

	/**
	 * Loads a specified course
	 * @param id Unique course identifier
	 */
	public Course(int id) {
		this.id.set(id);
		
		if(id == -1) {
			title.set("Индивидуальное занятие");
			price.set(0);
			archived.set(false);
		} else {
			exists = load();		
		}
	}
	
	/**
	 * @return Course title
	 */
	public StringProperty titleProperty() {
		return title;
	}
	
	/**
	 * @return A price of one lesson of a course (in Russian rubble)
	 */
	public FloatProperty priceProperty() {
		return price;
	}
	
	/**
	 * @return Course's archived state
	 */
	public BooleanProperty archivedProperty() {
		return archived;
	}
	
	/**
	 * @return
	 */
	public int getId() {
		return id.get();
	}
	
	/**
	 * @return Course title
	 */
	public String getTitle() {
		return title.get();
	}

	/**
	 * @param title Course title
	 */
	public void setTitle(String title) {
		this.title.set(title);
	}
	
	/**
	 * @return A price of one lesson of a course (in Russian rubble)
	 */
	public float getPrice() {
		return price.get();
	}
	
	/**
	 * @param price A price of one lesson of a course (in Russian rubble)
	 */
	public void setPrice(float price) {
		this.price.set(price);
	}
	
	/**
	 * @return Course's archived state
	 */
	public boolean isArchived() {
		return archived.get();
	}

	/**
	 * @param archived Course's archived state
	 */
	public void setArchived(boolean archived) {
		// cannot change the archive state of a non-existing course
		if (!exists) {
			return;
		}
		
		this.archived.set(archived);
		try (PreparedStatement stmt = DbHelper.getConnection().prepareStatement(ARCHIVE_QUERY)) {
			stmt.setBoolean(1, archived);
			stmt.setInt(2, getId());
			stmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Load information about current course from the database
	 * @return whether loading succeeded or not
	 */
	private boolean load() {
		try (PreparedStatement stmt = DbHelper.getConnection().prepareStatement(LOAD_QUERY)) {
			stmt.setInt(1, getId());
			ResultSet response = stmt.executeQuery();
			if(response.next()) {
				id.set(response.getInt("id"));
				title.set(response.getString("title"));
				price.set(response.getFloat("price"));
				archived.set(response.getBoolean("archive"));
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Delete current course from the database
	 */
	public void delete() {
		try (PreparedStatement stmt = DbHelper.getConnection().prepareStatement(DELETE_QUERY)) {
			stmt.setInt(1, getId());
			stmt.executeUpdate();
			exists = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Save current course if it exists or create a new one otherwise
	 */
	public void update() {
		try (PreparedStatement stmt = DbHelper.getConnection().prepareStatement(exists ? UPDATE_QUERY : INSERT_QUERY)) {
			
			stmt.setString(1, getTitle());
			stmt.setFloat(2, getPrice());
			if(exists) {
				stmt.setInt(3, getId());
			}
			stmt.executeUpdate();
			
			// if this course did not exist, get its new id
			if(!exists) {
				ResultSet lastId = stmt.getGeneratedKeys();
				if(lastId.next()) {
					id.set(lastId.getInt(1));
					exists = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*
	 * SECTION END
	 */

}
