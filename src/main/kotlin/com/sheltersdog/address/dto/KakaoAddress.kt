package com.sheltersdog.address.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class KakaoDocument(
    val documents: List<KakaoAddress>,
)

data class KakaoAddress(
    @field:JsonProperty(value = "address_name")
    val addressName: String,
    @field:JsonProperty(value = "address_type")
    val addressType: String,
    val address: KakaoAddressDetail?,
    @field:JsonProperty(value = "road_address")
    val roadAddress: KakaoRoadAddressDetail?,
    val x: Double,
    val y: Double,
)

data class KakaoRoadAddressDetail(
    @field:JsonProperty(value = "address_name")
    val addressName: String,
    @field:JsonProperty(value = "road_name")
    val roadName: String?,
    @field:JsonProperty(value = "underground_yn")
    val undergroundYn: String?,
    @field:JsonProperty(value = "main_building_no")
    val mainBuildingNo: String?,
    @field:JsonProperty(value = "sub_building_no")
    val subBuildingNo: String?,
    @field:JsonProperty(value = "building_name")
    val buildingName: String?,
    @field:JsonProperty(value = "zone_no")
    val zoneNo: String?,

    @field:JsonProperty(value = "region_1depth_name")
    val region1depthName: String?,
    @field:JsonProperty(value = "region_2depth_name")
    val region2depthName: String?,
    @field:JsonProperty(value = "region_3depth_name")
    val region3depthName: String?,

    val x: Double?,
    val y: Double?,
)

data class KakaoAddressDetail(
    @field:JsonProperty(value = "address_name")
    val addressName: String,
    @field:JsonProperty(value = "b_code")
    val bCode: Long,
    @field:JsonProperty(value = "h_code")
    val hCode: Long,
    @field:JsonProperty(value = "main_address_no")
    val mainAddressNo: String?,
    @field:JsonProperty(value = "mountain_yn")
    val mountainYn: String?,

    @field:JsonProperty(value = "region_1depth_name")
    val region1depthName: String?,
    @field:JsonProperty(value = "region_2depth_name")
    val region2depthName: String?,
    @field:JsonProperty(value = "region_3depth_h_name")
    val region3depthHName: String?,
    @field:JsonProperty(value = "region_3depth_name")
    val region3depthName: String?,
    @field:JsonProperty(value = "sub_address_no")
    val subAddressNo: String?,
    val x: Double?,
    val y: Double?,
)