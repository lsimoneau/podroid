package com.podling.podroid.loader;

public class LoaderResult<T> {
	private Exception exception;
	private T data;

	public LoaderResult(T data) {
		this.data = data;
	}

	public LoaderResult(Exception exception) {
		this.exception = exception;
	}

	public Exception getException() {
		return exception;
	}

	public T getData() {
		return data;
	}

}
