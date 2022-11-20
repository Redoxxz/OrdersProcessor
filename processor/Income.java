package processor;

public class Income {
	private int value;

	public Income(int startValue) {
		value = startValue;
	}

	public void add(int value) {
		this.value += value;
	}

	public int getValue() {
		return value;
	}
	
	public String toString() {
		return Integer.toString(value);
	}
}

