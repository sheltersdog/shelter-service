package com.sheltersdog.volunteer

import com.sheltersdog.volunteer.dto.request.GetVolunteerCategoriesRequest
import com.sheltersdog.volunteer.dto.request.GetVolunteersRequest
import com.sheltersdog.volunteer.dto.request.PostVolunteer
import com.sheltersdog.volunteer.dto.response.VolunteerDto
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/volunteer")
class VolunteerController @Autowired constructor(val volunteerService: VolunteerService) {

    @PostMapping
    suspend fun postVolunteer(
        @Valid @RequestBody requestBody: PostVolunteer
    ): VolunteerDto {
        return volunteerService.postVolunteer(requestBody)
    }

    @GetMapping("/list")
    suspend fun getVolunteers(
        requestParam: GetVolunteersRequest
    ): List<VolunteerDto> {
        return volunteerService.getVolunteers(requestParam)
    }

    @GetMapping("/category/list")
    suspend fun getVolunteerCategories(requestParam: GetVolunteerCategoriesRequest): Array<String> {
        return volunteerService.getVolunteerCategories(requestParam)
    }
}