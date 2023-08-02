package com.sheltersdog.volunteer

import com.sheltersdog.volunteer.dto.request.GetVolunteerCategoriesRequest
import com.sheltersdog.volunteer.dto.request.GetVolunteersRequest
import com.sheltersdog.volunteer.dto.request.PostVolunteer
import com.sheltersdog.volunteer.dto.response.VolunteerDto
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/volunteer")
class VolunteerController @Autowired constructor(val volunteerService: VolunteerService) {

    @PostMapping
    fun postVolunteer(
        @Valid @RequestBody requestMono: Mono<PostVolunteer>
    ): Mono<VolunteerDto> {
        return requestMono.flatMap { requestBody -> volunteerService.postVolunteer(requestBody) }
    }

    @GetMapping("/list")
    fun getVolunteers(
        requestMono: Mono<GetVolunteersRequest>
    ): Mono<List<VolunteerDto>> {
        return requestMono.flatMap {
            volunteerService.getVolunteers(it)
        }
    }

    @GetMapping("/category/list")
    fun getVolunteerCategories(requestMono: Mono<GetVolunteerCategoriesRequest>): Mono<Array<String>> {
        return requestMono.flatMap {
            volunteerService.getVolunteerCategories(it)
        }
    }


}