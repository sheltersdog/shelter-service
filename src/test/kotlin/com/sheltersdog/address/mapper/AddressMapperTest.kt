package com.sheltersdog.address.mapper

import com.sheltersdog.address.entity.Address
import com.sheltersdog.address.model.AddressType
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class AddressMapperTest {

    @MethodSource
    @ParameterizedTest(name = "entity: {0}")
    fun addressToDtoTest(entity: Address) {
        val actualDto = addressToDto(entity)
        assertEquals(entity.id.toString(), actualDto.id)
        assertEquals(entity.type, actualDto.type)
        assertEquals(entity.regionCd, actualDto.regionCode)
        assertEquals(entity.regionName, actualDto.regionName)

        when (actualDto.type) {
            AddressType.SIDO -> {
                assertEquals(entity.sidoCd, actualDto.code)
                assertEquals(entity.sidoName, actualDto.name)
            }
            AddressType.SGG -> {
                assertEquals(entity.sggCd, actualDto.code)
                assertEquals(entity.sggName, actualDto.name)
            }
            AddressType.UMD -> {
                assertEquals(entity.umdCd, actualDto.code)
                assertEquals(entity.umdName, actualDto.name)
            }
            AddressType.RI -> {
                assertEquals(entity.riCd, actualDto.code)
                assertEquals(entity.riName, actualDto.name)
            }
        }
    }

    companion object {
        @JvmStatic
        fun addressToDtoTest(): List<Arguments> {
            val sidoId = ObjectId.get()
            val sggId = ObjectId.get()
            val umdId = ObjectId.get()
            val riId = ObjectId.get()
            return listOf(
                Arguments.of(
                    Address(
                        id = sidoId,
                        type = AddressType.SIDO,
                        regionName = "regionName$sidoId",
                        regionCd = 239487192374L,
                        sidoCd = "23",
                        sidoName = "regionNameSido",
                    ),
                ),
                Arguments.of(
                    Address(
                        id = sggId,
                        type = AddressType.SGG,
                        regionName = "regionName$sggId",
                        regionCd = 239487192374L,
                        sidoCd = "23",
                        sidoName = "regionNameSido",
                        sggCd = "11",
                        sggName = "regionNameSgg",
                    ),
                ),
                Arguments.of(
                    Address(
                        id = umdId,
                        type = AddressType.UMD,
                        regionName = "regionName$umdId",
                        regionCd = 239487192374L,
                        sidoCd = "23",
                        sidoName = "regionNameSido",
                        umdCd = "12",
                        umdName = "regionNameUmd",
                    ),
                ),
                Arguments.of(
                    Address(
                        id = riId,
                        type = AddressType.RI,
                        regionName = "regionName$riId",
                        regionCd = 239487192374L,
                        sidoCd = "23",
                        sidoName = "regionNameSido",
                        riCd = "13",
                        riName = "regionNameRi",
                    ),
                ),
            )
        }
    }
}