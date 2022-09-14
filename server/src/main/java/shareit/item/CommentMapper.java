package shareit.item;

import shareit.item.dto.CommentDto;
import shareit.item.model.Comment;

public class CommentMapper {
    public static CommentDto toDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }
}
