package com.sheltersdog.volunteer

import com.sheltersdog.core.util.notStringThrow
import com.sheltersdog.volunteer.dto.request.GetVolunteerCategoriesRequest
import com.sheltersdog.volunteer.dto.request.GetVolunteersRequest
import com.sheltersdog.volunteer.dto.request.PostVolunteer
import com.sheltersdog.volunteer.dto.request.PutVolunteer
import com.sheltersdog.volunteer.dto.response.VolunteerDto
import jakarta.validation.Valid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/volunteer")
class VolunteerController @Autowired constructor(val volunteerService: VolunteerService) {
    val log: Logger = LoggerFactory.getLogger(this::class.java)

    @PostMapping
    suspend fun postVolunteer(
        @Valid @RequestBody requestBody: PostVolunteer,
    ): VolunteerDto {
        return volunteerService.postVolunteer(requestBody)
    }

    @PutMapping
    suspend fun putVolunteer(
        @Valid @RequestBody requestBody: PutVolunteer
    ): VolunteerDto? {
        return volunteerService.putVolunteer(requestBody)
    }

    @PutMapping("/list/status")
    suspend fun putAllVolunteerStatusByShelterId(@RequestBody requestBody: Map<String, Any>): String {
        val shelterId = requestBody.notStringThrow("shelterId")
        volunteerService.putAllVolunteerStatusByShelterId(shelterId = shelterId)
        return ""
    }

    @GetMapping("/list")
    suspend fun getVolunteers(
        requestParam: GetVolunteersRequest,
    ): List<VolunteerDto> {
        return volunteerService.getVolunteers(requestParam)
    }

    @GetMapping("/category/list")
    suspend fun getVolunteerCategories(requestParam: GetVolunteerCategoriesRequest): Array<String> {
        return volunteerService.getVolunteerCategories(requestParam)
    }
}