package org.roko.nbt.distribution;

public class GetResult<T> {

	public enum GeneralResult {
		ERROR,
		NOT_FOUND,
		FOUND
	}
	
	private GeneralResult generalResult; 
	private T value;

	public GetResult(GeneralResult generalResult) {
		this(generalResult, null);
	}
	
	public GetResult(GeneralResult generalResult, T value) {
		this.generalResult = generalResult;
		this.value = value;
	}

	public GeneralResult getGeneralResult() {
		return generalResult;
	}

	public void setGeneralResult(GeneralResult generalResult) {
		this.generalResult = generalResult;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}
	
}
