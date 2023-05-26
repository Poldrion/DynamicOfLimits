package dynamics.utils;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.List;

public class SpecificationUtils {
    public static Specification<?> containsTextInAttributes(String text, List<String> attributes) {
        if (!text.contains("%")) {
            text = "%" + text + "%";
        }
        String finalText = text;
        return (root, query, builder) -> builder.or(root.getModel().getDeclaredSingularAttributes().stream()
                .filter(a -> attributes.contains(a.getName()))
                .map(a -> builder.like(builder.lower(root.get(a.getName())), finalText.toLowerCase()))
                .toArray(Predicate[]::new)
        );
    }

}
