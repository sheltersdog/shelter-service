package com.sheltersdog.volunte

import com.sheltersdog.volunte.dto.request.GetVolunteersRequest
import com.sheltersdog.volunte.dto.request.PostCrawlingVolunteer
import com.sheltersdog.volunte.dto.response.VolunteeerDto
import com.sheltersdog.volunte.entity.CrawlingVolunteer
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/volunteer")
class VolunteerController @Autowired constructor(val volunteerService: VolunteerService) {

    @GetMapping("/list")
    fun getVolunteers(
        requestMono: Mono<GetVolunteersRequest>
    ): Mono<List<VolunteeerDto>> {
        return requestMono.flatMap {
            volunteerService.getVolunteers(it)
        }
    }

    @PostMapping("/crawling")
    fun postCrawlingVolunteer(@Valid @RequestBody requestMono: Mono<PostCrawlingVolunteer>): Mono<CrawlingVolunteer> {
        return requestMono.flatMap {
            volunteerService.postCrawlingVolunteer(it)
        }
    }


}