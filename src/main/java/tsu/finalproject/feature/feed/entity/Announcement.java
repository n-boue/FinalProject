package tsu.finalproject.feature.feed.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("ANNOUNCEMENT")
@NoArgsConstructor
@SuperBuilder
public class Announcement extends Post {
}
