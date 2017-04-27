package jianqiang.com.youngheart.mockdata;

import com.alibaba.fastjson.JSON;

import jianqiang.com.mylibrary.mockdata.MockService;
import jianqiang.com.mylibrary.net.NetworkEntity;
import jianqiang.com.youngheart.entity.Movie;

public class MockMovie extends MockService {
	@Override
	public String getJsonData() {
		Movie movie = new Movie();
		movie.movieId = 123;
		movie.movieName = "WarCraft";

		NetworkEntity response = getSuccessResponse();
		response.result = JSON.toJSONString(movie);
		return JSON.toJSONString(response);
	}
}
