package org.util;

import java.util.ArrayList;
import java.util.List;

public class Fact {

	private int factId;
	private String factString;
	private float factValue;
	private NLPTriple triple;

	Fact() {
	}

	Fact(int factId, String factString, float factValue) {
		this.factId = factId;
		this.factString = factString;
		this.factValue = factValue;
	}

	public int getFactId() {
		return factId;
	}

	public void setFactId(int factId) {
		this.factId = factId;
	}

	public String getFactString() {
		return factString;
	}

	public void setFactString(String factString) {
		this.factString = factString;
	}

	public float getFactValue() {
		return factValue;
	}

	public void setFactValue(float factValue) {
		this.factValue = factValue;
	}

	public NLPTriple getTriple() {
		return triple;
	}

	public void setTriple(NLPTriple triple) {
		this.triple = triple;
	}
}
