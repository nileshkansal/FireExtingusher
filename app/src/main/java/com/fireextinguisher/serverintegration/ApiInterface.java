package com.fireextinguisher.serverintegration;

import com.google.gson.JsonObject;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("userSignin")
    Call<JsonObject> login(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("userSignup")
    Call<JsonObject> signup(@Field("empId") String empId, @Field("mobile") String mobile, @Field("email") String email,
                            @Field("password") String password);

    @FormUrlEncoded
    @POST("sendOTP")
    Call<JsonObject> sendSms(@Field("mobile") String mobile);

    @FormUrlEncoded
    @POST("sendOTP")
    Call<JsonObject> reSendSms(@Field("mobile") String mobile, @Field("old_otp") String oldOTP);

    @GET("getProduct")
    Call<JsonObject> getProduct();

    @FormUrlEncoded
    @POST("createSiteModelProduct")
    Call<JsonObject> createSiteModelProduct(@Field("empId") String userId,
                                            @Field("modelNo") String modelNo,
                                            @Field("pId") String pId,
                                            @Field("location") String location,
                                            @Field("feNo") String feNo,
                                            @Field("feType") String feType,
                                            @Field("capacity") String capacity,
                                            @Field("mfgYear") String mfgYear,
                                            @Field("emptyCylinderPressure") String emptyCylinderPressure,
                                            @Field("fullCylinderPressure") String fullCylinderPressure,
                                            @Field("netPressure") String netPressure,
                                            @Field("lastDateRefilling") String lastDateRefilling,
                                            @Field("dueDateRefiiling") String dueDateRefiiling,
                                            @Field("lastDateHpt") String lastDateHpt,
                                            @Field("dueDateHpt") String dueDateHp,
                                            @Field("sparePartRequired") String sparePartRequired,
                                            @Field("remarks") String remarks,
                                            @Field("sparePartItemRequired") String sparePartItemRequired,
                                            @Field("clientName") String clientName);

    @FormUrlEncoded
    @POST("getProductByModel")
    Call<JsonObject> getProductByModel(@Field("modelNo") String modelNo, @Field("pId") String pId);

    @FormUrlEncoded
    @POST("createFireHydrant")
    Call<JsonObject> createFireHydrant(@Field("empId") String userId,
                                       @Field("modelNo") String modelNo,
                                       @Field("pId") String pId,
                                       @Field("location") String location,
                                       @Field("sparePartRequired") String sparePartRequired,
                                       @Field("remarks") String remarks,
                                       @Field("sparePartItemRequired") String sparePartItemRequired,
                                       @Field("clientName") String clientName,
                                       @Field("hosePipe") String hosePipe,
                                       @Field("hydrantValve") String hydrantValve,
                                       @Field("blackCap") String blackCap,
                                       @Field("shuntWheel") String shuntWheel,
                                       @Field("hoseBox") String hoseBox,
                                       @Field("hoses") String hoses,
                                       @Field("glasses") String glasses,
                                       @Field("branchPipe") String branchPipe,
                                       @Field("keys") String keys,
                                       @Field("glassHammer") String glassHammer,
                                       @Field("observation") String observation,
                                       @Field("action") String action);

    @FormUrlEncoded
    @POST("getFireHydrant")
    Call<JsonObject> getFireHydrant(@Field("modelNo") String modelNo, @Field("pId") String pId);

    @FormUrlEncoded
    @POST("createHoseReel")
    Call<JsonObject> createHoseReel(@Field("empId") String userId,
                                    @Field("modelNo") String modelNo,
                                    @Field("pId") String pId,
                                    @Field("location") String location,
                                    @Field("sparePartRequired") String sparePartRequired,
                                    @Field("remarks") String remarks,
                                    @Field("sparePartItemRequired") String sparePartItemRequired,
                                    @Field("clientName") String clientName,
                                    @Field("observation") String observation,
                                    @Field("action") String action,
                                    @Field("hoseReel") String hoseReel,
                                    @Field("shutOffNozzel") String shutOffNozzel,
                                    @Field("ballValve") String ballValve,
                                    @Field("jubliClip") String jubliClip,
                                    @Field("connectingRubberHose") String connectingRubberHose);

    @FormUrlEncoded
    @POST("getHoseReel")
    Call<JsonObject> getHoseReel(@Field("modelNo") String modelNo, @Field("pId") String pId);

    @FormUrlEncoded
    @POST("createFireBucket")
    Call<JsonObject> createFireBucket(@Field("empId") String userId,
                                      @Field("modelNo") String modelNo,
                                      @Field("pId") String pId,
                                      @Field("location") String location,
                                      @Field("sparePartRequired") String sparePartRequired,
                                      @Field("remarks") String remarks,
                                      @Field("sparePartItemRequired") String sparePartItemRequired,
                                      @Field("clientName") String clientName,
                                      @Field("observation") String observation,
                                      @Field("action") String action,
                                      @Field("numberOfFireBuckets") String numberOfFireBuckets,
                                      @Field("buckets") String buckets,
                                      @Field("stand") String stand,
                                      @Field("sand") String sand);

    @FormUrlEncoded
    @POST("getFireBucket")
    Call<JsonObject> getFireBucket(@Field("modelNo") String modelNo, @Field("pId") String pId);

    @FormUrlEncoded
    @POST("getModelNo")
    Call<JsonObject> getModelNo(@Field("modelNo") String modelNo, @Field("clientId") String clientId);

    @FormUrlEncoded
    @POST("getAllClientName")
    Call<JsonObject> getAllClientName(@Field("empId") String empId);

    @FormUrlEncoded
    @POST("createFirePump")
    Call<JsonObject> createFirePump(@Field("empId") String userId,
                                    @Field("modelNo") String modelNo,
                                    @Field("pId") String pId,
                                    @Field("location") String location,
                                    @Field("sparePartRequired") String sparePartRequired,
                                    @Field("remarks") String remarks,
                                    @Field("sparePartItemRequired") String sparePartItemRequired,
                                    @Field("clientName") String clientName,
                                    @Field("hp") String hp,
                                    @Field("head") String head,
                                    @Field("kw") String kw,
                                    @Field("pumpNo") String pumpNo,
                                    @Field("pumpType") String pumpType,
                                    @Field("rpm") String rpm,
                                    @Field("motorNo") String motorNo);

    @FormUrlEncoded
    @POST("getFirePump")
    Call<JsonObject> getFirePump(@Field("modelNo") String modelNo, @Field("pId") String pId);


    @FormUrlEncoded
    @POST("updateSiteModelProduct")
    Call<JsonObject> updateFireExtinguisherService(@Field("empId") String userId,
                                                   @Field("modelNo") String modelNo,
                                                   @Field("sparePartRequired") String sparePartRequired,
                                                   @Field("sparePartItemRequired") String sparePartItemRequired,
                                                   @Field("remarks") String remarks);

    @FormUrlEncoded
    @POST("updateSiteModelProduct")
    Call<JsonObject> updateFireExtinguisherRefill(@Field("empId") String userId,
                                                  @Field("modelNo") String modelNo,
                                                  @Field("lastDateRefilling") String lastDateRefilling,
                                                  @Field("dueDateRefiiling") String dueDateRefiiling,
                                                  @Field("remarks") String remarks);


    @FormUrlEncoded
    @POST("updateSiteModelProduct")
    Call<JsonObject> updateFireExtinguisherHPT(@Field("empId") String userId,
                                               @Field("modelNo") String modelNo,
                                               @Field("lastDateHpt") String lastDateHpt,
                                               @Field("dueDateHpt") String dueDateHp,
                                               @Field("remarks") String remarks);

    @FormUrlEncoded
    @POST("updateSiteModelProduct")
    Call<JsonObject> updateFireExtinguisherReplaceOrNonService(@Field("empId") String userId,
                                                               @Field("modelNo") String modelNo,
                                                               @Field("remarks") String remarks,
                                                               @Field("comment") String comment);

    @FormUrlEncoded
    @POST("updateFireHydrant")
    Call<JsonObject> updateFireHydrantService(@Field("empId") String userId,
                                              @Field("modelNo") String modelNo,
                                              @Field("sparePartRequired") String sparePartRequired,
                                              @Field("sparePartItemRequired") String sparePartItemRequired,
                                              @Field("remarks") String remarks);

    @FormUrlEncoded
    @POST("updateFireHydrant")
    Call<JsonObject> updateFireHydrantNonService(@Field("empId") String userId,
                                                 @Field("modelNo") String modelNo,
                                                 @Field("remarks") String remarks,
                                                 @Field("comment") String comment);

    @FormUrlEncoded
    @POST("updateHoseReel")
    Call<JsonObject> updateHoseReelService(@Field("empId") String userId,
                                           @Field("modelNo") String modelNo,
                                           @Field("sparePartRequired") String sparePartRequired,
                                           @Field("sparePartItemRequired") String sparePartItemRequired,
                                           @Field("remarks") String remarks);

    @FormUrlEncoded
    @POST("updateHoseReel")
    Call<JsonObject> updateHoseReelNonService(@Field("empId") String userId,
                                              @Field("modelNo") String modelNo,
                                              @Field("remarks") String remarks,
                                              @Field("comment") String comment);

    @FormUrlEncoded
    @POST("updateFirePump")
    Call<JsonObject> updateFirePumpService(@Field("empId") String userId,
                                           @Field("modelNo") String modelNo,
                                           @Field("sparePartRequired") String sparePartRequired,
                                           @Field("sparePartItemRequired") String sparePartItemRequired,
                                           @Field("remarks") String remarks);

    @FormUrlEncoded
    @POST("updateFirePump")
    Call<JsonObject> updateFirePumpNonService(@Field("empId") String userId,
                                              @Field("modelNo") String modelNo,
                                              @Field("remarks") String remarks,
                                              @Field("comment") String comment);

    @FormUrlEncoded
    @POST("updateFireBucket")
    Call<JsonObject> updateFireBucketService(@Field("empId") String userId,
                                             @Field("modelNo") String modelNo,
                                             @Field("sparePartRequired") String sparePartRequired,
                                             @Field("sparePartItemRequired") String sparePartItemRequired,
                                             @Field("remarks") String remarks);

    @FormUrlEncoded
    @POST("updateFireBucket")
    Call<JsonObject> updateFireBucketNonService(@Field("empId") String userId,
                                                @Field("modelNo") String modelNo,
                                                @Field("remarks") String remarks,
                                                @Field("comment") String comment);

    @FormUrlEncoded
    @POST("updateTrackingStatus")
    Call<JsonObject> updateTrackingStatus(@Field("modelNo") String modelNo, @Field("status") String status,
                                          @Field("empId") String empId, @Field("latitude") String latitude,
                                          @Field("longitude") String longitude);

    @Multipart
    @POST("updateFireBucket")
    Call<JsonObject> updateFireBucketWithImage(@PartMap() Map<String, RequestBody> partMap, @Part MultipartBody.Part file);

    @Multipart
    @POST("updateFirePump")
    Call<JsonObject> updateFirePumpWithImage(@PartMap() Map<String, RequestBody> partMap, @Part MultipartBody.Part file);

    @Multipart
    @POST("updateHoseReel")
    Call<JsonObject> updateHoseReelWithImage(@PartMap() Map<String, RequestBody> partMap, @Part MultipartBody.Part file);

    @Multipart
    @POST("updateFireHydrant")
    Call<JsonObject> updateFireHydrantWithImage(@PartMap() Map<String, RequestBody> partMap, @Part MultipartBody.Part file);

    @Multipart
    @POST("updateSiteModelProduct")
    Call<JsonObject> updateSiteModelProductWithImage(@PartMap() Map<String, RequestBody> partMap, @Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("client/signin")
    Call<JsonObject> clientLogin(@Field("email") String email, @Field("password") String password);

    @Multipart
    @POST("updateFireBucket")
    Call<JsonObject> clientUpdateFireBucketWithImage(@PartMap() Map<String, RequestBody> partMap, @Part MultipartBody.Part file);

    @Multipart
    @POST("updateFirePump")
    Call<JsonObject> clientUpdateFirePumpWithImage(@PartMap() Map<String, RequestBody> partMap, @Part MultipartBody.Part file);

    @Multipart
    @POST("updateHoseReel")
    Call<JsonObject> clientUpdateHoseReelWithImage(@PartMap() Map<String, RequestBody> partMap, @Part MultipartBody.Part file);

    @Multipart
    @POST("updateFireHydrant")
    Call<JsonObject> clientUpdateFireHydrantWithImage(@PartMap() Map<String, RequestBody> partMap, @Part MultipartBody.Part file);

    @Multipart
    @POST("updateSiteModelProduct")
    Call<JsonObject> clientUpdateSiteModelProductWithImage(@PartMap() Map<String, RequestBody> partMap, @Part MultipartBody.Part file);

    @Multipart
    @POST("updateFireBucket")
    Call<JsonObject> clientUpdateFireBucket(@PartMap() Map<String, RequestBody> partMap);

    @Multipart
    @POST("updateFirePump")
    Call<JsonObject> clientUpdateFirePump(@PartMap() Map<String, RequestBody> partMap);

    @Multipart
    @POST("updateHoseReel")
    Call<JsonObject> clientUpdateHoseReel(@PartMap() Map<String, RequestBody> partMap);

    @Multipart
    @POST("updateFireHydrant")
    Call<JsonObject> clientUpdateFireHydrant(@PartMap() Map<String, RequestBody> partMap);

    @Multipart
    @POST("updateSiteModelProduct")
    Call<JsonObject> clientUpdateSiteModelProduct(@PartMap() Map<String, RequestBody> partMap);


    @FormUrlEncoded
    @POST("getPortableMonitors")
    Call<JsonObject> getPortableMonitors(@Field("modelNo") String modelNo, @Field("pId") String pId);

    @Multipart
    @POST("updatePortableMonitors")
    Call<JsonObject> updatePortableMonitorsWithImage(@PartMap() Map<String, RequestBody> partMap, @Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("updatePortableMonitors")
    Call<JsonObject> updatePortableMonitorsService(@Field("empId") String userId,
                                             @Field("modelNo") String modelNo,
                                             @Field("sparePartRequired") String sparePartRequired,
                                             @Field("sparePartItemRequired") String sparePartItemRequired,
                                             @Field("remarks") String remarks);

    @FormUrlEncoded
    @POST("updatePortableMonitors")
    Call<JsonObject> updatePortableMonitorsNonService(@Field("empId") String userId,
                                                               @Field("modelNo") String modelNo,
                                                               @Field("remarks") String remarks,
                                                               @Field("comment") String comment);

    @FormUrlEncoded
    @POST("getFireDetection")
    Call<JsonObject> getFireDetection(@Field("modelNo") String modelNo, @Field("pId") String pId);

    @Multipart
    @POST("updateFireDetection")
    Call<JsonObject> updateFireDetectionWithImage(@PartMap() Map<String, RequestBody> partMap, @Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("updateFireDetection")
    Call<JsonObject> updateFireDetectionService(@Field("empId") String userId,
                                                   @Field("modelNo") String modelNo,
                                                   @Field("sparePartRequired") String sparePartRequired,
                                                   @Field("sparePartItemRequired") String sparePartItemRequired,
                                                   @Field("remarks") String remarks);

    @FormUrlEncoded
    @POST("updateFireDetection")
    Call<JsonObject> updateFireDetectionNonService(@Field("empId") String userId,
                                                      @Field("modelNo") String modelNo,
                                                      @Field("remarks") String remarks,
                                                      @Field("comment") String comment);

    @FormUrlEncoded
    @POST("getSupressionSystem")
    Call<JsonObject> getSupressionSystem(@Field("modelNo") String modelNo, @Field("pId") String pId);

    @Multipart
    @POST("updateSupressionSystem")
    Call<JsonObject> updateSupressionSystemWithImage(@PartMap() Map<String, RequestBody> partMap, @Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("updateSupressionSystem")
    Call<JsonObject> updateSupressionSystemService(@Field("empId") String userId,
                                                @Field("modelNo") String modelNo,
                                                @Field("sparePartRequired") String sparePartRequired,
                                                @Field("sparePartItemRequired") String sparePartItemRequired,
                                                @Field("remarks") String remarks);

    @FormUrlEncoded
    @POST("updateSupressionSystem")
    Call<JsonObject> updateSupressionSystemNonService(@Field("empId") String userId,
                                                   @Field("modelNo") String modelNo,
                                                   @Field("remarks") String remarks,
                                                   @Field("comment") String comment);

    @FormUrlEncoded
    @POST("getControlValve")
    Call<JsonObject> getControlValve(@Field("modelNo") String modelNo, @Field("pId") String pId);

    @Multipart
    @POST("updateControlValve")
    Call<JsonObject> updateControlValveWithImage(@PartMap() Map<String, RequestBody> partMap, @Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("updateControlValve")
    Call<JsonObject> updateControlValveService(@Field("empId") String userId,
                                                   @Field("modelNo") String modelNo,
                                                   @Field("sparePartRequired") String sparePartRequired,
                                                   @Field("sparePartItemRequired") String sparePartItemRequired,
                                                   @Field("remarks") String remarks);

    @FormUrlEncoded
    @POST("updateControlValve")
    Call<JsonObject> updateControlValveNonService(@Field("empId") String userId,
                                                      @Field("modelNo") String modelNo,
                                                      @Field("remarks") String remarks,
                                                      @Field("comment") String comment);

}