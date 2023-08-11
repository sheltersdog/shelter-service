package com.sheltersdog.shelter.event

import com.sheltersdog.core.event.EventBus
import com.sheltersdog.shelter.entity.Shelter
import com.sheltersdog.shelter.repository.ShelterRepository
import com.sheltersdog.volunteer.repository.VolunteerRepository
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

data class SaveVolunteerEvent(
    val volunteerId: String,
)

@Component
class SaveVolunteerEventListener @Autowired constructor(
    val shelterRepository: ShelterRepository,
    val volunteerRepository: VolunteerRepository,
) {
    val log: Logger = LoggerFactory.getLogger(this::class.java)

    @PostConstruct
    fun handleSaveVolunteer() {
        CoroutineScope(Dispatchers.Default).launch {
            EventBus.subscribe { event: SaveVolunteerEvent ->
                log.debug("handleSaveVolunteer start: $event")
                CoroutineScope(Dispatchers.IO).launch {
                    val volunteer = volunteerRepository.findById(event.volunteerId)

                    if (volunteer == null) {
                        log.debug("volunteer is not exist. check saveVolunteer event and volunteerId, $event")
                        return@launch
                    }

                    val shelter = volunteer.shelterId?.let { shelterRepository.findById(it) }
                    if (shelter == null) {
                        log.debug("shelter is not exist. check saveVolunteer event and shelter, $volunteer")
                        return@launch
                    }

                    shelterRepository.updateById(
                        id = volunteer.shelterId,
                        updateFields = mapOf(
                            Pair(Shelter::volunteerActiveCount, shelter.volunteerActiveCount + 1),
                            Pair(Shelter::volunteerTotalCount, shelter.volunteerTotalCount + 1)
                        )
                    )
                }
            }
        }
    }
}