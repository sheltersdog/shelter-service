package com.sheltersdog.address

import com.sheltersdog.address.dto.AddressDto
import com.sheltersdog.address.dto.KakaoDocument
import com.sheltersdog.address.model.AddressType
import com.sheltersdog.address.model.getPropertyCode
import com.sheltersdog.address.model.getPropertyName
import com.sheltersdog.address.repository.AddressRepository
import com.sheltersdog.core.exception.ExceptionType
import com.sheltersdog.core.exception.SheltersdogException
import com.sheltersdog.core.properties.KakaoProperties
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class AddressService @Autowired constructor(
    val kakaoProperties: KakaoProperties,
    val addressRepository: AddressRepository,
    val webClient: WebClient,
) {

//    @PostConstruct
//    fun init() {
//        val addresses = mutableListOf<Address>()
//        csvReader {
//        }.open("/Users/mysend/유반/법정동.csv") {
//            readAllAsSequence().forEach { row: List<String> ->
//                if (row[7].isNotBlank()) return@forEach
//                if (row[7].isBlank()) {
//                    val code = row[0]
//                    if (code.substring(2).toInt() == 0) {
//                        addresses.add(
//                            Address(
//                                regionCd = code.toLong(),
//                                regionName = row[1],
//                                sidoCd = code.substring(0, 2),
//                                sidoName = row[1],
//                                createdDate = yyyyMMddToLocalDate(row[6])
//                            )
//                        )
//                    } else if (code.substring(5).toInt() == 0) {
//                        addresses.add(
//                            Address(
//                                type = AddressType.SGG,
//                                regionCd = code.toLong(),
//                                regionName = "${row[1]} ${row[2]}",
//                                sidoCd = code.substring(0, 2),
//                                sidoName = row[1],
//                                sggCd = code.substring(2, 5),
//                                sggName = row[2],
//                                createdDate = yyyyMMddToLocalDate(row[6])
//                            )
//                        )
//                    } else if (code.substring(8).toInt() == 0) {
//                        addresses.add(
//                            Address(
//                                type = AddressType.UMD,
//                                regionCd = code.toLong(),
//                                regionName = "${row[1]} ${row[2]} ${row[3]}",
//                                sidoCd = code.substring(0, 2),
//                                sidoName = row[1],
//                                sggCd = code.substring(2, 5),
//                                sggName = row[2],
//                                umdCd = code.substring(5, 8),
//                                umdName = row[3],
//                                createdDate = yyyyMMddToLocalDate(row[6])
//                            )
//                        )
//                    } else {
//                        addresses.add(
//                            Address(
//                                type = AddressType.RI,
//                                regionCd = code.toLong(),
//                                regionName = "${row[1]} ${row[2]} ${row[3]} ${row[4]}",
//                                sidoCd = code.substring(0, 2),
//                                sidoName = row[1],
//                                sggCd = code.substring(2, 5),
//                                sggName = row[2],
//                                umdCd = code.substring(5, 8),
//                                umdName = row[3],
//                                riCd = code.substring(8),
//                                riName = row[4],
//                                createdDate = yyyyMMddToLocalDate(row[6])
//                            )
//                        )
//                    }
//                }
//            }
//        }
//
//        addresses.forEach { println(it) }
//        addressRepository.saveAll(addresses)
//    }

    suspend fun getKakaoAddressByKeyword(
        analyzeType: String,
        page: Int,
        size: Int,
        keyword: String,
    ): KakaoDocument {
        val uri =
            "https://dapi.kakao.com/v2/local/search/address.json?analyze_type=${analyzeType}&page=${page}&size=${size}&query=${keyword}"

        return webClient.get()
            .uri(uri)
            .header("Authorization", "KakaoAK ${kakaoProperties.apiKey}")
            .accept(MediaType.APPLICATION_JSON)
            .exchangeToMono { it.bodyToMono(KakaoDocument::class.java) }
            .awaitFirstOrNull()
            ?: throw SheltersdogException(
                type = ExceptionType.NOT_FOUND_KAKAO_DOCUMENT,
                variables = mapOf("url" to uri)
            )
    }

    suspend fun getAddresses(
        type: AddressType,
        parentCode: String,
        keyword: String,
    ): AddressDto {
        val address = addressRepository.getAddresses(
            type, parentCode, keyword
        ) ?: throw SheltersdogException(
            type = ExceptionType.NOT_FOUND_ADDRESS,
            variables = mapOf(
                "type" to type,
                "parentCode" to parentCode,
                "keyword" to keyword
            )
        )

        return AddressDto(
            id = address.id.toString(),
            type = type,
            regionName = address.regionName,
            regionCode = address.regionCd,
            name = type.getPropertyName().get(receiver = address).toString(),
            code = type.getPropertyCode().get(receiver = address).toString(),
        )
    }


}