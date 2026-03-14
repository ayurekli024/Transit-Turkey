package com.example.kartransit;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface EgoApiService {
    @GET("sorgu/{durak_no}/{hat_no}")
    Call<EgoResponse> getLiveUpdate(
            @Path("durak_no") String durakNo,
            @Path("hat_no") String hatNo
    );
}