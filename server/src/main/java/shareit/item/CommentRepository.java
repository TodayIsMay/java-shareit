package shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import shareit.item.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
