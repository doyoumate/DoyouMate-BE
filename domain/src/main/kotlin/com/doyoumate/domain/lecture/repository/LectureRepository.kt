package com.doyoumate.domain.lecture.repository

import com.doyoumate.domain.lecture.model.Filter
import com.doyoumate.domain.lecture.model.Lecture
import org.springframework.data.mongodb.repository.Aggregation
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface LectureRepository : ReactiveMongoRepository<Lecture, String> {
    @Aggregation(
        pipeline = [
            """
            { 
                ${'$'}group: { 
                    _id: null, 
                    year: { ${'$'}addToSet: '${'$'}year' },
                    grade: { ${'$'}addToSet: '${'$'}grade' },
                    semester: { ${'$'}addToSet: '${'$'}semester' },
                    major: { ${'$'}addToSet: '${'$'}major' },
                    credit: { ${'$'}addToSet: '${'$'}credit' },
                    section: { ${'$'}addToSet: '${'$'}section' }
                } 
            }
            """
        ]
    )
    fun getFilter(): Mono<FilterResponse>
}
