package com.atos.search3.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Searchresults {

	private final SimpleStringProperty name;
	private final SimpleStringProperty type;
	private final SimpleIntegerProperty no;

//	private Searchresults() {
//		this(null, null, 0);
//	}

	public Searchresults(String name, String type, int no) {

		this.name = new SimpleStringProperty(name);
		this.type = new SimpleStringProperty(type);
		this.no = new SimpleIntegerProperty(no);
		
		
	}

	public SimpleStringProperty NameProperty() {
		return name;
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public SimpleStringProperty TypeProperty() {
		return type;

	}

	public void setType(String type) {
		this.type.set(type);
	}

	public SimpleIntegerProperty NoProperty() {
		return no;
	}

	public void setNo(Integer no) {
		this.no.set(no);
	}

	public String getName() {
		return name.get();
	}

	public String getType() {
		return type.get();
	}

	public int getNo() {
		return no.get();
	}

}
