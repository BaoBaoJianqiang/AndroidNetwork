package jianqiang.com.mylibrary.net;

public class RequestParameter implements java.io.Serializable,
		Comparable<Object> {

	private static final long serialVersionUID = 1274906854152052510L;

	private String name;

	private String value;

	public RequestParameter(final String name, final String value) {
		this.name = name;
		this.value = value;
	}

	public int compareTo(final Object another) {
		int compared;
		/**
		 * 值比较
		 */
		final RequestParameter parameter = (RequestParameter) another;
		compared = name.compareTo(parameter.name);
		if (compared == 0) {
			compared = value.compareTo(parameter.value);
		}
		return compared;
	}

	public boolean equals(final Object o) {
		if (null == o) {
			return false;
		}

		if (this == o) {
			return true;
		}

		if (o instanceof RequestParameter) {
			final RequestParameter parameter = (RequestParameter) o;
			return name.equals(parameter.name) && value.equals(parameter.value);
		}

		return false;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setValue(final String value) {
		this.value = value;
	}
}
