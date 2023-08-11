package com.sheltersdog.shelter.event

import com.sheltersdog.core.event.EventBus
import com.sheltersdog.foreverdog.repository.ForeverdogRepository
import com.sheltersdog.shelter.entity.Shelter
import com.sheltersdog.shelter.repository.ShelterRepository
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

data class SaveForeverdogEvent(val foreverdogId: String)

@Component
class SaveForeverdogEventListener @Autowired constructor(
    val shelterRepository: ShelterRepository,
    val foreverdogRepository: ForeverdogRepository,
) {
    val log: Logger = LoggerFactory.getLogger(this::class.java)

    @PostConstruct
    fun handleSaveForeverdog() {
        CoroutineScope(Dispatchers.Default).launch {
            EventBus.subscribe { event: SaveForeverdogEvent ->
                log.debug("handle handleSaveForeverdog start: $event")
                CoroutineScope(Dispatchers.IO).launch {
                    val foreverdog = foreverdogRepository.findById(event.foreverdogId)

                    if (foreverdog == null) {
                        log.debug("foreverdog is not exist. check saveForeverdog event and foreverdogId, $event")
                        return@launch
                    }

                    val shelter = foreverdog.shelterId?.let { shelterRepository.findById(it) }
                    if (shelter == null) {
                        log.debug("shelter is not exist. check saveForeverdog event and shelter, $foreverdog")
                        return@launch
                    }

                    shelterRepository.updateById(
                        id = shelter.id!!.toString(),
                        updateFields = mapOf(Pair(Shelter::foreverdogCount, shelter.foreverdogCount + 1))
                    )
                }
            }

        }
    }
}
