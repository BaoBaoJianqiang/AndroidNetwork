package jianqiang.com.mylibrary.mockdata;

import jianqiang.com.mylibrary.net.NetworkEntity;

public abstract class MockService {
	public abstract String getJsonData();
	
	public NetworkEntity getSuccessResponse() {
		NetworkEntity response = new NetworkEntity();
		response.isError = 0;
		response.errorType = 0;
		response.errorMessage = "";

		return response;
	}

	public NetworkEntity getFailResponse(int errorType, String errorMessage) {
		NetworkEntity response = new NetworkEntity();
		response.isError = 1;
		response.errorType = errorType;
		response.errorMessage = errorMessage;

		return response;
	}
}
