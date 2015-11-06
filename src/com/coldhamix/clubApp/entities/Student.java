package com.coldhamix.clubApp.entities;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.coldhamix.clubApp.util.DbHelper;

/**
 * @author Vadim A. Khamzin
 * @version 0.1
 * @since 0.1
 */
public class Student {
	
	/**
	 * SQL template for student insertion
	 */
	private static final String INSERT_QUERY = "INSERT INTO [students] VALUES (NULL, ?, ?, ?, ?, ?, 0)";
	
	/**
	 * SQL template for updating a student
	 */
	private static final String UPDATE_QUERY = "UPDATE [students] SET [name] = ?, [birthday] = ?, [phone] = ?, [parentName] = ?, [institution] = ? WHERE [id] = ?";

	/**
	 * SQL template for loading a student
	 */
	private static final String LOAD_QUERY = "SELECT * FROM [students] WHERE [id] = ?";

	/**
	 * SQL template for archiving a student
	 */
	private static final String ARCHIVE_QUERY = "UPDATE [students] SET [archive] = ? WHERE [id] = ?";
	

	/**
	 * SQL template for deleting a student
	 */
	private static final String DELETE_QUERY = "DELETE FROM [students] WHERE [id] = ?";
	
	/**
	 * Unique student identifier
	 */
	private SimpleIntegerProperty id = new SimpleIntegerProperty();
	
	/**
	 * Student's name
	 */
	private SimpleStringProperty name = new SimpleStringProperty();
	
	/**
	 * Student's birthday in UNIX time
	 */
	private SimpleIntegerProperty birthday = new SimpleIntegerProperty();
	
	/**
	 * A phone number to contact a student
	 */
	private SimpleStringProperty phone = new SimpleStringProperty();
	
	/**
	 * Contact person (parent) name
	 */
	private SimpleStringProperty parentName = new SimpleStringProperty();
	
	/**
	 * Educational institution being visited by a student
	 */
	private SimpleStringProperty institution = new SimpleStringProperty();
	
	/**
	 * Student's archived state
	 */
	private SimpleBooleanProperty archived = new SimpleBooleanProperty();
	
	/**
	 * Indicates whether this is a newly created student
	 */
	private boolean exists;

	/**
	 * Creates a new student
	 */
	public Student() {
		this.id.set(0);
		exists = false;
	}
	
	/**
	 * Loads a specified student
	 * @param id student's unique identifier
	 */
	public Student(int id) {
		this.id.set(id);
		exists = load();
	}
	
	/**
	 * @return Student's name property
	 */
	public StringProperty nameProperty() {
		return name;
	}
	
	/**
	 * @return Student's birthday in UNIX time
	 */
	public IntegerProperty birthdayProperty() {
		return birthday;
	}
	
	/**
	 * @return A phone number to contact a student
	 */
	public StringProperty phoneProperty() {
		return phone;
	}
	
	/**
	 * @return Contact person (parent) name
	 */
	public StringProperty parentNameProperty() {
		return parentName;
	}
	
	/**
	 * @return Educational institution being visited by a student
	 */
	public StringProperty institutionProperty() {
		return institution;
	}
	
	/**
	 * @return Student's archived property
	 */
	public BooleanProperty archivedProperty() {
		return archived;
	}
	
	/**
	 * @return Unique student identifier
	 */
	public int getId() {
		return id.get();
	}

	/**
	 * @return Student's name
	 */
	public String getName() {
		return name.get();
	}

	/**
	 * @param name Student's name
	 */
	public void setName(String name) {
		this.name.set(name);
	}
	
	/**
	 * @return Student's birthday in UNIX time
	 */
	public int getBirthday() {
		return birthday.get();
	}

	/**
	 * @param birthday Student's birthday in UNIX time
	 */
	public void setBirthday(int birthday) {
		this.birthday.set(birthday);
	}
	
	/**
	 * @return A phone number to contact a student
	 */
	public String getPhone() {
		return phone.get();
	}
	
	/**
	 * @param phone A phone number to contact a student
	 */
	public void setPhone(String phone) {
		this.phone.set(phone);
	}
	
	/**
	 * @return Contact person (parent) name
	 */
	public String getParentName() {
		return parentName.get();
	}
	
	/**
	 * @param parentName Contact person (parent) name
	 */
	public void setParentName(String parentName) {
		this.parentName.set(parentName);
	}
	/**
	 * @return Educational institution being visited by a student
	 */
	public String getInstitution() {
		return institution.get();
	}
	
	/**
	 * @param institution Educational institution being visited by a student
	 */
	public void setInstitution(String institution) {
		this.institution.set(institution);
	}
	
	/**
	 * @return Student's archived state
	 */
	public boolean isArchived() {
		return archived.get();
	}

	/**
	 * <code>true</code> if should be archived, <code>false</code> otherwise
	 * Note: you can't change the state if this is being applied on a non-existing student
	 * @param archived Student's archived state
	 */
	public void setArchived(boolean archived) {
		// cannot change the archive state of a non-existing student
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
	 * Load information about current user from the database
	 * @return whether loading succeeded or not
	 */
	private boolean load() {
		try (PreparedStatement stmt = DbHelper.getConnection().prepareStatement(LOAD_QUERY)) {
			stmt.setInt(1, getId());
			ResultSet response = stmt.executeQuery();
			
			// set corresponding object' fields if something was selected
			if ( response.next() ) {
				id.set(response.getInt("id"));
				name.set(response.getString("name"));
				birthday.set(response.getInt("birthday"));
				phone.set(response.getString("phone"));
				parentName.set(response.getString("parentName"));
				institution.set(response.getString("institution"));
				archived.set(response.getBoolean("archive"));
				
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Delete current user from the database
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
	 * Save current user if it exists or create a new one otherwise
	 */
	public void update() {
		try (PreparedStatement stmt = DbHelper.getConnection().prepareStatement(exists ? UPDATE_QUERY : INSERT_QUERY)) {

			stmt.setString(1, getName());
			stmt.setInt(2, getBirthday());
			stmt.setString(3, getPhone());
			stmt.setString(4, getParentName());
			stmt.setString(5, getInstitution());
			if (exists) {
				stmt.setInt(6, getId());
			}		
			stmt.executeUpdate();
			
			// if this student did not exist, get its new id
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
}
