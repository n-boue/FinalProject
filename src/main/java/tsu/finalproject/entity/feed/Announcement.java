package tsu.finalproject.entity.feed;

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
