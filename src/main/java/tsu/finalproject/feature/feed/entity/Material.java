package tsu.finalproject.feature.feed.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("MATERIAL")
@NoArgsConstructor
@SuperBuilder
public class Material extends Post {
}
