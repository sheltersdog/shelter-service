package com.sheltersdog.address

import com.sheltersdog.address.dto.AddressDto
import com.sheltersdog.address.dto.KakaoDocument
import com.sheltersdog.address.model.AddressType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/address")
class AddressController @Autowired constructor(val addressService: AddressService){

    @GetMapping("/kakao/search")
    fun getAddressByKeyword(
        @RequestParam(name = "analyzeType", required = false, defaultValue = "similar") analyzeType: String,
        @RequestParam(name = "page", required = false, defaultValue = "1") page: Int,
        @RequestParam(name = "size", required = false, defaultValue = "10") size: Int,
        @RequestParam("keyword") keyword: String): Mono<KakaoDocument> {
        return addressService.getKakaoAddressByKeyword(
            analyzeType, page, size, keyword
        )
    }

    @GetMapping("/list")
    fun getAddress(
        @RequestParam(name = "type") type: AddressType,
        @RequestParam(name = "parentCode") parentCode: String = "",
        @RequestParam(name = "keyword") keyword: String = ""): Flux<AddressDto> {
        return addressService.getAddresses(
            type, parentCode, keyword
        )
    }


}