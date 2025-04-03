package traintickets.businesslogic.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import traintickets.businesslogic.exception.InvalidEntityException;
import traintickets.businesslogic.model.CommentId;
import traintickets.businesslogic.model.Comment;
import traintickets.businesslogic.model.TrainId;
import traintickets.businesslogic.model.UserId;
import traintickets.businesslogic.repository.CommentRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {
//    @Mock
//    CommentRepository commentRepository;
//
//    @InjectMocks
//    CommentServiceImpl commentService;
//
//    @Test
//    void addComment_positive_saved() {
//        var comment = new Comment(null, new UserId(1), new TrainId(737), 5, "good");
//        commentService.addComment(comment);
//        verify(commentRepository).addComment(comment);
//    }
//
//    @Test
//    void addComment_negative_invalid() {
//        var comment = new Comment(null, new UserId(1), new TrainId(737), 6, "good");
//        assertThrows(InvalidEntityException.class, () -> commentService.addComment(comment));
//        verify(commentRepository, never()).addComment(any());
//    }
//
//    @Test
//    void getComments_positive_got() {
//        var trainId = new TrainId(228);
//        var comm1 = new Comment(new CommentId(1), new UserId(1), trainId, 5, "good");
//        var comm2 = new Comment(new CommentId(2), new UserId(2), trainId, 1, "bad");
//        given(commentRepository.getComments(trainId)).willReturn(List.of(comm1, comm2));
//        var comments = commentService.getComments(trainId);
//        assertNotNull(comments);
//        assertEquals(2, comments.size());
//        assertEquals(comm1, comments.get(0));
//        assertEquals(comm2, comments.get(1));
//    }
//
//    @Test
//    void getComments_positive_empty() {
//        var trainId = new TrainId(228);
//        given(commentRepository.getComments(trainId)).willReturn(List.of());
//        var result = commentService.getComments(trainId);
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    void deleteComment_positive_deleted() {
//        var commId = new CommentId(1);
//        commentService.deleteComment(commId);
//        verify(commentRepository).deleteComment(commId);
//    }
}